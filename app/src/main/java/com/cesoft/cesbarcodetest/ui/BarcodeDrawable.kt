package com.cesoft.cesbarcodetest.ui

import android.graphics.*
import android.graphics.drawable.Drawable
import com.google.mlkit.vision.barcode.common.Barcode

data class BarcodeData(val barcode: Barcode, val error: Boolean) {
    var boundingRect: Rect = barcode.boundingBox!!
    var qrContent: String = ""

    init {
        qrContent = when (barcode.valueType) {
            Barcode.TYPE_URL -> {
                barcode.url!!.url!!
            }
            else -> {
                barcode.rawValue.toString()
            }
        }
    }
}

class BarcodeDrawable(private val barcodeData: BarcodeData) : Drawable() {
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
    private var textWidth = contentTextPaint.measureText(barcodeData.qrContent).toInt()

    override fun draw(canvas: Canvas) {
        canvas.drawRect(
            barcodeData.boundingRect,
            if(barcodeData.error) boundingRectErrorPaint else boundingRectPaint
        )
        canvas.drawRect(
            Rect(
                barcodeData.boundingRect.left,
                barcodeData.boundingRect.bottom + contentPadding/2,
                barcodeData.boundingRect.left + textWidth + contentPadding*2,
                barcodeData.boundingRect.bottom + contentTextPaint.textSize.toInt() + contentPadding),
                if(barcodeData.error) contentRectErrorPaint else contentRectPaint
        )
        canvas.drawText(
            barcodeData.qrContent,
            (barcodeData.boundingRect.left + contentPadding).toFloat(),
            (barcodeData.boundingRect.bottom + contentPadding*2).toFloat(),
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