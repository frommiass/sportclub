package pro.grino.karateclub.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import pro.grino.karateclub.domain.model.Group
import pro.grino.karateclub.domain.repository.GroupRepository
import java.util.UUID

/**
 * Мок-реализация репозитория групп для демонстрации
 */
class MockGroupRepository : GroupRepository {

    // Фиктивные данные для тестирования
    private val groups = mutableListOf(
        Group(
            id = "1",
            name = "Начинающие",
            coach = "Иванов А.С.",
            level = "Начальный",
            schedule = "Пн, Ср, Пт 18:00-19:30",
            maxCapacity = 15,
            currentMembersCount = 8
        ),
        Group(
            id = "2",
            name = "Юниоры",
            coach = "Петров И.В.",
            level = "Средний",
            schedule = "Вт, Чт 16:00-18:00, Сб 10:00-12:00",
            maxCapacity = 12,
            currentMembersCount = 10
        ),
        Group(
            id = "3",
            name = "Продвинутые",
            coach = "Сидоров П.А.",
            level = "Продвинутый",
            schedule = "Пн, Ср, Пт 20:00-22:00",
            maxCapacity = 10,
            currentMembersCount = 7
        ),
        Group(
            id = "4",
            name = "Мастера",
            coach = "Кузнецов М.И.",
            level = "Мастера",
            schedule = "Вт, Чт 19:00-21:00, Сб 12:00-14:00",
            maxCapacity = 8,
            currentMembersCount = 5
        )
    )

    override fun getAllGroups(): Flow<List<Group>> = flow {
        // Искусственная задержка для имитации сетевого запроса
        kotlinx.coroutines.delay(1000)
        emit(groups)
    }

    override suspend fun getGroupById(id: String): Group {
        // Искусственная задержка для имитации сетевого запроса
        kotlinx.coroutines.delay(500)
        return groups.find { it.id == id } ?: Group("", "", "", "", "")
    }

    override suspend fun addGroup(group: Group) {
        // Создаем новую группу с уникальным ID
        val newGroup = if (group.id.isBlank()) {
            group.copy(id = UUID.randomUUID().toString())
        } else {
            group
        }
        groups.add(newGroup)
    }

    override suspend fun updateGroup(group: Group) {
        val index = groups.indexOfFirst { it.id == group.id }
        if (index != -1) {
            groups[index] = group
        }
    }

    override suspend fun deleteGroup(id: String) {
        groups.removeIf { it.id == id }
    }
}