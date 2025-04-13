package pro.grino.karateclub.domain.repository

import kotlinx.coroutines.flow.Flow
import pro.grino.karateclub.domain.model.Group

interface GroupRepository {
    fun getAllGroups(): Flow<List<Group>>
    suspend fun getGroupById(id: String): Group
    suspend fun addGroup(group: Group)
    suspend fun updateGroup(group: Group)
    suspend fun deleteGroup(id: String)
}