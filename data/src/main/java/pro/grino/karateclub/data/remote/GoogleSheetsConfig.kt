package pro.grino.karateclub.data.remote

import android.util.Log

/**
 * Константы для работы с Google Sheets API
 */
object GoogleSheetsConfig {

    // API ключ для публичного доступа к Google Sheets
    // Для тестирования можно использовать пустой ключ, это позволит приложению запускаться
    // в режиме тестирования без реальных API-запросов
    const val API_KEY = "AIzaSyBYI2vhghRYVV8Zzg5HUTkFZaBCA2KhfHg"

    // ID Google таблицы
    // Здесь должен быть реальный ID вашей Google таблицы
    const val SHEET_ID = "1nRwB4FxCgm8IcGrbW5CzVqCBs_6MZqwkdi16bo4vBGA"

    // Имена листов в таблице
    const val PLAYERS_SHEET = "Players"
    const val GROUPS_SHEET = "Groups"

    // Базовый URL для Google Sheets API
    const val BASE_URL = "https://sheets.googleapis.com/v4/spreadsheets/"

    // Диапазон данных (включая заголовки)
    const val PLAYERS_RANGE = "$PLAYERS_SHEET!A1:H"
    const val GROUPS_RANGE = "$GROUPS_SHEET!A1:G"

    init {
        Log.d("GoogleSheetsConfig", "Инициализация GoogleSheetsConfig")
        Log.d("GoogleSheetsConfig", "API_KEY: $API_KEY")
        Log.d("GoogleSheetsConfig", "SHEET_ID: $SHEET_ID")
        Log.d("GoogleSheetsConfig", "PLAYERS_RANGE: $PLAYERS_RANGE")
        Log.d("GoogleSheetsConfig", "GROUPS_RANGE: $GROUPS_RANGE")
    }
}