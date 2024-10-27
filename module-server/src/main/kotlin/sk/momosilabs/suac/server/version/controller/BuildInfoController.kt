package sk.momosilabs.suac.server.version.controller

import jakarta.servlet.http.HttpServletRequest
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.suac.api.version.BuildInfoApi
import sk.momosilabs.suac.api.version.dto.BuildInfoDTO
import java.time.ZoneOffset

@RestController
class BuildInfoController(
    private val buildProperties: BuildProperties,
) : BuildInfoApi {

    override fun get(request: HttpServletRequest): BuildInfoDTO = BuildInfoDTO(
        name = buildProperties.name,
        version = buildProperties.version,
        time = buildProperties.time.atOffset(ZoneOffset.UTC).toZonedDateTime(),
        url = request.requestURL.toString(),
    )

}
