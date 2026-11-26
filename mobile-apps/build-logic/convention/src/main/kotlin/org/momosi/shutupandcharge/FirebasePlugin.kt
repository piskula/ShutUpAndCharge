package org.momosi.shutupandcharge

import org.momosi.shutupandcharge.utils.commonMainDependencies
import org.momosi.shutupandcharge.utils.libs
import org.gradle.api.Plugin
import org.gradle.api.Project

class FirebasePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.commonMainDependencies {
            implementation(target.libs.findLibrary("firebase-common").get())
            implementation(target.libs.findLibrary("firebase-crashlytics").get())
        }
        target.dependencies.add(
            "androidMainImplementation",
            target.dependencies.platform(target.libs.findLibrary("firebase-bom").get()),
        )
    }
}
