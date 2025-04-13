package pro.grino.karateclub.features.groups.di

import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import pro.grino.karateclub.features.groups.GroupsViewModel

val groupsModule = module {
    // ViewModels
    viewModel { GroupsViewModel(get()) }
}