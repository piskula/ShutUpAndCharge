package org.momosi.shutupandcharge.feature.profile.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.profile.impl.ProfileViewModel

val profileModule = module {
    viewModel { ProfileViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
