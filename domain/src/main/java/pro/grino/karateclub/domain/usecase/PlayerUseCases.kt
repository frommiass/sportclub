package pro.grino.karateclub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.repository.PlayerRepository

class GetAllPlayersUseCase(private val repository: PlayerRepository) {
    operator fun invoke(): Flow<List<Player>> {
        return repository.getAllPlayers()
    }
}

class GetPlayerByIdUseCase(private val repository: PlayerRepository) {
    suspend operator fun invoke(id: String): Player {
        return repository.getPlayerById(id)
    }
}

class AddPlayerUseCase(private val repository: PlayerRepository) {
    suspend operator fun invoke(player: Player) {
        return repository.addPlayer(player)
    }
}

class UpdatePlayerUseCase(private val repository: PlayerRepository) {
    suspend operator fun invoke(player: Player) {
        return repository.updatePlayer(player)
    }
}

class DeletePlayerUseCase(private val repository: PlayerRepository) {
    suspend operator fun invoke(id: String) {
        return repository.deletePlayer(id)
    }
}