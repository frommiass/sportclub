package pro.grino.karateclub.data.remote

/**
 * Константы для работы с Google Sheets API
 */
object GoogleSheetsConfig {
    // API ключ для публичного доступа к Google Sheets
    const val API_KEY = "YOUR_API_KEY"

    // ID Google таблицы
    const val SHEET_ID = "YOUR_SHEET_ID"

    // Имена листов в таблице
    const val PLAYERS_SHEET = "Players"
    const val GROUPS_SHEET = "Groups"

    // Базовый URL для Google Sheets API
    const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets/"

    // Диапазон данных (включая заголовки)
    const val PLAYERS_RANGE = "$PLAYERS_SHEET!A1:H"
    const val GROUPS_RANGE = "$GROUPS_SHEET!A1:G"
}