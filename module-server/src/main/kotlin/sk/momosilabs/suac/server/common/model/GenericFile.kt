package sk.momosilabs.suac.server.common.model

import org.springframework.http.MediaType
import java.io.ByteArrayOutputStream
import java.util.Objects

data class GenericFile(
    val filename: String,
    val content: ByteArray,
    val size: Int,
    val contentType: MediaType,
) {

    companion object {
        fun asPngFile(filename: String, content: ByteArrayOutputStream) =
            content.toByteArray().let { contentByteArray ->
                GenericFile(
                    filename = filename,
                    content = contentByteArray,
                    size = contentByteArray.size,
                    contentType = MediaType.IMAGE_PNG,
                )
            }

        fun asZipFile(filename: String, content: ByteArrayOutputStream) =
            content.toByteArray().let { contentByteArray ->
                GenericFile(
                    filename = filename,
                    content = contentByteArray,
                    size = contentByteArray.size,
                    contentType = MediaType.APPLICATION_OCTET_STREAM, // TODO check if can be application/zip
                )
            }

        fun asExcelFile(excelName: String, content: ByteArrayOutputStream) =
            content.toByteArray().let { contentByteArray ->
                GenericFile(
                    filename = excelName,
                    content = contentByteArray,
                    size = contentByteArray.size,
                    contentType = MediaType("application", "vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
                )
            }

        fun asPdfFile(pdfName: String, content: ByteArrayOutputStream) =
            content.toByteArray().let { contentByteArray ->
                GenericFile(
                    filename = pdfName,
                    content = contentByteArray,
                    size = contentByteArray.size,
                    contentType = MediaType.APPLICATION_PDF,
                )
            }
    }

    override fun equals(other: Any?): Boolean = this === other ||
        other is GenericFile && this.filename == other.filename && this.contentType == other.contentType

    override fun hashCode(): Int = Objects.hash(filename, contentType)
}
