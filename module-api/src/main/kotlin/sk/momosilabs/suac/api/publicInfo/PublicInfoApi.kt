package sk.momosilabs.suac.api.publicInfo

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import sk.momosilabs.suac.api.publicInfo.dto.ChargerStatusDTO
import sk.momosilabs.suac.api.publicInfo.dto.BuildInfoDTO

@Tag(name = "Public Info")
interface PublicInfoApi {

    companion object {
        const val ENDPOINT_INFO = "/info"
    }

    @Operation(summary = "Check version")
    @GetMapping("$ENDPOINT_INFO/version")
    fun getVersion(request: HttpServletRequest): BuildInfoDTO

    @Operation(summary = "Get Charging Status")
    @GetMapping("$ENDPOINT_INFO/chargerStatus", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getChargerStatus(): ChargerStatusDTO

}
