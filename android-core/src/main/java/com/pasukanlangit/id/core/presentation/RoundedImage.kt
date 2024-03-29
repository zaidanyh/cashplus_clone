package com.pasukanlangit.id.core.presentation

import android.content.Context
import android.graphics.*


object RoundedImage {
    fun getRoundedCornerBitmap(
        context: Context,
        input: Bitmap?,
        pixels: Int,
        w: Int,
        h: Int,
        squareTL: Boolean,
        squareTR: Boolean,
        squareBL: Boolean,
        squareBR: Boolean
    ): Bitmap {
        val output: Bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(output)
        val densityMultiplier: Float = context.resources.displayMetrics.density
        val color = -0xbdbdbe
        val paint = Paint()
        val rect = Rect(0, 0, w, h)
        val rectF = RectF(rect)

        //make sure that our rounded corner is scaled appropriately
        val roundPx = pixels * densityMultiplier
        paint.isAntiAlias = true
        canvas.drawARGB(0, 0, 0, 0)
        paint.color = color
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint)


        //draw rectangles over the corners we want to be square
        if (squareTL) {
            canvas.drawRect(0f, 0f, (w / 2).toFloat(), (h / 2).toFloat(), paint)
        }
        if (squareTR) {
            canvas.drawRect((w / 2).toFloat(), 0f, w.toFloat(), (h / 2).toFloat(), paint)
        }
        if (squareBL) {
            canvas.drawRect(0f, (h / 2).toFloat(), (w / 2).toFloat(), h.toFloat(), paint)
        }
        if (squareBR) {
            canvas.drawRect((w / 2).toFloat(), (h / 2).toFloat(), w.toFloat(), h.toFloat(), paint)
        }
        paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
        if (input != null) {
            canvas.drawBitmap(input, 0f, 0f, paint)
        }
        return output
    }
}