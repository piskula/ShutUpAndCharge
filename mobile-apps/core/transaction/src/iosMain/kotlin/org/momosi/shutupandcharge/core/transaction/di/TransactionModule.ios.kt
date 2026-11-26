package org.momosi.shutupandcharge.core.transaction.di

import org.koin.core.module.Module
import org.koin.dsl.module
import org.momosi.shutupandcharge.core.transaction.export.FileSharer
import org.momosi.shutupandcharge.core.transaction.export.IosFileSharer

actual fun platformTransactionModule(): Module = module {
    single<FileSharer> { IosFileSharer(get()) }
}
