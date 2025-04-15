package pro.grino.karateclub.data.repository

import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.grino.karateclub.data.remote.GoogleSheetsConfig
import pro.grino.karateclub.data.remote.GoogleSheetsService
import pro.grino.karateclub.data.remote.ValueRange
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.repository.PlayerRepository
import retrofit2.Response

class PlayerRepositoryImpl(
    private val sheetsService: GoogleSheetsService
) : PlayerRepository {

    // Заголовки колонок в таблице
    private val headers = listOf("ID", "Name", "Age", "Belt", "Group", "Phone", "Email", "JoinDate")

    override fun getAllPlayers(): Flow<List<Player>> = flow {
        try {
            val response = sheetsService.getValues(
                spreadsheetId = GoogleSheetsConfig.SHEET_ID,
                range = GoogleSheetsConfig.PLAYERS_RANGE,
                apiKey = GoogleSheetsConfig.API_KEY
            )

            if (response.isSuccessful) {
                val sheetData = response.body()
                val dataRows = sheetData?.values?.drop(1) ?: emptyList() // Пропускаем заголовки

                val players = dataRows.map { row ->
                    Player(
                        id = row.getOrElse(0) { "" },
                        name = row.getOrElse(1) { "" },
                        age = row.getOrElse(2) { "0" }.toIntOrNull() ?: 0,
                        belt = row.getOrElse(3) { "" },
                        group = row.getOrElse(4) { "" },
                        phone = row.getOrElse(5) { "" },
                        email = row.getOrElse(6) { "" },
                        joinDate = row.getOrElse(7) { "" }
                    )
                }

                emit(players)
            } else {
                Log.e("PlayerRepository", "Ошибка получения данных: ${response.errorBody()?.string()}")
                emit(emptyList())
            }
        } catch (e: Exception) {
            Log.e("PlayerRepository", "Исключение при получении данных", e)
            emit(emptyList())
        }
    }

    override suspend fun getPlayerById(id: String): Player {
        val response = sheetsService.getValues(
            spreadsheetId = GoogleSheetsConfig.SHEET_ID,
            range = GoogleSheetsConfig.PLAYERS_RANGE,
            apiKey = GoogleSheetsConfig.API_KEY
        )

        if (response.isSuccessful) {
            val sheetData = response.body()
            val dataRows = sheetData?.values?.drop(1) ?: emptyList()

            val playerRow = dataRows.find { row -> row.getOrElse(0) { "" } == id }

            return if (playerRow != null) {
                Player(
                    id = playerRow.getOrElse(0) { "" },
                    name = playerRow.getOrElse(1) { "" },
                    age = playerRow.getOrElse(2) { "0" }.toIntOrNull() ?: 0,
                    belt = playerRow.getOrElse(3) { "" },
                    group = playerRow.getOrElse(4) { "" },
                    phone = playerRow.getOrElse(5) { "" },
                    email = playerRow.getOrElse(6) { "" },
                    joinDate = playerRow.getOrElse(7) { "" }
                )
            } else {
                Player("", "", 0, "", "")
            }
        } else {
            throw Exception("Failed to get player data")
        }
    }

    override suspend fun addPlayer(player: Player) {
        try {
            Log.d("PlayerRepository", "Подготовка данных для добавления участника: ${player.name}")

            // Формируем данные игрока
            val playerData = listOf(
                player.id,
                player.name,
                player.age.toString(),
                player.belt,
                player.group,
                player.phone,
                player.email,
                player.joinDate
            )

            // Создаем значения для вставки
            val valueRange = ValueRange(
                range = GoogleSheetsConfig.PLAYERS_RANGE,
                values = listOf(playerData)
            )

            Log.d("PlayerRepository", "Отправка запроса в Google Sheets")
            Log.d("PlayerRepository", "Spreadsheet ID: ${GoogleSheetsConfig.SHEET_ID}")
            Log.d("PlayerRepository", "Range: ${GoogleSheetsConfig.PLAYERS_RANGE}")
            Log.d("PlayerRepository", "API Key: ${GoogleSheetsConfig.API_KEY}")

            // Выполняем запрос
            val response = sheetsService.appendValues(
                spreadsheetId = GoogleSheetsConfig.SHEET_ID,
                range = GoogleSheetsConfig.PLAYERS_RANGE,
                apiKey = GoogleSheetsConfig.API_KEY,
                valueRange = valueRange
            )

            // Проверка результата
            if (response.isSuccessful) {
                Log.d("PlayerRepository", "Участник успешно добавлен: ${response.body()}")
            } else {
                Log.e("PlayerRepository", "Ошибка при добавлении участника: ${response.errorBody()?.string()}")
                throw Exception("Не удалось добавить участника. Ошибка: ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e("PlayerRepository", "Исключение при добавлении участника", e)
            throw e
        }
    }

    override suspend fun updatePlayer(player: Player) {
        // Находим индекс игрока в таблице
        val response = sheetsService.getValues(
            spreadsheetId = GoogleSheetsConfig.SHEET_ID,
            range = GoogleSheetsConfig.PLAYERS_RANGE,
            apiKey = GoogleSheetsConfig.API_KEY
        )

        if (response.isSuccessful) {
            val sheetData = response.body()
            val rows = sheetData?.values ?: emptyList()

            // Ищем индекс строки, учитывая заголовки (индекс 0)
            var rowIndex = -1
            for (i in 1 until rows.size) {
                if (rows[i].getOrElse(0) { "" } == player.id) {
                    rowIndex = i
                    break
                }
            }

            if (rowIndex != -1) {
                // Строка найдена, обновляем данные
                val updateRange = "${GoogleSheetsConfig.PLAYERS_SHEET}!A${rowIndex + 1}:H${rowIndex + 1}"

                val playerData = listOf(
                    player.id,
                    player.name,
                    player.age.toString(),
                    player.belt,
                    player.group,
                    player.phone,
                    player.email,
                    player.joinDate
                )

                val valueRange = ValueRange(
                    range = updateRange,
                    values = listOf(playerData)
                )

                sheetsService.updateValues(
                    spreadsheetId = GoogleSheetsConfig.SHEET_ID,
                    range = updateRange,
                    apiKey = GoogleSheetsConfig.API_KEY,
                    valueRange = valueRange
                )
            }
        }
    }

    override suspend fun deletePlayer(id: String) {
        // Note: Google Sheets API не позволяет напрямую удалять строки.
        // Обычно делается через обновление строки пустыми значениями
        // или через Google Apps Script, но это выходит за рамки этого примера.

        // В реальном приложении здесь будет логика удаления
    }
}