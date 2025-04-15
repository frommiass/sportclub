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
import java.util.*

/**
 * Упрощенная реализация PlayerRepository с использованием сервисного аккаунта
 * Эта версия использует мок-данные, но структура готова для внедрения сервисного аккаунта
 */
class SimplifiedServiceAccountRepository(
    private val context: Context
) : PlayerRepository {
    private val TAG = "ServiceAccRepository"

    // Мок-данные для тестирования
    private val players = mutableListOf(
        Player(
            id = "1",
            name = "Иван Иванов",
            age = 15,
            belt = "Зеленый",
            group = "Юниоры",
            phone = "+7 900 123-45-67",
            email = "ivan@example.com",
            joinDate = "2023-01-15"
        ),
        Player(
            id = "2",
            name = "Мария Петрова",
            age = 14,
            belt = "Синий",
            group = "Юниоры",
            phone = "+7 900 234-56-78",
            email = "maria@example.com",
            joinDate = "2023-02-20"
        ),
        Player(
            id = "3",
            name = "Алексей Сидоров",
            age = 22,
            belt = "Коричневый",
            group = "Взрослые",
            phone = "+7 900 345-67-89",
            email = "alex@example.com",
            joinDate = "2022-05-10"
        )
    )

    override fun getAllPlayers(): Flow<List<Player>> = flow {
        Log.d(TAG, "Получение списка всех участников (мок)")
        kotlinx.coroutines.delay(500) // Имитация задержки сети
        emit(players)
    }

    override suspend fun getPlayerById(id: String): Player {
        Log.d(TAG, "Получение участника по ID: $id (мок)")
        kotlinx.coroutines.delay(300) // Имитация задержки сети
        return players.find { it.id == id } ?: Player("", "", 0, "", "")
    }

    override suspend fun addPlayer(player: Player) {
        Log.d(TAG, "Добавление участника: ${player.name} (мок)")
        // Создаем нового игрока с уникальным ID, если ID не указан
        val newPlayer = if (player.id.isBlank()) {
            player.copy(id = UUID.randomUUID().toString())
        } else {
            player
        }
        players.add(newPlayer)
        kotlinx.coroutines.delay(500) // Имитация задержки сети
    }

    override suspend fun updatePlayer(player: Player) {
        Log.d(TAG, "Обновление участника: ${player.id} - ${player.name} (мок)")
        val index = players.indexOfFirst { it.id == player.id }
        if (index != -1) {
            players[index] = player
        }
        kotlinx.coroutines.delay(500) // Имитация задержки сети
    }

    override suspend fun deletePlayer(id: String) {
        Log.d(TAG, "Удаление участника с ID: $id (мок)")
        players.removeIf { it.id == id }
        kotlinx.coroutines.delay(500) // Имитация задержки сети
    }

    /**
     * Код ниже подготовлен для будущего использования с сервисным аккаунтом
     * Раскомментируйте и используйте, когда будет готов файл ключа сервисного аккаунта
     */
    /*
    private val SERVICE_ACCOUNT_KEY_FILE = "service-account-key.json"

    private suspend fun getSheetsService(): Sheets = withContext(Dispatchers.IO) {
        try {
            // Получаем поток с ключом сервисного аккаунта из assets
            val keyStream = context.assets.open(SERVICE_ACCOUNT_KEY_FILE)

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
    */
}