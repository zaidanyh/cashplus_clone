package com.pasukanlangit.cashplus.utils

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.*
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.*
import android.graphics.pdf.PdfDocument
import android.graphics.pdf.PdfDocument.Page
import android.graphics.pdf.PdfDocument.PageInfo
import android.net.Uri
import android.os.Build
import android.text.StaticLayout
import android.text.TextPaint
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.pasukanlangit.cashplus.MainActivityNavComp
import com.pasukanlangit.cashplus.R
import com.pasukanlangit.cashplus.ui.checkout.ButtomSheetNotif
import com.pasukanlangit.cashplus.utils.print.NewAsyncEscPosPrinter
import com.pasukanlangit.id.core.MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE
import com.pasukanlangit.id.core.utils.putExtra
import com.pasukanlangit.id.library_core.datasource.utils.getNumberRupiahWithoutRp
import com.pasukanlangit.id.library_core.domain.model.NotifType
import java.math.BigInteger
import java.security.MessageDigest
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.ceil

const val DAY_IN_MILES = 86400000
val PERMISSION_LOCATION = arrayOf(
    Manifest.permission.ACCESS_FINE_LOCATION,
    Manifest.permission.ACCESS_COARSE_LOCATION
)
const val UPDATE_INTERVAL_IN_MILLISECONDS = 10000.toLong()
const val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = UPDATE_INTERVAL_IN_MILLISECONDS / 2
const val URL_IMAGE_EMPTY = "https://www.bigpharmacy.com.my/scripts/timthumb.php?src=https://www.bigpharmacy.com.my//site_media/img/103303EA.jpg&w=600&zc=1"
const val CITY_CODE_BALI_MMBC = "17193"

object MyUtils {
    fun createTransactionID(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy-HH-mm-ss", Locale.getDefault())
        val currentDate = sdf.format(Date()).replace("-", "")
        return "${getModelPhone()}-$currentDate"
    }

    fun Context.getCompleteLocaleDateFormat(date: String): String {
        val dateParser: Date = try {
            SimpleDateFormat("yyyy-MM-dd", Locale(this.getString(R.string.locale_language), this.getString(R.string.locale_country))).parse(date) as Date
        }catch (e: ParseException) {
            SimpleDateFormat("dd MMM yyyy", Locale(this.getString(R.string.locale_language), this.getString(R.string.locale_country))).parse(date) as Date
        }
        return SimpleDateFormat("EEEE, dd MMMM yyyy", Locale(this.getString(R.string.locale_language), this.getString(R.string.locale_country))).format(dateParser).toString()
    }

