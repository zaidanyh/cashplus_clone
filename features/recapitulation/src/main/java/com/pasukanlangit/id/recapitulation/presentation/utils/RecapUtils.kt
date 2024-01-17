package com.pasukanlangit.id.recapitulation.presentation.utils

import android.content.Context
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiah
import com.pasukanlangit.id.recapitulation.R
import com.pasukanlangit.id.recapitulation.domain.model.MutationDepositResponse
import com.pasukanlangit.id.recapitulation.domain.model.ProfitByProductResponse
import org.apache.poi.ss.usermodel.FillPatternType
import org.apache.poi.ss.usermodel.HorizontalAlignment
import org.apache.poi.ss.usermodel.IndexedColors
import org.apache.poi.ss.usermodel.VerticalAlignment
import org.apache.poi.xssf.usermodel.XSSFSheet
import java.text.SimpleDateFormat
import java.util.*


object RecapUtils {
    fun String.changeDateFormat(context: Context): String {
        val sdfParse = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val date = sdfParse.parse(this)
        val sdf = SimpleDateFormat(
            "dd MMM yyyy - HH:mm:ss", Locale(
                context.getString(R.string.locale_language), context.getString(
                    R.string.locale_country
                )
            )
        )
        return sdf.format(date as Date)
    }

    fun XSSFSheet.setTitleXlsxFile(titles: List<String>) {
        val font = this.workbook.createFont()
        font.apply {
            fontHeightInPoints = (12).toShort()
            fontName = "Verdana"
            color = IndexedColors.BLACK.index
            bold = true
            italic = false
        }

        val cellStyle = this.workbook.createCellStyle()
        cellStyle.apply {
            wrapText = true
            alignment = HorizontalAlignment.CENTER
            verticalAlignment = VerticalAlignment.CENTER
            fillForegroundColor = IndexedColors.BLUE_GREY.index
            fillPattern = FillPatternType.SOLID_FOREGROUND
            setFont(font)
        }

        val row = this.createRow(0)
        row.height = (656).toShort()
        for (i in titles.indices) {
            val title = row.createCell(i)
            title.cellStyle = cellStyle
            title.setCellValue(titles[i])
        }
    }

    fun XSSFSheet.setDataXlsxFile(data: List<List<String>>, cellLongest: MutableList<Int>) {
        val font = this.workbook.createFont()
        font.apply {
            fontName = "Verdana"
            color = IndexedColors.BLACK.index
        }
        val cellStyle = this.workbook.createCellStyle()
        cellStyle.apply {
            setFont(font)
        }

        var rowNum = 0
        data.forEach { item ->
            val row = this.createRow(++rowNum)

            var cellNum = 0
            item.forEach { value ->
                val cell = row.createCell(cellNum)
                cell.cellStyle = cellStyle
                cell.setCellValue(value.ifEmpty { "-" })
                if (row.getCell(cellNum).stringCellValue.length > cellLongest[cellNum])
                    cellLongest[cellNum] = row.getCell(cellNum).stringCellValue.length
                this.setColumnWidth(cellNum, cellLongest[cellNum] * 256)
                cellNum++
            }
        }
    }

    fun List<ProfitByProductResponse>.toNestedListProductOfString(): List<List<String>> =
        this.mapIndexed { index, item ->
            listOf(
                "${index+1}", item.category, item.productCode, item.providerCode, item.desc,
                getNumberRupiah(item.modal.toBigIntegerOrNull()), getNumberRupiah(item.selling.toBigIntegerOrNull()),
                getNumberRupiah(item.profit.toBigIntegerOrNull())
            )
        }

    fun List<MutationDepositResponse>.toNestedListOfString(context: Context): List<List<String>> =
        this.map { item ->
            listOf(
                item.rowNum, item.trxDate.changeDateFormat(context), getNumberRupiah(item.value.toBigIntegerOrNull()),
                if (item.isDB) "Debit" else "Kredit", getNumberRupiah(item.endingBalance.toBigIntegerOrNull()),
                item.productCode.ifEmpty { "-" }, item.dest.ifEmpty { "" }, item.productDesc.ifEmpty { "" },
                item.category.ifEmpty { "" }
            )
        }
}