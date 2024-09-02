package com.slobozhaninova.customview

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.use
import androidx.core.graphics.withClip
import androidx.core.graphics.withTranslation
import kotlin.math.max
import kotlin.math.min

@SuppressLint("UseCompatLoadingForDrawables")
class CircleImageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var image: Drawable? = null
        set(value) {
            field = value
            updateBoundsAndPath()
            invalidate()
        }

    private val path = Path()

    init {
        context.obtainStyledAttributes(
            attrs,
            R.styleable.CircleImageView,
            defStyleAttr,
            0
        ).use {
            image = kotlin.runCatching {
                it.getDrawable(R.styleable.CircleImageView_image)
            }.getOrNull() ?: if (isInEditMode) {
                context.getDrawable(androidx.core.R.drawable.notification_bg)
            } else null
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val requiredWidthPx = MeasureSpec.getSize(widthMeasureSpec)

        val calculatedWidth = when (widthMode) {
            // Exactly - нужен именно тот размер, который указан
            MeasureSpec.EXACTLY -> requiredWidthPx
            MeasureSpec.AT_MOST -> max(requiredWidthPx, image?.bounds?.width() ?: 0)
            // unspecified
            else -> image?.bounds?.width() ?: 0
        }

        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val requiredHeightPx = MeasureSpec.getSize(heightMeasureSpec)

        val calculatedHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> requiredHeightPx
            MeasureSpec.AT_MOST -> max(requiredHeightPx, image?.bounds?.height() ?: 0)
            else -> image?.bounds?.height() ?: 0
        }

        val size = min(calculatedWidth, calculatedHeight)
            .coerceAtLeast(0)

        setMeasuredDimension(
            size,
            size
        )
        updateBoundsAndPath()
    }

    private fun updateBoundsAndPath() {
        updateBounds()
        updatePath()
    }

    private fun updateBounds() {
        val drawable = image ?: return
        if (measuredWidth == 0 || measuredHeight == 0) return

        val (currentWidth, currentHeight) = if (drawable.bounds.isEmpty) {
            val width = drawable.intrinsicWidth.takeIf { it > 0 } ?: measuredWidth
            val height = drawable.intrinsicHeight.takeIf { it > 0 } ?: measuredHeight
            width to height
        } else drawable.bounds.width() to drawable.bounds.height()

        val scale = max(
            measuredWidth / currentWidth.toFloat(),
            measuredHeight / currentHeight.toFloat(),
        )

        val bounds = Rect(
            0,
            0,
            (currentWidth * scale).toInt(),
            (currentHeight * scale).toInt(),
        )

        drawable.bounds = bounds
    }

    private fun updatePath() {
        val cornerRadiusPx = min(measuredWidth, measuredHeight) / 2f

        val width = measuredWidth //bounds.width()
        val height = measuredWidth //bounds.height()

        val radius = minOf(width / 2f, height / 2f, cornerRadiusPx.toFloat()).coerceAtLeast(0f)
        val doubleRadius = radius * 2

        path.reset()

        if (radius == 0f) return

        path.moveTo(radius, 0f)
        path.lineTo(width - radius, 0f)
        path.arcTo(width - doubleRadius, 0f, width.toFloat(), doubleRadius, 270f, 90f, false)
        path.lineTo(width.toFloat(), height - radius)
        path.arcTo(
            width - doubleRadius,
            height - doubleRadius,
            width.toFloat(),
            height.toFloat(),
            0f,
            90f,
            false
        )
        path.lineTo(radius, height.toFloat())
        path.arcTo(0f, height - doubleRadius, doubleRadius, height.toFloat(), 90f, 90f, false)
        path.lineTo(0f, radius)
        path.arcTo(0f, 0f, doubleRadius, doubleRadius, 180f, 90f, false)
        path.close()
    }

    override fun onDraw(canvas: Canvas) {
        canvas ?: return
        val drawable = image ?: return
        if (measuredWidth == 0 || measuredHeight == 0) return

        canvas.withClip(
            path
        ) {
            canvas.withTranslation(
                x = (measuredWidth - drawable.bounds.width()) / 2f,
                y = (measuredHeight - drawable.bounds.height()) / 2f
            ) {
                drawable.draw(this)
            }
        }
    }
}