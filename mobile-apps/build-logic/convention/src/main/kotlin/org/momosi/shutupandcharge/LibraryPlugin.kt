package org.momosi.shutupandcharge

import com.android.build.api.dsl.KotlinMultiplatformAndroidLibraryTarget
import org.momosi.shutupandcharge.utils.CompileSdk
import org.momosi.shutupandcharge.utils.MinSdk
import org.momosi.shutupandcharge.utils.configureKotlin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

class LibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.multiplatform")
            apply("com.android.kotlin.multiplatform.library")
            apply("suac.spotless")
            apply("suac.detekt")
        }

        extensions.configure<KotlinMultiplatformExtension> {
            jvmToolchain(25)

            applyDefaultHierarchyTemplate()

            iosArm64()
            iosSimulatorArm64()

            targets.withType<KotlinMultiplatformAndroidLibraryTarget> {
                compileSdk = CompileSdk
                minSdk = MinSdk
                lint {
                    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
                }
            }
        }

        configureKotlin()
    }
}
