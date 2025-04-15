package pro.grino.karateclub.data.di

import android.util.Log
import org.koin.dsl.module
import pro.grino.karateclub.data.repository.ServiceAccountGroupRepository
import pro.grino.karateclub.data.repository.ServiceAccountPlayerRepository
import pro.grino.karateclub.domain.repository.GroupRepository
import pro.grino.karateclub.domain.repository.PlayerRepository

val dataModule = module {
    // API Service (используется в старой реализации)
    single { NetworkModule.provideOkHttpClient() }
    single { NetworkModule.provideGoogleSheetsService(get()) }

    single<PlayerRepository> {
        Log.d("DataModule", "Создание репозитория для игроков")
        ServiceAccountPlayerRepository(get())
    }

    single<GroupRepository> {
        Log.d("DataModule", "Создание репозитория для групп")
        ServiceAccountGroupRepository(get())
    }
}