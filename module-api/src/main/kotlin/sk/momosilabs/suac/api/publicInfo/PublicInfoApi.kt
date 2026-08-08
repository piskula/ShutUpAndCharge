package sk.momosilabs.suac.api.publicInfo

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
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

    @Operation(summary = "Stream Charging Status")
    @ApiResponse(
        responseCode = "200",
        description = "Server-Sent Events stream; each 'status' event's data is a ChargerStatusDTO as JSON",
        content = [Content(mediaType = MediaType.TEXT_EVENT_STREAM_VALUE, schema = Schema(type = "string"))],
    )
    @GetMapping("$ENDPOINT_INFO/chargerStatus/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun streamChargerStatus(response: HttpServletResponse): SseEmitter

}
