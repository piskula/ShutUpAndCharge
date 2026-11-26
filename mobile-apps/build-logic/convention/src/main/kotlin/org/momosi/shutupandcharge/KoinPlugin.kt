package org.momosi.shutupandcharge

import org.momosi.shutupandcharge.utils.androidMainDependencies
import org.momosi.shutupandcharge.utils.commonMainDependencies
import org.momosi.shutupandcharge.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class KoinPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        commonMainDependencies {
            implementation(libs.findLibrary("koin-core").get())
            implementation(libs.findLibrary("koin-compose-viewmodel").get())
        }
        androidMainDependencies {
            implementation(libs.findLibrary("koin-android").get())
            implementation(libs.findLibrary("koin-androidx-compose").get())
        }
    }
}
