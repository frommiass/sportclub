package pro.grino.karateclub.features.players.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pro.grino.karateclub.features.players.AddPlayerViewModel
import pro.grino.karateclub.features.players.PlayersViewModel

val playersModule = module {
    // ViewModels
    viewModel { PlayersViewModel(get()) }
    viewModel { AddPlayerViewModel(get()) }
}