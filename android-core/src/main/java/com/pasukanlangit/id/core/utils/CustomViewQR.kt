package com.pasukanlangit.id.core.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import me.dm7.barcodescanner.core.ViewFinderView

class CustomViewQR constructor(context: Context) : ViewFinderView(context) {

    private val paint = Paint()
    private val scannerAlphaList = intArrayOf(0, 2, 4, 8, 16, 32, 64, 128, 192, 256, 192, 128, 64, 32, 16, 8, 4, 2)
    private var scannerAlpha: Int = 0
    private var cntr = 0
    private var goingup = false
    private val animationDelay = 10L

    init {
        paint.isAntiAlias = true
        setSquareViewFinder(true)
        setBorderColor(Color.parseColor("#F8FAFC"))
        setBorderCornerRadius(20)
        setLaserColor(Color.parseColor("#F8FAFC"))
        setMaskColor(Color.parseColor("#0FF8FAFC"))
        setLaserEnabled(true)
    }

    override fun drawLaser(canvas: Canvas?) {
        mLaserPaint.alpha = scannerAlphaList[scannerAlpha]
        scannerAlpha = (scannerAlpha + 1) % scannerAlphaList.size
        var viewMoving = framingRect.top
        viewMoving += cntr
        if (cntr < framingRect.height() && !goingup) {
            canvas?.drawRect(
                framingRect.left.toFloat(),
                (viewMoving - 2).toFloat(),
                framingRect.right.toFloat(),
                (viewMoving + 2).toFloat(),
                mLaserPaint
            )
            cntr += 4
        }

        if (cntr >= framingRect.height() && !goingup) goingup = true

        if (cntr > 0 && goingup) {
            canvas!!.drawRect(
                framingRect.left.toFloat(),
                (viewMoving - 2).toFloat(),
                framingRect.right.toFloat(),
                (viewMoving + 2).toFloat(),
                mLaserPaint
            )
            cntr -= 4
        }

        if (cntr <= 0 && goingup) goingup = false

        postInvalidateDelayed(animationDelay,
            framingRect.left,
            framingRect.top,
            framingRect.right,
            framingRect.bottom
        )
    }
}