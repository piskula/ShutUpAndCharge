package sk.momosilabs.suac.server.common

import org.slf4j.LoggerFactory
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import org.springframework.web.servlet.resource.NoResourceFoundException
import org.springframework.web.util.WebUtils
import sk.momosilabs.suac.server.common.model.ErrorDTO
import java.util.UUID

// this is a workaround to fix broken FE hard refresh in browser getting 404 no static resource
@ControllerAdvice
@Order(-1)
internal class NoResourceFoundExceptionHandler {
    @ExceptionHandler(NoResourceFoundException::class)
    @Throws(Exception::class)
    fun handleResourceNotFound(ex: Exception): ResponseEntity<Any?>? {
        throw ex
    }
}

@ControllerAdvice
class GlobalExceptionHandler : ResponseEntityExceptionHandler() {

    companion object {
        private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
        private val internalServerErrorDto = ErrorDTO(
            userMessage = "Internal Server Error",
            errorIdentifier = UUID.randomUUID(),
        )
    }

    @ExceptionHandler(RuntimeException::class)
    fun handleApplicationException(exception: RuntimeException, request: WebRequest): ResponseEntity<ErrorDTO> {
        val httpStatus = if (exception is GlobalException) getHttpStatus(exception) else HttpStatus.INTERNAL_SERVER_ERROR
        if (httpStatus == HttpStatus.INTERNAL_SERVER_ERROR) {
            request.setAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE, exception, WebRequest.SCOPE_REQUEST)
        }

        val errorDto = if (exception is GlobalException) getErrorDto(exception) else internalServerErrorDto
        GlobalExceptionHandler.logger.error("Error = $errorDto", exception)

        return ResponseEntity<ErrorDTO>(errorDto, HttpHeaders(), httpStatus)
    }

    private fun getHttpStatus(exception: GlobalException): HttpStatus {
        val cause = exception.cause
        if (cause is GlobalException)
            return cause.httpStatus
        else
            return exception.httpStatus
    }

    private fun getErrorDto(exception: GlobalException): ErrorDTO {
        val cause = exception.cause
        if (cause is GlobalException)
            return ErrorDTO(cause.userMessage, UUID.randomUUID())
        else
            return ErrorDTO(exception.userMessage, UUID.randomUUID())
    }

}
