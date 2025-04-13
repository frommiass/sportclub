package pro.grino.karateclub.data.di

import org.koin.dsl.module
import pro.grino.karateclub.data.repository.GroupRepositoryImpl
import pro.grino.karateclub.data.repository.PlayerRepositoryImpl
import pro.grino.karateclub.domain.repository.GroupRepository
import pro.grino.karateclub.domain.repository.PlayerRepository

val dataModule = module {
    // API Service
    single { NetworkModule.provideOkHttpClient() }
    single { NetworkModule.provideGoogleSheetsService(get()) }

    // Repositories
    single<PlayerRepository> { PlayerRepositoryImpl(get()) }
    single<GroupRepository> { GroupRepositoryImpl(get()) }
}