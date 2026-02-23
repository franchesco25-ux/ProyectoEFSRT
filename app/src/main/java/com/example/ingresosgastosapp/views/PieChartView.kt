package com.example.ingresosgastosapp.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class PieChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class Slice(val label: String, val value: Float, val color: Int)

    private var slices = listOf<Slice>()
    private var totalLabel = "$0"
    private val slicePaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val holePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#102216")
    }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#90cba4")
        textSize = 28f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        letterSpacing = 0.15f
    }

    private val rectF = RectF()

    companion object {
        val CHART_COLORS = intArrayOf(
            Color.parseColor("#0df259"),  // verde stitch
            Color.parseColor("#3b82f6"),  // azul
            Color.parseColor("#f59e0b"),  // ámbar
            Color.parseColor("#ec4899"),  // rosa
            Color.parseColor("#8b5cf6"),  // morado
            Color.parseColor("#14b8a6"),  // teal
            Color.parseColor("#f97316"),  // naranja
            Color.parseColor("#ef4444")   // rojo
        )
    }

    fun setData(data: List<Slice>, total: String) {
        slices = data
        totalLabel = total
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (slices.isEmpty()) {
            // Draw empty state
            val cx = width / 2f
            val cy = height / 2f
            val radius = minOf(cx, cy) * 0.85f
            slicePaint.color = Color.parseColor("#1A0df259")
            canvas.drawCircle(cx, cy, radius, slicePaint)
            val holeRadius = radius * 0.66f
            canvas.drawCircle(cx, cy, holeRadius, holePaint)
            labelPaint.textSize = 24f
            canvas.drawText("TOTAL", cx, cy - 10f, labelPaint)
            textPaint.textSize = 42f
            textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            canvas.drawText(totalLabel, cx, cy + 40f, textPaint)
            return
        }

        val cx = width / 2f
        val cy = height / 2f
        val radius = minOf(cx, cy) * 0.85f
        rectF.set(cx - radius, cy - radius, cx + radius, cy + radius)

        val total = slices.sumOf { it.value.toDouble() }.toFloat()
        if (total <= 0) return

        var startAngle = -90f
        for (slice in slices) {
            val sweepAngle = (slice.value / total) * 360f
            slicePaint.color = slice.color
            canvas.drawArc(rectF, startAngle, sweepAngle, true, slicePaint)
            startAngle += sweepAngle
        }

        // Draw center hole
        val holeRadius = radius * 0.66f
        canvas.drawCircle(cx, cy, holeRadius, holePaint)

        // Draw center text
        labelPaint.textSize = 24f
        canvas.drawText("TOTAL", cx, cy - 10f, labelPaint)

        textPaint.textSize = 42f
        textPaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText(totalLabel, cx, cy + 40f, textPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val size = resolveSize(500, widthMeasureSpec)
        setMeasuredDimension(size, size)
    }
}
