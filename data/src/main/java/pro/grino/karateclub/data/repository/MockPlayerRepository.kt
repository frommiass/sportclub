package pro.grino.karateclub.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.repository.PlayerRepository
import java.util.UUID

/**
 * Мок-реализация репозитория игроков для тестирования
 * Используется, когда API ключ не настроен
 */
class MockPlayerRepository : PlayerRepository {

    // Фиктивные данные для тестирования
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
        ),
        Player(
            id = "4",
            name = "Елена Кузнецова",
            age = 20,
            belt = "Черный",
            group = "Взрослые",
            phone = "+7 900 456-78-90",
            email = "elena@example.com",
            joinDate = "2021-09-05"
        ),
        Player(
            id = "5",
            name = "Дмитрий Новиков",
            age = 12,
            belt = "Желтый",
            group = "Дети",
            phone = "+7 900 567-89-01",
            email = "dmitry@example.com",
            joinDate = "2023-06-30"
        )
    )

    override fun getAllPlayers(): Flow<List<Player>> = flow {
        // Искусственная задержка для имитации сетевого запроса
        kotlinx.coroutines.delay(1000)
        emit(players)
    }

    override suspend fun getPlayerById(id: String): Player {
        // Искусственная задержка для имитации сетевого запроса
        kotlinx.coroutines.delay(500)
        return players.find { it.id == id } ?: Player("", "", 0, "", "")
    }

    override suspend fun addPlayer(player: Player) {
        // Создаем нового игрока с уникальным ID
        val newPlayer = player.copy(id = UUID.randomUUID().toString())
        players.add(newPlayer)
    }

    override suspend fun updatePlayer(player: Player) {
        val index = players.indexOfFirst { it.id == player.id }
        if (index != -1) {
            players[index] = player
        }
    }

    override suspend fun deletePlayer(id: String) {
        players.removeIf { it.id == id }
    }
}

