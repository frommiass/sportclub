package pro.grino.karateclub.domain.di

import org.koin.dsl.module
import pro.grino.karateclub.domain.usecase.*

val domainModule = module {
    // Player use cases
    factory { GetAllPlayersUseCase(get()) }
    factory { GetPlayerByIdUseCase(get()) }
    factory { AddPlayerUseCase(get()) }
    factory { UpdatePlayerUseCase(get()) }
    factory { DeletePlayerUseCase(get()) }

    // Group use cases
    factory { GetAllGroupsUseCase(get()) }
    factory { GetGroupByIdUseCase(get()) }
    factory { AddGroupUseCase(get()) }
    factory { UpdateGroupUseCase(get()) }
    factory { DeleteGroupUseCase(get()) }
}