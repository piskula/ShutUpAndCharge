package org.momosi.shutupandcharge

import org.momosi.shutupandcharge.utils.commonMainDependencies
import org.momosi.shutupandcharge.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class FeatureApiPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("suac.library")
            apply("org.jetbrains.kotlin.plugin.serialization")
        }

        commonMainDependencies {
            api(libs.findLibrary("androidx-navigation3-runtime").get())
        }
    }
}
