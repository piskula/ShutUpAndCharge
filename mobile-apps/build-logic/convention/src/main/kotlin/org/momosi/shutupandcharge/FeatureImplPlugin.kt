package org.momosi.shutupandcharge

import org.momosi.shutupandcharge.utils.commonMainDependencies
import org.gradle.api.Plugin
import org.gradle.api.Project

class FeatureImplPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("suac.library")
            apply("suac.koin")
            apply("suac.compose")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        commonMainDependencies {
            implementation(project(":core:ui-library"))
            implementation(project(":core:navigation"))
        }
    }
}
