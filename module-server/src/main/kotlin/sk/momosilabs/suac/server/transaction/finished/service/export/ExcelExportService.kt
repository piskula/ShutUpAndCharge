package sk.momosilabs.suac.server.transaction.finished.service.export

import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFCell
import org.apache.poi.xssf.usermodel.XSSFWorkbook
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
        val workbook = XSSFWorkbook()
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

        entities.toList().forEachIndexed { rowIndex, entity ->
            val row = sheet.createRow(rowIndex + 1)
            fieldMappingDefinition.entries.forEachIndexed { cellIndex, (_, resolver) ->
                val value = resolver.invoke(entity)
                val cell = row.createCell(cellIndex)
                setValueToCell(cell, value)
            }
        }

        fieldMappingDefinition.onEachIndexed { index, _ -> sheet.autoSizeColumn(index) }
        return writeWorkbookAsStream(workbook)
    }

    private fun writeWorkbookAsStream(workbook: Workbook): ByteArrayOutputStream {
        val outputStream = ByteArrayOutputStream()
        outputStream.use {
            workbook.write(it)
        }
        return outputStream
    }

    private fun setValueToCell(cell: XSSFCell, value: Any?) {
        when (value) {
            is String -> cell.setCellValue(value)
            is Instant -> {
                val workbook = cell.sheet.workbook
                val style = workbook.createCellStyle()
                style.dataFormat = workbook.creationHelper
                    .createDataFormat()
                    .getFormat("yyyy-mm-dd hh:mm:ss")
                cell.setCellValue(Date.from(value))
                cell.cellStyle = style
            }
            is BigDecimal -> cell.setCellValue(value.toDouble())
            else -> cell.setCellValue(value.toString())
        }
    }

}
