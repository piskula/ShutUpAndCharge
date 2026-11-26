package org.momosi.shutupandcharge.feature.dashboard.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.dashboard.impl.DashboardViewModel

val dashboardModule = module {
    viewModel { DashboardViewModel(get(), get(), get(), get(), get(), get(), get()) }
}
