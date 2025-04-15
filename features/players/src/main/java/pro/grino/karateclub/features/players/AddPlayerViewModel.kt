package pro.grino.karateclub.features.players

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pro.grino.karateclub.domain.model.Player
import pro.grino.karateclub.domain.usecase.AddPlayerUseCase

class AddPlayerViewModel(
    private val addPlayerUseCase: AddPlayerUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AddPlayerState>(AddPlayerState.Initial)
    val state: StateFlow<AddPlayerState> = _state

    suspend fun addPlayer(player: Player) {
        viewModelScope.launch {
            try {
                _state.value = AddPlayerState.Loading
                addPlayerUseCase(player)
                _state.value = AddPlayerState.Success
            } catch (e: Exception) {
                _state.value = AddPlayerState.Error(e.message ?: "Не удалось сохранить участника")
            }
        }
    }
}

sealed class AddPlayerState {
    object Initial : AddPlayerState()
    object Loading : AddPlayerState()
    object Success : AddPlayerState()
    data class Error(val message: String) : AddPlayerState()
}