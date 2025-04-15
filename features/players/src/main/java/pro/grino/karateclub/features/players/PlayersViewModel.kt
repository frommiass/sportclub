package pro.grino.karateclub.features.players

import android.util.Log
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
            Log.d("PlayersViewModel", "Загрузка списка участников")
            _state.value = PlayersState.Loading

            getAllPlayersUseCase()
                .catch { e ->
                    Log.e("PlayersViewModel", "Ошибка при загрузке участников", e)
                    _state.value = PlayersState.Error(e.message ?: "Ошибка загрузки данных")
                }
                .collect { players ->
                    Log.d("PlayersViewModel", "Получено ${players.size} участников")
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