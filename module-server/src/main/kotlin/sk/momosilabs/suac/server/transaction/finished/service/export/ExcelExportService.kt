package sk.momosilabs.suac.server.transaction.finished.service.export

import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import org.springframework.stereotype.Service
import sk.momosilabs.suac.server.transaction.finished.model.TransactionFinished
import java.io.ByteArrayOutputStream
import java.math.BigDecimal
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@Service
class ExcelExportService {

    private val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").withZone(ZoneOffset.UTC)

    fun exportTransactions(transactions: List<TransactionFinished>): ByteArray {
        val workbook = XSSFWorkbook()
        val sheet = workbook.createSheet("Transactions")

        val headerStyle = workbook.createCellStyle().apply {
            fillForegroundColor = IndexedColors.GREY_25_PERCENT.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            setFont(workbook.createFont().apply { bold = true })
        }

        val headers = listOf("Time (UTC)", "User", "Station", "kWh", "Price")
        val headerRow = sheet.createRow(0)
        headers.forEachIndexed { i, title ->
            headerRow.createCell(i).apply {
                setCellValue(title)
                cellStyle = headerStyle
            }
        }

        transactions.forEachIndexed { idx, tx ->
            val row = sheet.createRow(idx + 1)
            row.createCell(0).setCellValue(dateFormatter.format(tx.time))
            row.createCell(1).setCellValue(tx.accountName)
            row.createCell(2).setCellValue(if (tx.price > BigDecimal.ZERO) "Credit" else tx.chargingStationId ?: "")
            row.createCell(3).setCellValue(if (tx.price <= BigDecimal.ZERO) tx.kwh.toDouble() else 0.0)
            row.createCell(4).setCellValue(tx.price.toDouble())
        }

        headers.indices.forEach { sheet.autoSizeColumn(it) }

        val out = ByteArrayOutputStream()
        workbook.use { it.write(out) }
        return out.toByteArray()
    }
}
