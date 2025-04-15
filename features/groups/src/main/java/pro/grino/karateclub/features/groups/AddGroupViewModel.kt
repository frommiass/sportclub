package pro.grino.karateclub.features.groups

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import pro.grino.karateclub.domain.model.Group
import pro.grino.karateclub.domain.usecase.AddGroupUseCase

class AddGroupViewModel(
    private val addGroupUseCase: AddGroupUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<AddGroupState>(AddGroupState.Initial)
    val state: StateFlow<AddGroupState> = _state

    suspend fun addGroup(group: Group) {
        viewModelScope.launch {
            try {
                _state.value = AddGroupState.Loading
                addGroupUseCase(group)
                _state.value = AddGroupState.Success
            } catch (e: Exception) {
                _state.value = AddGroupState.Error(e.message ?: "Не удалось сохранить группу")
            }
        }
    }
}

sealed class AddGroupState {
    object Initial : AddGroupState()
    object Loading : AddGroupState()
    object Success : AddGroupState()
    data class Error(val message: String) : AddGroupState()
}