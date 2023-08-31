package com.cesoft.cesbarcodetest.ui

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MotionEvent
import android.view.View
import com.google.mlkit.vision.barcode.common.Barcode

data class QrCodeViewModel(val barcode: Barcode, val error: Boolean) {
    var boundingRect: Rect = barcode.boundingBox!!
    var qrContent: String = ""
    private var qrCodeTouchCallback = { v: View, e: MotionEvent -> false} //no-op

    init {
        when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                qrContent = barcode.url!!.url!!
                qrCodeTouchCallback = { v: View, e: MotionEvent ->
                    if (e.action == MotionEvent.ACTION_DOWN && boundingRect.contains(e.x.toInt(), e.y.toInt())) {
                        val openBrowserIntent = Intent(Intent.ACTION_VIEW)
                        openBrowserIntent.data = Uri.parse(qrContent)
                        v.context.startActivity(openBrowserIntent)
                    }
                    true // return true from the callback to signify the event was handled
                }
            }
            else -> {
                qrContent = "Code: ${barcode.rawValue.toString()}"
            }
        }
    }
}

class QrCodeDrawable(private val qrCodeViewModel: QrCodeViewModel) : Drawable() {
    private val boundingRectPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.YELLOW
        strokeWidth = 5F
        alpha = 200
    }
    private val boundingRectErrorPaint = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.RED
        strokeWidth = 6F
        alpha = 200
    }

    private val contentRectPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.YELLOW
        alpha = 255
    }
    private val contentRectErrorPaint = Paint().apply {
        style = Paint.Style.FILL
        color = Color.RED
        alpha = 255
    }
    private val contentTextPaint = Paint().apply {
        color = Color.DKGRAY
        alpha = 255
        textSize = 36F
    }

    private val contentPadding = 25
    private var textWidth = contentTextPaint.measureText(qrCodeViewModel.qrContent).toInt()

    override fun draw(canvas: Canvas) {
        canvas.drawRect(
            qrCodeViewModel.boundingRect,
            if(qrCodeViewModel.error) boundingRectErrorPaint else boundingRectPaint
        )
        canvas.drawRect(
            Rect(
                qrCodeViewModel.boundingRect.left,
                qrCodeViewModel.boundingRect.bottom + contentPadding/2,
                qrCodeViewModel.boundingRect.left + textWidth + contentPadding*2,
                qrCodeViewModel.boundingRect.bottom + contentTextPaint.textSize.toInt() + contentPadding),
                if(qrCodeViewModel.error) contentRectErrorPaint else contentRectPaint
        )
        canvas.drawText(
            qrCodeViewModel.qrContent,
            (qrCodeViewModel.boundingRect.left + contentPadding).toFloat(),
            (qrCodeViewModel.boundingRect.bottom + contentPadding*2).toFloat(),
            contentTextPaint
        )
    }

    override fun setAlpha(alpha: Int) {
        boundingRectPaint.alpha = alpha
        contentRectPaint.alpha = alpha
        contentTextPaint.alpha = alpha
    }

    override fun setColorFilter(colorFiter: ColorFilter?) {
        boundingRectPaint.colorFilter = colorFilter
        contentRectPaint.colorFilter = colorFilter
        contentTextPaint.colorFilter = colorFilter
    }

    @Deprecated("Deprecated in Java")
    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT
}