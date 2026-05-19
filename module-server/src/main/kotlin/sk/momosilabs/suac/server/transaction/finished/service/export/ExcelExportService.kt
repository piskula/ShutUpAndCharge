package sk.momosilabs.suac.server.transaction.finished.service.export

import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.streaming.SXSSFCell
import org.apache.poi.xssf.streaming.SXSSFWorkbook
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.util.SequencedMap
import java.util.stream.Stream
import java.time.Instant
import java.util.Date

@Service
class ExcelExportService {

    fun <T> exportEntityToStream(
        entities: Stream<T>,
        fieldMappingDefinition: SequencedMap<String, (T) -> Any?>,
    ): ByteArrayOutputStream {
        val workbook = SXSSFWorkbook()
        val sheet = workbook.createSheet("Sheet1")

        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            setFont(workbook.createFont().apply { bold = true })
        }

        val headerRow = sheet.createRow(0)
        fieldMappingDefinition.keys.forEachIndexed { i, title ->
            headerRow.createCell(i).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        val dateStyle = workbook.createCellStyle().apply {
            dataFormat = workbook.creationHelper.createDataFormat().getFormat("yyyy-mm-dd hh:mm:ss")
        }

        var rowIndex = 0
        entities.forEach { entity ->
            val row = sheet.createRow(++rowIndex)
            fieldMappingDefinition.entries.forEachIndexed { cellIndex, (_, resolver) ->
                setValueToCell(row.createCell(cellIndex), resolver(entity), dateStyle)
            }
        }

        fieldMappingDefinition.keys.forEachIndexed { index, _ -> sheet.setColumnWidth(index, 6000) }
        return writeWorkbookAsStream(workbook)
    }

    private fun writeWorkbookAsStream(workbook: SXSSFWorkbook): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        outputStream.use {
            workbook.write(it)
        }
        workbook.dispose()
        return outputStream
    }

    private fun setValueToCell(cell: SXSSFCell, value: Any?, dateStyle: org.apache.poi.ss.usermodel.CellStyle) {
        when (value) {
            is String -> cell.setCellValue(value)
            is Instant -> {
                cell.setCellValue(Date.from(value))
                cell.cellStyle = dateStyle
            }
            is BigDecimal -> cell.setCellValue(value.toDouble())
            else -> cell.setCellValue(value.toString())
        }
    }

}
