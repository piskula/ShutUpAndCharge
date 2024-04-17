package sk.momosilabs.trucker.server.buildInfo.controller

import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.RestController
import sk.momosilabs.trucker.api.BuildInfoApi
import sk.momosilabs.trucker.api.model.buildInfo.BuildInfoDTO
import java.time.ZoneOffset

@RestController
class BuildInfoController(
    private val buildProperties: BuildProperties,
) : BuildInfoApi {

    override fun get(): BuildInfoDTO = BuildInfoDTO(
        name = buildProperties.name,
        version = buildProperties.version,
        time = buildProperties.time.atOffset(ZoneOffset.UTC).toZonedDateTime(),
    )

}
