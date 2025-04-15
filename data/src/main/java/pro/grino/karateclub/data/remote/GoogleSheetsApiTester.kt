package pro.grino.karateclub.data.remote

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Класс для тестирования соединения с Google Sheets API
 */
class GoogleSheetsApiTester(private val sheetsService: GoogleSheetsService) {

    fun testConnection() {
        Log.d(TAG, "Тестирование подключения к Google Sheets API")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                testReadAccess()
                testWriteAccess()
            } catch (e: Exception) {
                Log.e(TAG, "Ошибка при тестировании API", e)
            }
        }
    }

    private suspend fun testReadAccess() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Тестирование доступа на чтение...")
            Log.d(TAG, "SHEET_ID: ${GoogleSheetsConfig.SHEET_ID}")
            Log.d(TAG, "PLAYERS_RANGE: ${GoogleSheetsConfig.PLAYERS_RANGE}")
            Log.d(TAG, "API_KEY: ${GoogleSheetsConfig.API_KEY}")

            val response = sheetsService.getValues(
                spreadsheetId = GoogleSheetsConfig.SHEET_ID,
                range = GoogleSheetsConfig.PLAYERS_RANGE,
                apiKey = GoogleSheetsConfig.API_KEY
            )

            if (response.isSuccessful) {
                val data = response.body()
                Log.d(TAG, "Успешно получены данные: ${data?.values?.size ?: 0} строк")
                Log.d(TAG, "Первая строка (заголовки): ${data?.values?.firstOrNull()}")
            } else {
                Log.e(TAG, "Ошибка при чтении: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при тестировании чтения", e)
        }
    }

    private suspend fun testWriteAccess() = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Тестирование доступа на запись...")

            // Создаем тестовые данные
            val testRow = listOf("test_id", "Test Entry", "25", "Зеленый", "Тест", "", "", "2025-04-15")

            val valueRange = ValueRange(
                range = GoogleSheetsConfig.PLAYERS_RANGE,
                values = listOf(testRow)
            )

            val response = sheetsService.appendValues(
                spreadsheetId = GoogleSheetsConfig.SHEET_ID,
                range = GoogleSheetsConfig.PLAYERS_RANGE,
                apiKey = GoogleSheetsConfig.API_KEY,
                valueRange = valueRange
            )

            if (response.isSuccessful) {
                Log.d(TAG, "Успешно добавлена тестовая запись: ${response.body()}")
            } else {
                Log.e(TAG, "Ошибка при записи: ${response.code()} - ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Исключение при тестировании записи", e)
        }
    }

    companion object {
        private const val TAG = "GoogleSheetsApiTester"
    }
}