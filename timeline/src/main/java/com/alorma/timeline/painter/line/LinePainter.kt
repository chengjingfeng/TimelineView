package com.alorma.timeline.painter.line

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import com.alorma.timeline.AttributesUtils
import com.alorma.timeline.R
import com.alorma.timeline.painter.Painter
import com.alorma.timeline.property.*

class LinePainter(context: Context) : Painter {

    private val linearPainter: LineStylePainter = LinearLinePainter()
    private val dashedPainter: LineStylePainter = DashedLinePainter()

    private var currentPainter: LineStylePainter = linearPainter

    private var lineWidth: Float = context.resources.getDimensionPixelOffset(R.dimen.default_lineWidth).toFloat()

    private var lineColor: Int = AttributesUtils.colorPrimary(context, Color.GRAY)

    lateinit var paint: Paint

    private fun createPaint(): Paint = Paint().apply {
        flags = Paint.ANTI_ALIAS_FLAG
        color = lineColor
        strokeWidth = lineWidth
        style = Paint.Style.STROKE
        currentPainter.paintLineStyle(this)
    }

    private var validVerticalPositions: List<TimelinePositionOption> = listOf(
            TimelinePositionOption.POSITION_TOP,
            TimelinePositionOption.POSITION_CENTER,
            TimelinePositionOption.POSITION_CENTER_VERTICAL,
            TimelinePositionOption.POSITION_BOTTOM
    )

    private var validHorizontalPositions: List<TimelinePositionOption> = listOf(
            TimelinePositionOption.POSITION_START,
            TimelinePositionOption.POSITION_CENTER,
            TimelinePositionOption.POSITION_CENTER_HORIZONTAL,
            TimelinePositionOption.POSITION_END
    )
    private var verticalPosition: TimelinePositionOption = TimelinePositionOption.POSITION_CENTER_VERTICAL
    private var horizontalPosition: TimelinePositionOption = TimelinePositionOption.POSITION_CENTER_HORIZONTAL

    override fun initProperties(typedArray: TypedArray) {
        readLineStyle(typedArray)
        readLineWidth(typedArray)
        readLineColor(typedArray)

        paint = createPaint()
    }

    private fun readLineStyle(typedArray: TypedArray) {
        val style = typedArray.getInt(R.styleable.TimelineView_timeline_lineStyle, STYLE_LINEAR)
        currentPainter = getLineStylePainter(style)
    }

    private fun readLineWidth(typedArray: TypedArray) {
        lineWidth = typedArray.getDimension(R.styleable.TimelineView_timeline_lineWidth,
                lineWidth)
    }

    private fun readLineColor(typedArray: TypedArray) {
        lineColor = typedArray.getColor(R.styleable.TimelineView_timeline_lineColor,
                lineColor)
    }

    override fun <T> updateProperty(property: Property<T>) {
        when (property) {
            is LineStyle -> currentPainter = getLineStylePainter(property)
            is LineColor -> lineColor = property.lineColor
            is LineWidth -> lineWidth = property.lineWidth
            is TimelinePosition -> {

            }
        }
        paint = createPaint()
    }

    private fun getLineStylePainter(style: Int): LineStylePainter {
        val lineStyle = when (style) {
            STYLE_DASHED -> LineStyle.DASHED
            else -> LineStyle.LINEAR
        }
        return getLineStylePainter(lineStyle)
    }

    private fun getLineStylePainter(property: LineStyle): LineStylePainter = when (property) {
        is LineStyle.LINEAR -> linearPainter
        is LineStyle.DASHED -> dashedPainter
    }

    override fun draw(canvas: Canvas, rect: Rect) {
        currentPainter.draw(canvas, rect, paint)
    }

    companion object {
        const val STYLE_LINEAR = -1
        const val STYLE_DASHED = 0
    }
}