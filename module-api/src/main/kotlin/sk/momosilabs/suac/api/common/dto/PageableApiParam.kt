package sk.momosilabs.suac.api.common.dto

import io.swagger.annotations.ApiImplicitParam
import io.swagger.annotations.ApiImplicitParams

@Retention(AnnotationRetention.RUNTIME)
@ApiImplicitParams(
    ApiImplicitParam(paramType = "query", name = "page", dataType = "integer", dataTypeClass = Integer::class),
    ApiImplicitParam(paramType = "query", name = "size", dataType = "integer", dataTypeClass = Integer::class),
    ApiImplicitParam(paramType = "query", name = "sort", dataType = "string", dataTypeClass = String::class)
)
annotation class PageableApiParam
