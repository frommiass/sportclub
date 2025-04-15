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
import pro.grino.karateclub.domain.model.Group
import pro.grino.karateclub.domain.repository.GroupRepository
import java.io.InputStream
import java.util.*

/**
 * Реализация GroupRepository с использованием сервисного аккаунта
 */
class ServiceAccountGroupRepository(
    private val context: Context
) : GroupRepository {
    private val TAG = "ServiceAccGroupRepo"

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

    override fun getAllGroups(): Flow<List<Group>> = flow {
        withContext(Dispatchers.IO) {
            try {
                Log.d(TAG, "Получение списка всех групп")

                val service = getSheetsService()
                val result = service.spreadsheets().values()
                    .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.GROUPS_RANGE)
                    .execute()

                val values = result.getValues()

                if (values == null || values.isEmpty()) {
                    Log.d(TAG, "Нет данных или пустая таблица")
                    emit(emptyList<Group>())
                    return@withContext
                }

                // Пропускаем первую строку с заголовками
                val groupsList = values.drop(1).map { row ->
                    Group(
                        id = row.getOrNull(0)?.toString() ?: "",
                        name = row.getOrNull(1)?.toString() ?: "",
                        coach = row.getOrNull(2)?.toString() ?: "",
                        level = row.getOrNull(3)?.toString() ?: "",
                        schedule = row.getOrNull(4)?.toString() ?: "",
                        maxCapacity = row.getOrNull(5)?.toString()?.toIntOrNull() ?: 0,
                        currentMembersCount = row.getOrNull(6)?.toString()?.toIntOrNull() ?: 0
                    )
                }

                Log.d(TAG, "Получено ${groupsList.size} групп")
                emit(groupsList)
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при получении списка групп", e)
                emit(emptyList<Group>())
            }
        }
    }

    override suspend fun getGroupById(id: String): Group = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Получение группы по ID: $id")

            val service = getSheetsService()
            val result = service.spreadsheets().values()
                .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.GROUPS_RANGE)
                .execute()

            val values = result.getValues()

            if (values == null || values.isEmpty()) {
                Log.d(TAG, "Нет данных или пустая таблица")
                return@withContext Group("", "", "", "", "")
            }

            // Пропускаем первую строку с заголовками и ищем группу по ID
            val groupRow = values.drop(1).find { row ->
                row.getOrNull(0)?.toString() == id
            }

            return@withContext if (groupRow != null) {
                Group(
                    id = groupRow.getOrNull(0)?.toString() ?: "",
                    name = groupRow.getOrNull(1)?.toString() ?: "",
                    coach = groupRow.getOrNull(2)?.toString() ?: "",
                    level = groupRow.getOrNull(3)?.toString() ?: "",
                    schedule = groupRow.getOrNull(4)?.toString() ?: "",
                    maxCapacity = groupRow.getOrNull(5)?.toString()?.toIntOrNull() ?: 0,
                    currentMembersCount = groupRow.getOrNull(6)?.toString()?.toIntOrNull() ?: 0
                )
            } else {
                Group("", "", "", "", "")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при получении группы по ID", e)
            return@withContext Group("", "", "", "", "")
        }
    }

    override suspend fun addGroup(group: Group) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Добавление группы: ${group.name}")

            val service = getSheetsService()

            val values = listOf(listOf(
                group.id,
                group.name,
                group.coach,
                group.level,
                group.schedule,
                group.maxCapacity.toString(),
                group.currentMembersCount.toString()
            ))

            val body = ValueRange().setValues(values)

            val result = service.spreadsheets().values()
                .append(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.GROUPS_RANGE, body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Log.d(TAG, "Группа успешно добавлена: ${result.updates}")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при добавлении группы", e)
            throw e
        }
    }

    override suspend fun updateGroup(group: Group) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Обновление группы: ${group.id} - ${group.name}")

            // Сначала получаем все строки, чтобы найти индекс нужной
            val service = getSheetsService()
            val result = service.spreadsheets().values()
                .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.GROUPS_RANGE)
                .execute()

            val values = result.getValues()

            if (values == null || values.isEmpty()) {
                Log.e(TAG, "Нет данных или пустая таблица")
                return@withContext
            }

            // Ищем индекс строки с нужным ID (+1 для учета заголовков)
            var rowIndex = -1
            for (i in 1 until values.size) {
                if (values[i].getOrNull(0)?.toString() == group.id) {
                    rowIndex = i + 1 // +1, т.к. в API индексы строк начинаются с 1
                    break
                }
            }

            if (rowIndex == -1) {
                Log.e(TAG, "Группа с ID ${group.id} не найдена")
                return@withContext
            }

            // Формируем диапазон для обновления
            val updateRange = "${GoogleSheetsConfig.GROUPS_SHEET}!A$rowIndex:G$rowIndex"

            val updateValues = listOf(listOf(
                group.id,
                group.name,
                group.coach,
                group.level,
                group.schedule,
                group.maxCapacity.toString(),
                group.currentMembersCount.toString()
            ))

            val body = ValueRange().setValues(updateValues)

            val updateResult = service.spreadsheets().values()
                .update(GoogleSheetsConfig.SHEET_ID, updateRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Log.d(TAG, "Группа успешно обновлена: ${updateResult.updatedCells} ячеек")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при обновлении группы", e)
            throw e
        }
    }

    override suspend fun deleteGroup(id: String) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Удаление группы с ID: $id")

            // Находим строку
            val service = getSheetsService()
            val result = service.spreadsheets().values()
                .get(GoogleSheetsConfig.SHEET_ID, GoogleSheetsConfig.GROUPS_RANGE)
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
                    rowIndex = i + 1 // +1, т.к. в API индексы строк начинаются с 1
                    break
                }
            }

            if (rowIndex == -1) {
                Log.e(TAG, "Группа с ID $id не найдена")
                return@withContext
            }

            // Формируем диапазон для обновления
            val updateRange = "${GoogleSheetsConfig.GROUPS_SHEET}!A$rowIndex:G$rowIndex"

            // Создаем пустую строку (или можно оставить только ID для последующей фильтрации)
            val emptyValues = listOf(listOf("", "", "", "", "", "", ""))

            val body = ValueRange().setValues(emptyValues)

            val updateResult = service.spreadsheets().values()
                .update(GoogleSheetsConfig.SHEET_ID, updateRange, body)
                .setValueInputOption("USER_ENTERED")
                .execute()

            Log.d(TAG, "Группа успешно удалена: ${updateResult.updatedCells} ячеек")
        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при удалении группы", e)
            throw e
        }
    }
}