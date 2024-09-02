package com.slobozhaninova.customview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import kotlin.math.max
import kotlin.math.min
import kotlin.properties.Delegates


class CustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    //задает размеры view
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {


//  getMode мы вызываем для получения значений режима.
//  UNSPECIFIED – у родителя нет предпочтений к размеру view, размер может быть произвольным.
//  EXACTLY – родитель определил и передал точный размер view. View будет иметь этот размер независимо от того, какого размера view хочет быть. android:layout_width="64dp"
//  AT_MOST – родитель определил и передал верхнюю границу размера view. View может быть любого размера в пределах этой границы. wrap_content or also match_parent
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)


        //getSize мы вызываем для получения размера в пикселях
        val wightSizePx = MeasureSpec.getSize(widthMeasureSpec)
        val heightSizePx = MeasureSpec.getSize(widthMeasureSpec)

        val calculatedWidth = when (widthMode) {
            MeasureSpec.EXACTLY -> wightSizePx
            MeasureSpec.AT_MOST -> max(wightSizePx, heightSizePx)
            else -> {
                wightSizePx
            }
        }

        val calculatedHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSizePx
            MeasureSpec.AT_MOST -> max(wightSizePx, heightSizePx)
            else -> {
                heightSizePx
            }
        }

        setMeasuredDimension(calculatedWidth, calculatedHeight)
    }
    
}



//    // При работе с текстовыми Layout нужно использовать TextPaint вместо Paint
//    private val textPaint = TextPaint(Paint.ANTI_ALIAS_FLAG).apply { textSize = 80f }
//
//    // Layout, проводящий за нас измерения многострочного текста
//    private var textLayout: Layout? = null
//
//    // Здесь живет изменяющийся текст
//    private var editable: Editable = SpannableStringBuilder()
//
//    private val animator = ValueAnimator
//        .ofObject(StringEvaluator(), "Привет", "Привет! Как дела? Как настроение?")
//        .apply {
//            duration = 4000L
//            repeatCount = ValueAnimator.INFINITE
//            repeatMode = ValueAnimator.REVERSE
//
//            addUpdateListener { animator ->
//                val animatedValue = animator.animatedValue.toString()
//
//                // Нужно пересчитать размеры только если изменилось количество строк
//                val prevLineCount = textLayout?.lineCount
//                editable.replace(0, editable.length, animatedValue)
//                if (textLayout?.lineCount != prevLineCount) {
//                    requestLayout()
//                }
//
//                // Перерисоваться нужно в любом случае
//                invalidate()
//            }
//        }
//
//    init {
//        setBackgroundColor(Color.LTGRAY)
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        animator.start()
//    }
//
//    override fun onDetachedFromWindow() {
//        super.onDetachedFromWindow()
//        animator.cancel()
//    }
//
//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        // Не знаем максимальной ширины текста, займем всю доступную ширину
//        val widthSpecSize = MeasureSpec.getSize(widthMeasureSpec)
//        val width = if (widthSpecSize > 0) widthSpecSize else 500
//
//        val height = textLayout?.height ?: (textPaint.descent() - textPaint.ascent()).toInt()
//
//        setMeasuredDimension(width, height)
//    }
//
//    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
//        if (w == oldw) return
//
//        // Используем DynamicLayout т.к. текст меняется
//        textLayout = DynamicLayout.Builder
//            .obtain(editable, textPaint, w)
//            .build()
//    }
//
//    override fun onDraw(canvas: Canvas?) {
//        textLayout?.draw(canvas)
//    }
//}
//
//class StringEvaluator : TypeEvaluator<String> {
//
//    // fraction - значение, полученное из интерполятора
//    override fun evaluate(fraction: Float, startValue: String, endValue: String): String {
//        val coercedFraction = fraction.coerceIn(0f, 1f)
//
//        val lengthDiff = endValue.length - startValue.length
//        val currentDiff = (lengthDiff * coercedFraction).roundToInt()
//        return if (currentDiff > 0) {
//            endValue.substring(0, startValue.length + currentDiff)
//        } else {
//            startValue.substring(0, startValue.length + currentDiff)
//        }
//    }
