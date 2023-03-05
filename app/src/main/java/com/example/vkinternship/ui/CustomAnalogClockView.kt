package com.example.vkinternship.ui

import android.content.Context
import android.graphics.*
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.Paint.Style
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import androidx.core.content.ContextCompat
import com.example.vkinternship.R
import java.util.*
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class CustomAnalogClockView @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0,
): View(context,attributeSet,defStyleAttr) {

    private var init: Boolean = false
    private lateinit var backgroundCirclePaint: Paint
    private lateinit var mainCirclePaint: Paint
    private lateinit var rimCirclePaint: Paint
    private lateinit var centerCirclePaint: Paint
    private lateinit var hourHandPaint: Paint
    private lateinit var textPaint: Paint
    private lateinit var minuteHandPaint: Paint
    private lateinit var secondHandPaint: Paint
    private var fontSize: Float = 0f
    private var radius: Float = 0f
    private val numbers = intArrayOf(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12)
    private val rect = Rect()
    private var minSize = 0f

    private fun initPaints(){
        init = true

        mainCirclePaint = Paint(ANTI_ALIAS_FLAG)
        mainCirclePaint.color = Color.BLACK
        mainCirclePaint.style = Style.STROKE
        mainCirclePaint.strokeWidth = TypedValue.
        applyDimension(TypedValue.COMPLEX_UNIT_DIP,1f,resources.displayMetrics) * MAIN_CIRCLE_WIDTH_COEFFICIENT * radius

        backgroundCirclePaint = Paint(ANTI_ALIAS_FLAG)
        backgroundCirclePaint.color = ContextCompat.getColor(context, BACKGROUND_COLOR)
        backgroundCirclePaint.style = Style.FILL

        rimCirclePaint = Paint(ANTI_ALIAS_FLAG)
        rimCirclePaint.color = Color.BLACK
        rimCirclePaint.style = Style.STROKE
        val dashPath = DashPathEffect(floatArrayOf(20f,5f), 0f)
        rimCirclePaint.pathEffect = dashPath
        rimCirclePaint.strokeWidth = TypedValue.
        applyDimension(TypedValue.COMPLEX_UNIT_DIP,2f,resources.displayMetrics)

        centerCirclePaint = Paint(ANTI_ALIAS_FLAG)
        centerCirclePaint.color = Color.BLACK
        centerCirclePaint.style = Style.FILL
        centerCirclePaint.strokeWidth = TypedValue.
        applyDimension(TypedValue.COMPLEX_UNIT_DIP,4f,resources.displayMetrics)

        textPaint = Paint(ANTI_ALIAS_FLAG)
        textPaint.color = ContextCompat.getColor(context, HAND_COLOR)
        val typeface = resources.getFont(R.font.abril_fatface)
        fontSize = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 1f,
            resources.displayMetrics
        )*radius* TEXT_COEFFICIENT
        textPaint.typeface = typeface
        textPaint.style = Style.FILL
        textPaint.textSize = fontSize

        hourHandPaint = Paint(ANTI_ALIAS_FLAG)
        hourHandPaint.color = ContextCompat.getColor(context, HAND_COLOR)
        hourHandPaint.style= Style.FILL
        hourHandPaint.strokeWidth = HAND_WIDTH_HOUR_COEFFICIENT * radius

        minuteHandPaint = Paint(ANTI_ALIAS_FLAG)
        minuteHandPaint.color = ContextCompat.getColor(context, HAND_COLOR)
        minuteHandPaint.style= Style.FILL
        minuteHandPaint.strokeWidth = HAND_WIDTH_MINUTE_COEFFICIENT * radius

        secondHandPaint = Paint(ANTI_ALIAS_FLAG)
        secondHandPaint.color = ContextCompat.getColor(context, SECOND_HAND_COLOR)
        secondHandPaint.style= Style.FILL
        secondHandPaint.strokeWidth = HAND_WIDTH_SECOND_COEFFICIENT * radius
    }



    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mWidth = (MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight).toFloat()
        val mHeight = (MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom).toFloat()
        minSize = min(mWidth,mHeight)
        radius = minSize/2f

        val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
        val minHeight = suggestedMinimumWidth + paddingTop + paddingBottom
        val desiredSizeInPixels = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, DESIRED_SIZE,resources.displayMetrics).toInt()
        val desiredWidth = max(minWidth,desiredSizeInPixels + paddingLeft + paddingRight)
        val desiredHeight = max(minHeight,desiredSizeInPixels + paddingTop + paddingBottom)
        setMeasuredDimension(
            resolveSize(desiredWidth, widthMeasureSpec),
            resolveSize(desiredHeight, heightMeasureSpec)
        )
    }


    override fun onDraw(canvas: Canvas) {
        if (radius <= 0) return
        if(!init) initPaints()
        drawMainCircle(canvas)
        drawHands(canvas)
        drawRimCircle(canvas)
        drawCenter(canvas)
        drawNumeral(canvas)

        postInvalidateDelayed(500)
        invalidate()
    }

    private fun drawHand(canvas: Canvas, loc: Double, handCode: Char) {
        val angle = Math.PI * loc / 30 - Math.PI / 2
        when(handCode){
            CODE_HOUR ->{
                val handRadius = radius * HOUR_LENGTH_SECOND_COEFFICIENT
                canvas.drawLine(
                    (width/2f), (height/2f),
                    (width/2f + cos(angle) * handRadius).toFloat(),
                    (height/2f + sin(angle) * handRadius).toFloat(),
                    hourHandPaint)
            }
            CODE_MINUTE -> {
                val handRadius = radius * HAND_LENGTH_MINUTE_COEFFICIENT
                canvas.drawLine(
                    (width/2f), (height/2f),
                    (width/2f + cos(angle) * handRadius).toFloat(),
                    (height/2f + sin(angle) * handRadius).toFloat(),
                    minuteHandPaint)
            }
            else -> {
                val handRadius = radius * HAND_LENGTH_SECOND_COEFFICIENT
                canvas.drawLine(
                    (width/2f), (height/2f),
                    (width/2f + cos(angle) * handRadius).toFloat(),
                    (height/2f + sin(angle) * handRadius).toFloat(),
                    secondHandPaint)
            }
        }
    }

    private fun drawHands(canvas: Canvas) {
        val c: Calendar = Calendar.getInstance()
        var hour: Float = c.get(Calendar.HOUR_OF_DAY).toFloat()
        hour = if (hour > 12) hour - 12 else hour
        drawHand(canvas, (hour + c.get(Calendar.MINUTE) / 60.0) * 5f, handCode = CODE_HOUR)
        drawHand(canvas, c.get(Calendar.MINUTE).toDouble(), handCode = CODE_MINUTE)
        drawHand(canvas, c.get(Calendar.SECOND).toDouble(), handCode = CODE_SECOND)
    }

    private fun drawNumeral(canvas: Canvas) {
        for (number in numbers) {
            val temp = number.toString()
            textPaint.getTextBounds(temp, 0, temp.length, rect)
            val angle: Double = Math.PI / 6 * (number - 3)
            val x : Float = (width/2f + cos(angle) * (radius * TEXT_PRESSING_COEFFICIENT) - rect.width() / 2).toFloat()
            val y: Float = (height/2f + sin(angle) * (radius * TEXT_PRESSING_COEFFICIENT) + rect.height() / 2).toFloat()
            canvas.drawText(temp, x, y, textPaint)
        }
    }

    private fun drawCenter(canvas: Canvas) {
        canvas.drawCircle((width/2f), (height/2f), CENTER_CIRCLE_COEFFICIENT * radius, centerCirclePaint)
    }

    private fun drawRimCircle(canvas: Canvas) {
        canvas.drawCircle(
            (width/2f),
            (height/2f),
            radius* RIM_CIRCLE_COEFFICIENT,
            rimCirclePaint
        )
    }

    private fun drawMainCircle(canvas: Canvas) {
        canvas.drawCircle(
            (width/2f),
            (height/2f),
            radius * MAIN_CIRCLE_COEFFICIENT,
            backgroundCirclePaint
        )

        canvas.drawCircle(
            (width/2f),
            (height/2f),
            radius * MAIN_CIRCLE_COEFFICIENT,
            mainCirclePaint
        )
    }

    companion object{
        const val SECOND_HAND_COLOR = R.color.dark_red
        const val HAND_COLOR = R.color.dark_gray
        const val BACKGROUND_COLOR = R.color.background
        const val CODE_SECOND = 's'
        const val CODE_MINUTE= 'm'
        const val CODE_HOUR= 'h'
        const val HAND_LENGTH_SECOND_COEFFICIENT = 0.7f
        const val HAND_LENGTH_MINUTE_COEFFICIENT = 0.5f
        const val HOUR_LENGTH_SECOND_COEFFICIENT = 0.3f
        const val DESIRED_SIZE = 400f
        const val TEXT_COEFFICIENT = 0.08f
        const val HAND_WIDTH_HOUR_COEFFICIENT = 0.065f
        const val HAND_WIDTH_MINUTE_COEFFICIENT = 0.035f
        const val HAND_WIDTH_SECOND_COEFFICIENT = 0.015f
        const val CENTER_CIRCLE_COEFFICIENT = 0.05f
        const val TEXT_PRESSING_COEFFICIENT = 0.75f
        const val RIM_CIRCLE_COEFFICIENT = 0.9f
        const val MAIN_CIRCLE_COEFFICIENT = 0.95f
        const val MAIN_CIRCLE_WIDTH_COEFFICIENT = 0.02f
    }
}