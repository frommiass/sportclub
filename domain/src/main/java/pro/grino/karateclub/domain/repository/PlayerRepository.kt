package pro.grino.karateclub.domain.repository

import kotlinx.coroutines.flow.Flow
import pro.grino.karateclub.domain.model.Player

interface PlayerRepository {
    fun getAllPlayers(): Flow<List<Player>>
    suspend fun getPlayerById(id: String): Player
    suspend fun addPlayer(player: Player)
    suspend fun updatePlayer(player: Player)
    suspend fun deletePlayer(id: String)
}