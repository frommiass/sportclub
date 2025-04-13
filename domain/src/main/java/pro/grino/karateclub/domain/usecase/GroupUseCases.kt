package pro.grino.karateclub.domain.usecase

import kotlinx.coroutines.flow.Flow
import pro.grino.karateclub.domain.model.Group
import pro.grino.karateclub.domain.repository.GroupRepository

class GetAllGroupsUseCase(private val repository: GroupRepository) {
    operator fun invoke(): Flow<List<Group>> {
        return repository.getAllGroups()
    }
}

class GetGroupByIdUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(id: String): Group {
        return repository.getGroupById(id)
    }
}

class AddGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(group: Group) {
        return repository.addGroup(group)
    }
}

class UpdateGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(group: Group) {
        return repository.updateGroup(group)
    }
}

class DeleteGroupUseCase(private val repository: GroupRepository) {
    suspend operator fun invoke(id: String) {
        return repository.deleteGroup(id)
    }
}