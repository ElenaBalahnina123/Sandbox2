package com.slobozhaninova.customview

import android.content.Context
import android.graphics.*
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.Layout
import android.text.StaticLayout
import android.text.TextPaint
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.withClip
import kotlin.math.roundToInt
import kotlin.properties.Delegates

class ExampleCustomView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    companion object {
        private const val RATIO = 0.8611
    }

    private var pixelSize: Float = 0.0f

    var data: ExampleCustomViewData = ExampleCustomViewData(
        background = ColorDrawable(Color.BLACK),
        icon = ColorDrawable(Color.RED),
        title = "title",
        subtitle = "subtitle",
        buttonText = "button"
    )
        set(value) {
            field = value
            invalidate()
        }

    private val titlePaint = Paint().apply {
        color = Color.WHITE
        isAntiAlias = true
    }

    private val buttonBkgPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
    }

    private val buttonTextPaint = Paint().apply {
        isAntiAlias = true
        color = Color.BLACK
    }

    private val iconClipPath = Path()

    private var subtitleStaticLayout: StaticLayout by Delegates.notNull()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val width = if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.UNSPECIFIED) {
            resources.displayMetrics.widthPixels
        } else {
            MeasureSpec.getSize(widthMeasureSpec)
        }
        setMeasuredDimension(
            MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY),
            MeasureSpec.makeMeasureSpec((width * RATIO).roundToInt(), MeasureSpec.EXACTLY)
        )
        pixelSize = width.toFloat() / 720f
        titlePaint.textSize = 48.pxF

        val subtitleWidth = (width - 32.pxF).roundToInt()

        subtitleStaticLayout = StaticLayout(
            data.subtitle,
            TextPaint(
                Paint().apply {
                    color = Color.WHITE
                    textSize = 30.pxF
                    isAntiAlias = true
                }
            ),
            subtitleWidth,
            Layout.Alignment.ALIGN_CENTER,
            1f,
            0f,
            false
        )
        buttonBkgPaint.shader = LinearGradient(
            0f,0f,width.toFloat(),0f,Color.parseColor("#F2994A"),Color.parseColor("#FBB601"),Shader.TileMode.CLAMP
        )
        buttonTextPaint.textSize = 28.pxF

        iconClipPath.reset()
        iconClipPath.addCircle(
            360.pxF,
            252.pxF,
            40.pxF,
            Path.Direction.CCW
        )
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        with(data) {
            background.setBounds(0, 0, width, height)
            background.draw(canvas)

            if(icon != null) {
                canvas.withClip(
                    clipPath = iconClipPath
                ) {
                    icon.setBounds(
                        320.px,
                        212.px,
                        (320 + 80).px,
                        (212 + 80).px,
                    )
                    icon.draw(canvas)
                }
            }

            canvas.drawText(
                title,
                (width - titlePaint.measureText(title)) / 2f,
                345.pxF,
                titlePaint,
            )
            canvas.save()
            canvas.translate(
                16.pxF,
                365.pxF
            )
            subtitleStaticLayout.draw(canvas)
            canvas.restore()

            canvas.drawRoundRect(
                216.pxF,
                460.pxF,
                (216 + 288).pxF,
                (460 + 72).pxF,
                36.pxF,
                36.pxF,
                buttonBkgPaint
            )

            val buttonTextLeftOffset = (width - buttonTextPaint.measureText(buttonText))/2f

            canvas.drawText(
                buttonText,
                buttonTextLeftOffset,
                505.pxF,
                buttonTextPaint,
            )
        }
    }

    private val Number.px: Int
        get() = (toFloat() * pixelSize).roundToInt()

    private val Number.pxF: Float
        get() = (toFloat() * pixelSize)
}

data class ExampleCustomViewData(
    val background: Drawable,
    val icon: Drawable?,
    val title: String,
    val subtitle: String,
    val buttonText: String,
)