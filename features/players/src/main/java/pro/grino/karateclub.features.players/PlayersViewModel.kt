package pro.grino.karateclub.features.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.usecase.GetAllPlayersUseCase

class PlayersViewModel(
    private val getAllPlayersUseCase: GetAllPlayersUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<PlayersState>(PlayersState.Loading)
    val state: StateFlow<PlayersState> = _state

    init {
        loadPlayers()
    }

    fun loadPlayers() {
        viewModelScope.launch {
            _state.value = PlayersState.Loading

            getAllPlayersUseCase()
                .catch { e ->
                    _state.value = PlayersState.Error(e.message ?: "Ошибка загрузки данных")
                }
                .collect { players ->
                    _state.value = if (players.isEmpty()) {
                        PlayersState.Empty
                    } else {
                        PlayersState.Success(players)
                    }
                }
        }
    }
}

sealed class PlayersState {
    object Loading : PlayersState()
    data class Success(val players: List<Player>) : PlayersState()
    object Empty : PlayersState()
    data class Error(val message: String) : PlayersState()
}