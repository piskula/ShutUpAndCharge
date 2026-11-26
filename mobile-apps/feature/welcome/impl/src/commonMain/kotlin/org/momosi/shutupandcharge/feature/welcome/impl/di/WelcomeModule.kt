package org.momosi.shutupandcharge.feature.welcome.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.welcome.impl.WelcomeViewModel

val welcomeModule = module {
    viewModel { WelcomeViewModel(get(), get(), get(), get()) }
}
