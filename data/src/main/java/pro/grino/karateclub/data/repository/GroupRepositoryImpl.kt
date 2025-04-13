package pro.grino.karateclub.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.grino.karateclub.data.remote.GoogleSheetsConfig
import pro.grino.karateclub.data.remote.GoogleSheetsService
import pro.grino.karateclub.data.remote.ValueRange
import pro.grino.karateclub.domain.model.Group
import pro.grino.karateclub.domain.repository.GroupRepository

class GroupRepositoryImpl(
    private val sheetsService: GoogleSheetsService
) : GroupRepository {

    // Заголовки колонок в таблице
    private val headers = listOf("ID", "Name", "Coach", "Level", "Schedule", "MaxCapacity", "CurrentMembers")

    override fun getAllGroups(): Flow<List<Group>> = flow {
        try {
            val response = sheetsService.getValues(
                spreadsheetId = GoogleSheetsConfig.SHEET_ID,
                range = GoogleSheetsConfig.GROUPS_RANGE,
                apiKey = GoogleSheetsConfig.API_KEY
            )

            if (response.isSuccessful) {
                val sheetData = response.body()
                val dataRows = sheetData?.values?.drop(1) ?: emptyList() // Пропускаем заголовки

                val groups = dataRows.map { row ->
                    Group(
                        id = row.getOrElse(0) { "" },
                        name = row.getOrElse(1) { "" },
                        coach = row.getOrElse(2) { "" },
                        level = row.getOrElse(3) { "" },
                        schedule = row.getOrElse(4) { "" },
                        maxCapacity = row.getOrElse(5) { "0" }.toIntOrNull() ?: 0,
                        currentMembersCount = row.getOrElse(6) { "0" }.toIntOrNull() ?: 0
                    )
                }

                emit(groups)
            } else {
                emit(emptyList())
            }
        } catch (e: Exception) {
            emit(emptyList())
        }
    }

    override suspend fun getGroupById(id: String): Group {
        val response = sheetsService.getValues(
            spreadsheetId = GoogleSheetsConfig.SHEET_ID,
            range = GoogleSheetsConfig.GROUPS_RANGE,
            apiKey = GoogleSheetsConfig.API_KEY
        )

        if (response.isSuccessful) {
            val sheetData = response.body()
            val dataRows = sheetData?.values?.drop(1) ?: emptyList()

            val groupRow = dataRows.find { row -> row.getOrElse(0) { "" } == id }

            return if (groupRow != null) {
                Group(
                    id = groupRow.getOrElse(0) { "" },
                    name = groupRow.getOrElse(1) { "" },
                    coach = groupRow.getOrElse(2) { "" },
                    level = groupRow.getOrElse(3) { "" },
                    schedule = groupRow.getOrElse(4) { "" },
                    maxCapacity = groupRow.getOrElse(5) { "0" }.toIntOrNull() ?: 0,
                    currentMembersCount = groupRow.getOrElse(6) { "0" }.toIntOrNull() ?: 0
                )
            } else {
                Group("", "", "", "", "")
            }
        } else {
            throw Exception("Failed to get group data")
        }
    }

    override suspend fun addGroup(group: Group) {
        val groupData = listOf(
            group.id,
            group.name,
            group.coach,
            group.level,
            group.schedule,
            group.maxCapacity.toString(),
            group.currentMembersCount.toString()
        )

        val valueRange = ValueRange(
            range = GoogleSheetsConfig.GROUPS_RANGE,
            values = listOf(groupData)
        )

        sheetsService.appendValues(
            spreadsheetId = GoogleSheetsConfig.SHEET_ID,
            range = GoogleSheetsConfig.GROUPS_RANGE,
            apiKey = GoogleSheetsConfig.API_KEY,
            valueRange = valueRange
        )
    }

    override suspend fun updateGroup(group: Group) {
        // Находим индекс группы в таблице
        val response = sheetsService.getValues(
            spreadsheetId = GoogleSheetsConfig.SHEET_ID,
            range = GoogleSheetsConfig.GROUPS_RANGE,
            apiKey = GoogleSheetsConfig.API_KEY
        )

        if (response.isSuccessful) {
            val sheetData = response.body()
            val rows = sheetData?.values ?: emptyList()

            // Ищем индекс строки, учитывая заголовки (индекс 0)
            var rowIndex = -1
            for (i in 1 until rows.size) {
                if (rows[i].getOrElse(0) { "" } == group.id) {
                    rowIndex = i
                    break
                }
            }

            if (rowIndex != -1) {
                // Строка найдена, обновляем данные
                val updateRange = "${GoogleSheetsConfig.GROUPS_SHEET}!A${rowIndex + 1}:G${rowIndex + 1}"

                val groupData = listOf(
                    group.id,
                    group.name,
                    group.coach,
                    group.level,
                    group.schedule,
                    group.maxCapacity.toString(),
                    group.currentMembersCount.toString()
                )

                val valueRange = ValueRange(
                    range = updateRange,
                    values = listOf(groupData)
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

    override suspend fun deleteGroup(id: String) {
        // Note: Google Sheets API не позволяет напрямую удалять строки.
        // Обычно делается через обновление строки пустыми значениями
        // или через Google Apps Script, но это выходит за рамки этого примера.

        // В реальном приложении здесь будет логика удаления
    }
}