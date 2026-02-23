package com.example.ingresosgastosapp.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

class BarChartView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    data class BarGroup(val label: String, val value1: Float, val value2: Float)

    private var groups = listOf<BarGroup>()
    private var legend1 = "Ingresos"
    private var legend2 = "Gastos"

    private val barPaint1 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#0df259")
    }
    private val barPaint2 = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#ef4444")
    }
    private val labelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#66FFFFFF")
        textSize = 26f
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        letterSpacing = 0.05f
    }
    private val legendPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#99FFFFFF")
        textSize = 24f
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        letterSpacing = 0.1f
    }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#1AFFFFFF")
        strokeWidth = 2f
    }
    private val dotPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    fun setData(data: List<BarGroup>, legendLabel1: String = "Ingresos", legendLabel2: String = "Gastos") {
        groups = data
        legend1 = legendLabel1
        legend2 = legendLabel2
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val chartLeft = 20f
        val chartRight = width - 20f
        val chartTop = 20f
        val chartBottom = height - 90f // space for labels + legend
        val chartHeight = chartBottom - chartTop
        val labelsY = chartBottom + 36f
        val legendY = height - 10f

        if (groups.isEmpty()) {
            labelPaint.textSize = 28f
            labelPaint.textAlign = Paint.Align.CENTER
            canvas.drawText("Sin datos disponibles", width / 2f, height / 2f, labelPaint)
            return
        }

        // Draw horizontal grid lines
        for (i in 0..4) {
            val y = chartTop + (chartHeight / 4f) * i
            canvas.drawLine(chartLeft, y, chartRight, y, linePaint)
        }

        val maxVal = groups.maxOf { maxOf(it.value1, it.value2) }.coerceAtLeast(1f)
        val groupCount = groups.size
        val groupWidth = (chartRight - chartLeft) / groupCount
        val barWidth = groupWidth * 0.2f
        val barGap = groupWidth * 0.05f

        for ((i, group) in groups.withIndex()) {
            val centerX = chartLeft + groupWidth * i + groupWidth / 2f

            // Bar 1 (ingresos)
            val h1 = (group.value1 / maxVal) * chartHeight
            val bar1Left = centerX - barWidth - barGap / 2
            val bar1Right = bar1Left + barWidth
            val rect1 = RectF(bar1Left, chartBottom - h1, bar1Right, chartBottom)
            canvas.drawRoundRect(rect1, 4f, 4f, barPaint1)

            // Bar 2 (gastos)
            val h2 = (group.value2 / maxVal) * chartHeight
            val bar2Left = centerX + barGap / 2
            val bar2Right = bar2Left + barWidth
            val rect2 = RectF(bar2Left, chartBottom - h2, bar2Right, chartBottom)
            canvas.drawRoundRect(rect2, 4f, 4f, barPaint2)

            // Month label
            labelPaint.textSize = 22f
            labelPaint.textAlign = Paint.Align.CENTER
            canvas.drawText(group.label.uppercase(), centerX, labelsY, labelPaint)
        }

        // Legend
        val legendCenterX = width / 2f
        val dotRadius = 6f
        legendPaint.textSize = 22f

        // Legend 1
        dotPaint.color = Color.parseColor("#0df259")
        canvas.drawCircle(legendCenterX - 120f, legendY - 8f, dotRadius, dotPaint)
        legendPaint.textAlign = Paint.Align.LEFT
        canvas.drawText(legend1.uppercase(), legendCenterX - 108f, legendY, legendPaint)

        // Legend 2
        dotPaint.color = Color.parseColor("#ef4444")
        canvas.drawCircle(legendCenterX + 30f, legendY - 8f, dotRadius, dotPaint)
        canvas.drawText(legend2.uppercase(), legendCenterX + 42f, legendY, legendPaint)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = resolveSize(600, widthMeasureSpec)
        val h = resolveSize(500, heightMeasureSpec)
        setMeasuredDimension(w, h)
    }
}
