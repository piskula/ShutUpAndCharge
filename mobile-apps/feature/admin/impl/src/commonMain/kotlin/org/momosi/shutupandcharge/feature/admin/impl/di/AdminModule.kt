package org.momosi.shutupandcharge.feature.admin.impl.di

import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module
import org.momosi.shutupandcharge.feature.admin.impl.AdminViewModel

val adminModule = module {
    viewModel { AdminViewModel(get(), get(), get(), get(), get()) }
}
