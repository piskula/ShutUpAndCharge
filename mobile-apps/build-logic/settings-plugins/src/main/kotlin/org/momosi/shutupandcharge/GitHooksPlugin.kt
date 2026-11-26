package org.momosi.shutupandcharge

import org.gradle.api.Plugin
import org.gradle.api.initialization.Settings

private const val HOOK_MARKER = "suac-mobile-pre-commit"

class GitHooksPlugin : Plugin<Settings> {
    override fun apply(settings: Settings) {
        val hooksDir = settings.rootDir.resolve("../.git/hooks")
        if (!hooksDir.exists()) return

        val source = settings.rootDir.resolve(".githooks/pre-commit")
        val destination = hooksDir.resolve("pre-commit")
        val isForeignHook = destination.exists() && !destination.readText().contains(HOOK_MARKER)
        if (isForeignHook) return

        if (!destination.exists() || destination.readText() != source.readText()) {
            source.copyTo(destination, overwrite = true)
            destination.setExecutable(true)
        }
    }
}
