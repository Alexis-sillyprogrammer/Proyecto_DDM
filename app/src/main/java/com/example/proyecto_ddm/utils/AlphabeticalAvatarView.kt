package com.example.proyecto_ddm.utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import com.example.proyecto_ddm.R
import kotlin.math.abs
import kotlin.math.min

class AlphabeticalAvatarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    enum class Shape { CIRCLE, ROUNDED_SQUARE }

    private var displayText: String = "?"
    private var avatarBgColor: Int = Color.GRAY
    private var letterColor: Int = Color.WHITE
    private var shape: Shape = Shape.CIRCLE
    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
    }

    private val drawBounds = RectF()
    private val textBounds = Rect()

    private val palette = intArrayOf(
        0xFFE53935.toInt(), 0xFFD81B60.toInt(), 0xFF8E24AA.toInt(),
        0xFF5E35B1.toInt(), 0xFF3949AB.toInt(), 0xFF1E88E5.toInt(),
        0xFF039BE5.toInt(), 0xFF00ACC1.toInt(), 0xFF00897B.toInt(),
        0xFF43A047.toInt(), 0xFF7CB342.toInt(), 0xFFF4511E.toInt(),
        0xFFFF8F00.toInt(), 0xFF6D4C41.toInt(), 0xFF546E7A.toInt()
    )

    init {
        attrs?.let {
            val ta = context.obtainStyledAttributes(it, R.styleable.AlphabeticalAvatarView)
            try {
                val name = ta.getString(R.styleable.AlphabeticalAvatarView_av_name) ?: ""
                letterColor = ta.getColor(R.styleable.AlphabeticalAvatarView_av_textColor, Color.WHITE)
                shape = if(ta.getInt(R.styleable.AlphabeticalAvatarView_av_shape, 0) == 1)
                    Shape.ROUNDED_SQUARE else Shape.CIRCLE

                val forcedColor = ta.getColor(R.styleable.AlphabeticalAvatarView_av_backgroundColor, Int.MIN_VALUE)

                if(forcedColor != Int.MIN_VALUE) setName(name, forceColor = forcedColor)
                else setName(name)

            } finally {
                ta.recycle()
            }
        }
    }

    fun setName(name: String, forceColor: Int? = null) {
        displayText =extractInitials(name)
        avatarBgColor = forceColor ?: colorForName(name)
        invalidate()
    }

    fun setAvatarBackgroundColor(color: Int) {
        avatarBgColor = color;
        invalidate()
    }

    fun setLetterColor(color: Int) {
        letterColor = color;
        invalidate()
    }

    fun setShape(s: Shape) {
        shape = s;
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val default = (56 * resources.displayMetrics.density).toInt()
        val width = resolveSize(default, widthMeasureSpec)
        val height = resolveSize(default, heightMeasureSpec)
        val size = min(width, height)
        setMeasuredDimension(size, size)
    }

    override fun onDraw(canvas: Canvas) {
        val width = width.toFloat()
        val height = height.toFloat()
        val cx = width / 2f
        val cy = height / 2f
        val radius = min(width, height) / 2f

        bgPaint.color = avatarBgColor

        when(shape) {
            Shape.CIRCLE -> canvas.drawCircle(cx, cy, radius, bgPaint)
            Shape.ROUNDED_SQUARE -> {
                drawBounds.set(0f, 0f, width, height)
                canvas.drawRoundRect(drawBounds, width * 0.25f, width * 0.25f, bgPaint)
            }
        }

        textPaint.textSize = radius * 0.80f
        textPaint.color = letterColor
        textPaint.getTextBounds(displayText, 0, displayText.length, textBounds)

        val textY = cy - (textBounds.top + textBounds.bottom) / 2f
        canvas.drawText(displayText, cx, textY, textPaint)
    }

    private fun extractInitials(name: String): String {
        if(name.isBlank()) return ""

        val parts = name.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }
        return when {
            parts.size >= 2 ->
                "${parts.first().first().uppercaseChar()}${parts.last().first().uppercaseChar()}"
            parts.size == 1 -> parts.first().first().uppercaseChar().toString()
            else -> "?"
        }
    }

    private fun colorForName(name: String): Int {
        if(name.isBlank()) return palette[0]

        val hash = name.fold(0) { acc, c -> acc * 31 + c.code }
        return palette[abs(hash) % palette.size]
    }
}