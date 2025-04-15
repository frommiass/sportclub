package pro.grino.karateclub.data.repository

import android.content.Context
import android.util.Log
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import pro.grino.karateclub.data.remote.GoogleSheetsConfig
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.repository.PlayerRepository
import java.io.InputStream
import java.util.*

/**
 * Реализация PlayerRepository с использованием сервисного аккаунта
 */
class ServiceAccountPlayerRepository(
    private val context: Context
) : PlayerRepository {
    private val TAG = "ServiceAccRepository"

    // Имя файла с ключом сервисного аккаунта в assets
    private val SERVICE_ACCOUNT_KEY_FILE = "service-account-key.json"

    /**
     * Создаёт экземпляр сервиса Google Sheets API с аутентификацией через сервисный аккаунт
     */
    private suspend fun getSheetsService(): Sheets = withContext(Dispatchers.IO) {
        try {
            // Получаем поток с ключом сервисного аккаунта из assets
            val keyStream: InputStream = context.assets.open(SERVICE_ACCOUNT_KEY_FILE)

            // Создаём учетные данные
            val credentials = GoogleCredentials.fromStream(keyStream)
                .createScoped(Collections.singleton(SheetsScopes.SPREADSHEETS))

            // Создаём HTTP клиент с учетными данными
            val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
            val jsonFactory = GsonFactory.getDefaultInstance()

            // Создаём сервис Google Sheets
            return@withContext Sheets.Builder(httpTransport, jsonFactory, HttpCredentialsAdapter(credentials))
                .setApplicationName("KarateClub")
                .build()
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при создании сервиса Google Sheets", e)
            throw e
        }
    }

    override fun getAllPlayers(): Flow<List<Player>> = flow {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Получение списка всех участников")

                val service = getSheetsService()
                val result = service.spreadsheets().values()
                    .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.PLAYERS_RANGE)
                    .execute()

                val values = result.getValues()

                if (values == null || values.isEmpty()) {
                    Log.d(TAG, "Нет данных или пустая таблица")
                    emit(emptyList<Player>())
                    return@withContext
                }

                // Пропускаем первую строку с заголовками
                val playersList = values.drop(1).map { row ->
                    Player(
                        id = row.getOrNull(0)?.toString() ?: "",
                        name = row.getOrNull(1)?.toString() ?: "",
                        age = row.getOrNull(2)?.toString()?.toIntOrNull() ?: 0,
                        belt = row.getOrNull(3)?.toString() ?: "",
                        group = row.getOrNull(4)?.toString() ?: "",
                        phone = row.getOrNull(5)?.toString() ?: "",
                        email = row.getOrNull(6)?.toString() ?: "",
                        joinDate = row.getOrNull(7)?.toString() ?: ""
                    )
                }

                Log.d(TAG, "Получено ${playersList.size} участников")
                emit(playersList)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при получении списка участников", e)
                emit(emptyList<Player>())
            }
        }
    }

    override suspend fun getPlayerById(id: String): Player = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Получение участника по ID: $id")

            val service = getSheetsService()
            val result = service.spreadsheets().values()
                .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.PLAYERS_RANGE)
                .execute()

            val values = result.getValues()

            if (values == null || values.isEmpty()) {
                Log.d(TAG, "Нет данных или пустая таблица")
                return@withContext Player("", "", 0, "", "")
            }

            // Пропускаем первую строку с заголовками и ищем игрока по ID
            val playerRow = values.drop(1).find { row ->
                row.getOrNull(0)?.toString() == id
            }

            return@withContext if (playerRow != null) {
                Player(
                    id = playerRow.getOrNull(0)?.toString() ?: "",
                    name = playerRow.getOrNull(1)?.toString() ?: "",
                    age = playerRow.getOrNull(2)?.toString()?.toIntOrNull() ?: 0,
                    belt = playerRow.getOrNull(3)?.toString() ?: "",
                    group = playerRow.getOrNull(4)?.toString() ?: "",
                    phone = playerRow.getOrNull(5)?.toString() ?: "",
                    email = playerRow.getOrNull(6)?.toString() ?: "",
                    joinDate = playerRow.getOrNull(7)?.toString() ?: ""
                )
            } else {
                Player("", "", 0, "", "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении участника по ID", e)
            return@withContext Player("", "", 0, "", "")
        }
    }

    override suspend fun addPlayer(player: Player) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Добавление участника: ${player.name}")

            val service = getSheetsService()

            val values = listOf(listOf(
                player.id,
                player.name,
                player.age.toString(),
                player.belt,
                player.group,
                player.phone,
                player.email,
                player.joinDate
            ))

            val body = ValueRange().setValues(values)

            val result = service.spreadsheets().values()
                .append(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.PLAYERS_RANGE, body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Log.d(TAG, "Участник успешно добавлен: ${result.updates}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при добавлении участника", e)
            throw e
        }
    }

    override suspend fun updatePlayer(player: Player) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Обновление участника: ${player.id} - ${player.name}")

            // Сначала получаем все строки, чтобы найти индекс нужной
            val service = getSheetsService()
            val result = service.spreadsheets().values()
                .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.PLAYERS_RANGE)
                .execute()

            val values = result.getValues()

            if (values == null || values.isEmpty()) {
                Log.e(TAG, "Нет данных или пустая таблица")
                return@withContext
            }

            // Ищем индекс строки с нужным ID (+1 для учета заголовков)
            var rowIndex = -1
            for (i in 1 until values.size) {
                if (values[i].getOrNull(0)?.toString() == player.id) {
                    rowIndex = i + 1 // +1 потому что в API индексы строк начинаются с 1
                    break
                }
            }

            if (rowIndex == -1) {
                Log.e(TAG, "Участник с ID ${player.id} не найден")
                return@withContext
            }

            // Формируем диапазон для обновления (например, "Players!A2:H2" для 2-й строки)
            val updateRange = "${GoogleSheetsConfig.PLAYERS_SHEET}!A$rowIndex:H$rowIndex"

            val updateValues = listOf(listOf(
                player.id,
                player.name,
                player.age.toString(),
                player.belt,
                player.group,
                player.phone,
                player.email,
                player.joinDate
            ))

            val body = ValueRange().setValues(updateValues)

            val updateResult = service.spreadsheets().values()
                .update(GoogleSheetsConfig.SHEET_ID, updateRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Log.d(TAG, "Участник успешно обновлен: ${updateResult.updatedCells} ячеек")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении участника", e)
            throw e
        }
    }

    override suspend fun deletePlayer(id: String) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Удаление участника с ID: $id")

            // Находим строку
            val service = getSheetsService()
            val result = service.spreadsheets().values()
                .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.PLAYERS_RANGE)
                .execute()

            val values = result.getValues()

            if (values == null || values.isEmpty()) {
                Log.e(TAG, "Нет данных или пустая таблица")
                return@withContext
            }

            // Ищем индекс строки с нужным ID (+1 для учета заголовков)
            var rowIndex = -1
            for (i in 1 until values.size) {
                if (values[i].getOrNull(0)?.toString() == id) {
                    rowIndex = i + 1 // +1 потому что в API индексы строк начинаются с 1
                    break
                }
            }

            if (rowIndex == -1) {
                Log.e(TAG, "Участник с ID $id не найден")
                return@withContext
            }

            // Формируем диапазон для обновления
            val updateRange = "${GoogleSheetsConfig.PLAYERS_SHEET}!A$rowIndex:H$rowIndex"

            // Создаем пустую строку (или можно оставить только ID для последующей фильтрации)
            val emptyValues = listOf(listOf("", "", "", "", "", "", "", ""))

            val body = ValueRange().setValues(emptyValues)

            val updateResult = service.spreadsheets().values()
                .update(GoogleSheetsConfig.SHEET_ID, updateRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Log.d(TAG, "Участник успешно удален: ${updateResult.updatedCells} ячеек")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении участника", e)
            throw e
        }
    }
}