    private fun getModelPhone(): String {
        val phoneBrand = Build.MANUFACTURER.uppercase()
        val phoneModel = Build.MODEL.replace("-", "").uppercase()
        return "$phoneBrand-$phoneModel"
    }

//    fun getVersionNumberApps(context: Context): Int{
//        val manager = context.packageManager
//        val info = manager?.getPackageInfo(
//            context.packageName ?: "", 0
//        )
//
//        val versionNumber = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
//            info?.longVersionCode
//        else info?.versionCode
//
//       return versionNumber?.toInt() ?: -1
//    }
    fun PackageManager.getPackageInfoCompat(packageName: String, flags: Int = 0): PackageInfo =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
        getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(flags.toLong()))
    else @Suppress("DEPRECATION") getPackageInfo(packageName, flags)

    fun callCustomerService(context: Context, message: String? = null) {
        context.startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse(
                    java.lang.String.format(
                        "https://api.whatsapp.com/send?phone=%s&text=%s",
                        "628119993101",
                        if (message.isNullOrEmpty())
                            "Hallo, saya mau bertanya seputar Aplikasi Android Cashplus"
                        else message
                    )
                )
            )
        )
    }

    fun getAvaImagePlaceholder(name: String) = "https://ui-avatars.com/api/?name=$name"

    fun callCustomerServiceTelegram(context: Context) {
        try {
            val telegramIntent = Intent(Intent.ACTION_VIEW)
            telegramIntent.data = Uri.parse("http://telegram.me/cashpluscs")
            context.startActivity(telegramIntent)
        } catch (e: Exception) {
            Toast.makeText(context, "Terjadi masalah", Toast.LENGTH_SHORT).show()
        }
    }

    fun goToMainAndSendMessage(
        context: Context,
        message: String,
        notifType: NotifType,
        additionalToken: String? = null
    ) {
        (context as Activity).finishAffinity()
        val intent = Intent(context, MainActivityNavComp::class.java)
        intent.putExtra(MAIN_ACTIVITY_KEY_CALLBACK_MESSAGE, message)
        intent.putExtra(notifType)
        if(additionalToken != null) intent.putExtra(
            MainActivityNavComp.ADDITIONAL_MESSAGE_TOKEN,
            additionalToken
        )
        context.startActivity(intent)
    }

    fun generateMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val bigInt = BigInteger(1, md.digest(input.toByteArray(Charsets.UTF_8)))
        return String.format("%032x", bigInt)
    }

    fun getNumberRupiah(number: Double?): String {
        if(number == null) return ""
        return "Rp ${NumberFormat.getIntegerInstance().format(number.toInt())}"
    }

    fun getNumberRupiah(number: Int?): String {
        if(number == null) return ""
        return "Rp ${NumberFormat.getIntegerInstance().format(number)}"
    }

    fun String.getValueFromComplexString(): Int {
        val regex = """(!?,00)""".toRegex()
        val newValue = this
            .replace(oldValue = "Rp", newValue = "", ignoreCase = true)
            .replace(oldValue = "Rp.", newValue = "", ignoreCase = true)
            .trim()
        return regex.replace(newValue, "").convertToNumberInt()
    }

    fun convertToNormalNumber(value: String) : Double {
        val valueFiltered = value.filter { it.isDigit() }
        return valueFiltered.toDouble()
    }

    private fun String.convertToNumberInt(): Int = this.filter { it.isDigit()}.toInt()

    private fun String.addNewLineInString(position: Int, value: String): String {
        var selection = 15
        var newString = ""
        for (i in 0 until this.length) {
            if (i != 0 && i % position == 0) {
                newString += value
                selection = 15
            }
            newString += this[i]
            selection--
        }
        for (i in 0 until selection) {
            newString += " "
        }
        return newString
    }

    fun String.addCharAtEachIndex(position: Int, value: String): String {
        var newString = ""
        for (i in 0 until this.length) {
            if (i != 0 && i % position == 0) {
                newString += value
            }
            newString += this[i]
        }
        return newString
    }

    fun startZoomInAnim(context: Context, host: View){
        val animation = AnimationUtils.loadAnimation(context,R.anim.zoom_in_anim)
        host.startAnimation(animation)
    }

    fun showCoomingSon(fragmentManager: FragmentManager){
        val menusAllFragment = ButtomSheetNotif(
            "Coming Soon, Produk dalam tahap development",
            NotifType.NOTIF_COMING_SOON
        )
        menusAllFragment.show(fragmentManager, menusAllFragment.tag)
    }

    fun shareText(context: Context, subject: String?, body: String?) {
        val txtIntent = Intent(Intent.ACTION_SEND)
        txtIntent.type = "text/plain"
        txtIntent.putExtra(Intent.EXTRA_SUBJECT, subject)
        txtIntent.putExtra(Intent.EXTRA_TEXT, body)
        context.startActivity(Intent.createChooser(txtIntent, "Share"))
    }

    @SuppressLint("Recycle")
    fun generatePdf(context: Context, receipt: String): PdfDocument {
        val textCenter = TextPaint()
        val textLeft = TextPaint()
        val textRight = TextPaint()
        val lineDotted = Paint()

        with(textCenter) {
            color = Color.parseColor("#334155")
            textAlign = Paint.Align.CENTER
        }
        with(textLeft) {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            color = Color.parseColor("#334155")
            textSize = 6F
            textAlign = Paint.Align.LEFT
        }
        with(textRight) {
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            color = Color.parseColor("#334155")
            textSize = 7F
            textAlign = Paint.Align.RIGHT
        }
        with(lineDotted) {
            alpha = 255
            strokeWidth = 1F
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.FILL_AND_STROKE
            setPathEffect(DashPathEffect(floatArrayOf(4F, 4F), 0F))
        }

        val pdfDocument = PdfDocument()
        val pageInfo: PageInfo = PageInfo.Builder(180, 360, 1).create()
        val page: Page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        var scaleY = 12F
        canvas.drawBitmap(
            Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(context.resources, R.drawable.icon_cashplus_colored), 48, 24, true
            ), 66F, scaleY, Paint()
        )
        scaleY += 24
        val textNewLine = receipt.split("[L]")
        textNewLine.forEachIndexed { index, value ->
            val splitLeftAndRight = value.split(":[R]")
            if (value.isNotEmpty()) {
                if (splitLeftAndRight.first().trim().isNotEmpty()) {
                    canvas.drawText(
                        splitLeftAndRight.first().trim().replace("\n", ""),
                        10F, scaleY, textLeft
                    )
                    canvas.drawText(":", 72F, scaleY, textLeft)
                }
                val splitRightAndCenter = splitLeftAndRight.last().split("[C]")
                if (splitRightAndCenter.size > 1) {
                    if (index == textNewLine.lastIndex) textRight.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                    canvas.drawText(
                        splitRightAndCenter.first().trim().replace("\n[R]", ""),
                        170F, scaleY, textRight
                    )
                    scaleY += 12
                    for (i in 1 until splitRightAndCenter.size) {
                        val valueIndex = splitRightAndCenter[i]
                        if (valueIndex.contains("---"))
                            canvas.drawLine(10F, scaleY, 170F, scaleY + 1, lineDotted)
                        else {
                            val newValueIndex = if (valueIndex.contains("font"))
                                valueIndex.filter { it.isDigit() }.addCharAtEachIndex(4, "-")
                            else valueIndex
                            if (valueIndex.contains("font")) {
                                textCenter.typeface = Typeface.create(Typeface.DEFAULT_BOLD, Typeface.BOLD)
                                textCenter.textSize = 8F
                            }
                            else {
                                textCenter.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
                                textCenter.textSize = 7F
                            }

                            canvas.drawText(newValueIndex, 90F, scaleY, textCenter)
                        }
                        if (i != splitRightAndCenter.size) scaleY += 12
                    }
                } else {
                    val valueRight = splitLeftAndRight.last().trim().replace("\n[R]", "")
                    if (valueRight.length > 28) {
                        val slBuilder = StaticLayout.Builder.obtain(
                            valueRight, 0, valueRight.length, textRight, 100
                        )
                        val staticLayout = slBuilder.build()
                        canvas.save()
                        canvas.translate(170F, scaleY-6)
                        staticLayout.draw(canvas)
                        canvas.restore()
                        scaleY += 12
                    } else {
                        canvas.drawText(valueRight, 170F, scaleY, textRight)
                    }
                }
            }
            scaleY += 12
        }
        pdfDocument.finishPage(page)
        return pdfDocument
    }

    fun Context.actionShareFile(uri: Uri, type: String) {
        val intent = Intent(Intent.ACTION_SEND).apply {
            this.type = type
            putExtra(Intent.EXTRA_STREAM, uri)
            flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        }

        val chooser = Intent.createChooser(intent, "Share File")
        val resInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            packageManager.queryIntentActivities(chooser, PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong()))
        else @Suppress("DEPRECATION") packageManager.queryIntentActivities(chooser, PackageManager.MATCH_DEFAULT_ONLY)

        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName
            grantUriPermission(
                packageName,
                uri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }
        startActivity(chooser)
    }

    fun formatReceipt(
        receipt: String, codeProvider: String, productCode: String,
        fee: Int? = null, feeCounter: Int? = null
    ): String {
        val receiptSplit = receipt.split("||")
        val stringReceipt = StringBuilder("")
        var price = 0
        var admin = 0
        var newsForTransfer: String? = null

        receiptSplit.forEachIndexed { index, receiptRow ->
            val receiptRowSplitted = receiptRow.split("|")
            if(receiptRowSplitted.size > 1) {
                val splittingSn = receiptRowSplitted.last().split("//")
                if (splittingSn.isNotEmpty()) {
                    for (i in 0 until receiptRowSplitted.size - 1) {
                        if (receiptRowSplitted[i].contains(": ")) {
                            val receiptRowIndex = receiptRowSplitted[i].split(": ")
                            val receiptValue =
                                if (receiptRowIndex.last().contains("rp", ignoreCase = true) || receiptRowIndex.last().contains("rp.", ignoreCase = true)) {
                                    val lastValue = receiptRowIndex.last().replace("rp", "", ignoreCase = true).replace("rp.", "", ignoreCase = true).trim().getValueFromComplexString()
                                    if (receiptRowIndex.first().contains("tagihan", ignoreCase = true) || receiptRowIndex.first().contains("jumlah", ignoreCase = true) || receiptRowIndex.first().contains("harga", ignoreCase = true)) {
                                        price = lastValue
                                        ""
                                    } else if (receiptRowIndex.first().contains("admin", ignoreCase = true)) {
                                        admin = lastValue
                                        ""
                                    } else ""
                                } else
                                    if (codeProvider.contains("bpjs", ignoreCase = true) && index == receiptSplit.lastIndex)
                                        formatReceiptBPJSLastIndex(receiptRowSplitted[i])
                                    else
                                        if (receiptRowIndex.first().contains("biaya", ignoreCase = true)) ""
                                        else "[L]${receiptRowIndex.first().trim().addNewLineInString(15, "\n")}:[R]${receiptRowIndex.last().trim().addCharAtEachIndex(16, "\n[R]")}\n"
                            stringReceipt.append(receiptValue)
                        }
                        else stringReceipt.append("[C]${receiptRowSplitted[i].trim()}\n")
                        if (!receiptRowSplitted[i].contains(": "))
                            stringReceipt.append("[C]--------------------------------\n")
                    }

                    val splittingSizeIs1 = splittingSn.first().split("/")
                    if (splittingSizeIs1.size > 1) {
                        if (codeProvider.contains("PLN", ignoreCase = true)) {
                            val searchToken = splittingSizeIs1.first().replace("SN", "")
                            val token = searchToken.replace(":", "").trim()
                            val name = splittingSizeIs1[1]
                            val rates = splittingSizeIs1[2].trim()
                            val power = splittingSizeIs1[3].trim().replace("VA", "").trim()
                            val kwh = splittingSizeIs1[4].trim()

                            stringReceipt.append(
                                "[L]ATAS NAMA      :[R]${name.addCharAtEachIndex(16, "\n[R]")}\n" +
                                "[L]TARIF/DAYA     :[R]$rates/$power VA\n" +
                                "[L]JML KWh        :[R]$kwh\n"
                            )

                            if (splittingSn.size > 1) {
                                val splittingSizeMore1 = splittingSn.last().split("/")
                                val ppn = splittingSizeMore1.first().trim()
                                val ppj = splittingSizeMore1[1]
                                val stamp = splittingSizeMore1[2]
                                val installment = splittingSizeMore1[3]
                                val rpStroom = splittingSizeMore1[4]

                                stringReceipt.append(
                                    "[L]PPN            :[R]$ppn\n" +
                                    "[L]PPJ            :[R]$ppj\n" +
                                    "[L]MATERAI        :[R]$stamp\n" +
                                    "[L]ANGSURAN       :[R]$installment\n" +
                                    "[L]RP STROOM/TOKEN:[R]Rp. $rpStroom\n"
                                )
                            }
                            stringReceipt.append("[L]HARGA          :[R]Rp. ${getNumberRupiahWithoutRp(price)}\n")
                            stringReceipt.append("[C]--------------------------------\n")
                            stringReceipt.append("\n[C]TOKEN\n[C]<font size='big'>$token</font>\n")
                        } else {
                            if (!codeProvider.contains("BPJS", ignoreCase = true)) {
                                splittingSizeIs1.forEachIndexed { position, item ->
                                    if (position == 0) {
                                        val newValue = item.replace("sn", "", ignoreCase = true).replace(":", "").trim()
                                        stringReceipt.append("[L]SN             :[R]${newValue.trim().addCharAtEachIndex(16, "\n[R]")}\n")
                                    } else
                                        stringReceipt.append("[L]               :[R]${item.replace("REF:", "").trim().addCharAtEachIndex(16, "\n[R]")}\n")
                                }
                            }
                        }
                    } else {
                        if (codeProvider.contains("ovo", ignoreCase = true) || codeProvider.contains("gopay", ignoreCase = true))
                            stringReceipt.append("[L]               :[R]${splittingSn.last().trim()}\n")
                        else {
                            if (splittingSn.first().trim().isNotEmpty()) {
                                val sn = splittingSn.first().split(":")
                                if (sn.first().trim().isEmpty()) {
                                    stringReceipt.append(
                                        if (!codeProvider.contains("BPJS", ignoreCase = true))
                                            "[L]SN             :[R]${sn.last().trim().addCharAtEachIndex(16, "\n[R]")}\n"
                                        else ""
                                    )
                                } else
                                    if (sn.first().contains("berita", ignoreCase = true))
                                        newsForTransfer = sn.last()
                                    else
                                        stringReceipt.append("[L]${sn.first().trim().addNewLineInString(15, "\n")}:[R]${sn.last().trim().addCharAtEachIndex(16, "\n[R]")}\n")
                            }
                        }
                    }
                } else {
                    receiptRowSplitted.forEachIndexed { _, row ->
                        if (row.contains(" : ")) {
                            val receiptRowIndex = row.split(" : ")
                            stringReceipt.append(
                                "[L]${receiptRowIndex.first().trim().addNewLineInString(15, "\n")}:[R]${receiptRowIndex.last().trim().addCharAtEachIndex(16, "\n[R]")}\n"
                            )
                        } else stringReceipt.append("[C]${row.trim()}\n")
                    }
                }
            } else {
                if (receiptRow.contains(" : ")) {
                    val row = receiptRow.split(" : ")
                    stringReceipt.append(
                        "\n[L]${row.first().trim().addNewLineInString(15, "\n")}:[R]${row.last().trim().addCharAtEachIndex(16, "\n[R]")}\n"
                    )
                }
            }
        }

        if (!productCode.contains("pay", ignoreCase = true)) {
            if (!codeProvider.contains("pln", ignoreCase = true))
                stringReceipt.append("[L]HARGA          :[R]Rp. ${getNumberRupiahWithoutRp(price)}\n")
        } else {
            var total = price
            stringReceipt.append("[L]TAGIHAN/JUMLAH :[R]Rp. ${getNumberRupiahWithoutRp(price)}\n")
            if (admin > 0){
                total = total.plus(admin)
                stringReceipt.append("[L]BIAYA ADMIN    :[R]Rp. ${getNumberRupiahWithoutRp(admin)}\n")
            }

            if (fee != null && feeCounter != null) {
                if (fee == 0) stringReceipt.append("[L]BIAYA LAYANAN  :[R]GRATIS\n")
                else if (fee < 0) {
                    val discount = fee.toString().replace("-", "")
                    stringReceipt.append(
                        "[L]BIAYA LAYANAN  :[R]Rp. ${getNumberRupiahWithoutRp(feeCounter)}\n" +
                        "[L]POTONGAN HARGA :[R]Rp. ${getNumberRupiahWithoutRp(discount.toIntOrNull())}\n"
                    )
                }
                else {
                    val feeTotal = fee.plus(feeCounter)
                    stringReceipt.append("[L]BIAYA LAYANAN  :[R]Rp. ${getNumberRupiahWithoutRp(feeTotal)}\n")
                }
                total = total.plus(fee).plus(feeCounter)
            }

            if (productCode.contains("bank", ignoreCase = true))
                stringReceipt.append("[L]BERITA         :[R]${newsForTransfer?.addCharAtEachIndex(16, "\n[R]") ?: "-"}\n")
            stringReceipt.append("[L]TOTAL          :[R]Rp. ${getNumberRupiahWithoutRp(total)}\n")
        }

        if (codeProvider.contains("PLN", ignoreCase = true))
            stringReceipt.append("\n[C]Informasi Call Center 123\n[C]atau Hubungi PLN terdekat")
        else {
            stringReceipt.append("[C]--------------------------------\n")
            stringReceipt.append("\n[C]Simpan struk ini\n[C]sebagai bukti transaksi")
        }
        Log.d("stringReceipt", stringReceipt.toString())
        return stringReceipt.toString()
    }

    private fun formatReceiptBPJSLastIndex(value: String): String {
        val valueRow = value.split(" : ")
        var newValue = ""
        if (valueRow.first().contains("cabang", ignoreCase = true))
            newValue += "[L]${valueRow.first().replace("_", " ")}  :[R]${valueRow.last()}\n"
        return newValue
    }

    fun rescaleImagePrint(printer: NewAsyncEscPosPrinter, bitmap: Bitmap): Bitmap {
        val maxWidth = printer.printerWidthPx
        var newBitmap: Bitmap
        bitmap.apply {
            newBitmap = if (width > maxWidth || width >= 180) {
                val ratio = height.toDouble() / width.toDouble()
                val newHeight = ceil(maxWidth * ratio).toInt()
                Bitmap.createScaledBitmap(this, maxWidth/2, newHeight/2, false)
            } else {
                this
            }
        }

        return newBitmap
    }

    fun String.httpToHttps(): String =
        if (this.contains("http:")) this.replace("http:", "https:")
        else this
}