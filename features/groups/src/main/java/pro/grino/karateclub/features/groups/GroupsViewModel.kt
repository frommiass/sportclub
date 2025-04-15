package pro.grino.karateclub.features.groups

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import pro.grino.karateclub.domain.model.Group
import pro.grino.karateclub.domain.usecase.GetAllGroupsUseCase

class GroupsViewModel(
    private val getAllGroupsUseCase: GetAllGroupsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<GroupsState>(GroupsState.Loading)
    val state: StateFlow<GroupsState> = _state

    init {
        loadGroups()
    }

    fun loadGroups() {
        viewModelScope.launch {
            Log.d("GroupsViewModel", "Загрузка списка групп")
            _state.value = GroupsState.Loading

            getAllGroupsUseCase()
                .catch { e ->
                    Log.e("GroupsViewModel", "Ошибка при загрузке групп", e)
                    _state.value = GroupsState.Error(e.message ?: "Ошибка загрузки данных")
                }
                .collect { groups ->
                    Log.d("GroupsViewModel", "Получено ${groups.size} групп")
                    _state.value = if (groups.isEmpty()) {
                        GroupsState.Empty
                    } else {
                        GroupsState.Success(groups)
                    }
                }
        }
    }
}

sealed class GroupsState {
    object Loading : GroupsState()
    data class Success(val groups: List<Group>) : GroupsState()
    object Empty : GroupsState()
    data class Error(val message: String) : GroupsState()
}