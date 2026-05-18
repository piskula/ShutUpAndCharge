package sk.momosilabs.suac.server.common.controller.mapper

import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders.CONTENT_DISPOSITION
import org.springframework.http.ResponseEntity
import sk.momosilabs.suac.server.common.model.GenericFile
import java.nio.charset.StandardCharsets

fun GenericFile.mapToResponseEntity(): ResponseEntity<ByteArrayResource> =
    ResponseEntity.ok()
        .contentLength(content.size.toLong())
        .contentType(contentType)
        .header(CONTENT_DISPOSITION, filename.fileNameToContentDispositionUTF8())
        .body(ByteArrayResource(content))

private fun String.fileNameToContentDispositionUTF8() =
    ContentDisposition.attachment().filename(this, StandardCharsets.UTF_8).build().toString()
