package org.momosi.shutupandcharge.core.uilibrary.illustrations

import org.jetbrains.compose.resources.DrawableResource
import org.momosi.shutupandcharge.core.uilibrary.resources.Res
import org.momosi.shutupandcharge.core.uilibrary.resources.error_generic_illustration
import org.momosi.shutupandcharge.core.uilibrary.resources.error_no_connection_illustration
import org.momosi.shutupandcharge.core.uilibrary.resources.welcome_charger_illustration

data object SuacIllustrations {
    val NoConnection: DrawableResource = Res.drawable.error_no_connection_illustration
    val GenericError: DrawableResource = Res.drawable.error_generic_illustration
    val Charger: DrawableResource = Res.drawable.welcome_charger_illustration
}
