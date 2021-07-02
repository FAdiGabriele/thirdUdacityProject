package com.udacity.uicomponent

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.*
import android.os.Handler
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import com.udacity.R
import java.util.concurrent.TimeUnit
import kotlin.properties.Delegates


class CircleLoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textToDisplayToButton = ""
    private var typedArray = context.obtainStyledAttributes(
        attrs,
        R.styleable.LoadingButton,
        defStyleAttr,
        0
    )


    private var progressValue = typedArray.getInt(R.styleable.LoadingButton_progressValue, 0)
    private var progressColor = typedArray.getColor(R.styleable.LoadingButton_progessColor, resources.getColor(R.color.colorPrimaryDark, null))
    private var buttonText = typedArray.getString(R.styleable.LoadingButton_buttonText)
    private var loadingText = typedArray.getString(R.styleable.LoadingButton_loadingText)

    private var progressCircleValue = 0


    //remember to call invalidate when you want to update the UI after user interact with this view
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Inactive) { p, old, new ->
        when (new) {
            //this is the default state
            ButtonState.Inactive -> {
                progressValue = -1
                progressCircleValue = 0
                regolateButtonText()
                invalidate()
            }
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                regolateButtonText()
                invalidate()

            }
            ButtonState.Completed -> {
                progressValue = 100
                circleAnimator.pause()
                progressCircleValue = 360
                invalidate()

                Handler().postDelayed({
                    buttonState = ButtonState.Inactive
                }, 3000)

            }
        }
    }


    //used device size
    private val deviceHeight : Int
    private val deviceWidth : Int

    //Components for drawing the loading button
    private val rect = RectF( //basic shape of rect
        resources.getDimension(R.dimen.loading_button_left_value),
        resources.getDimension(R.dimen.loading_button_top_value),
        0f, //I set it in init
        resources.getDimension(R.dimen.loading_button_bottom_value)
    )

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorPrimary, null)
    }

    private val textWhitePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color =  Color.WHITE
        textSize = resources.getDimension(R.dimen.loading_button_text_size)
        textAlign = Paint.Align.CENTER

    }

    private val progressRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = progressColor
    }

    private val circlePaint = Paint()
    private val startAngle = 0f
    private val circleAnimator = animateCircle(TimeUnit.SECONDS.toMillis(10))
    private var mArea = RectF(
        0f, //I set it in init
        rect.top+ resources.getDimension(R.dimen.loading_button_top_value)/2,
        0f, //I set it in init
        rect.bottom - resources.getDimension(R.dimen.loading_button_top_value)/2
    )

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(rect, rectPaint)

        if(progressValue in 1..100){
            val progressRect = createProgressRect()
            canvas.drawRect(progressRect, progressRectPaint)
            circleAnimator.start()
           }else{
                circleAnimator.pause()
               progressCircleValue = 0

           }

        circlePaint.color = resources.getColor(R.color.colorAccent,null)
        canvas.drawArc(mArea, startAngle, progressCircleValue.toFloat(), true, circlePaint)

        canvas.drawText(
            textToDisplayToButton,
            rect.centerX(),
            rect.centerY() + resources.getDimension(R.dimen.loading_button_top_value) / 1.5f,
            textWhitePaint
        )

    }

    //It define how load button fits in layout
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = deviceWidth
        heightSize =  resources.getDimension(R.dimen.loading_button_bottom_value).toInt() + resources.getDimension(R.dimen.loading_button_top_value).toInt()
        setMeasuredDimension(widthSize, heightSize)
    }

    //It regolate the size of the view when it first appears and each time its size changes
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
    }

    //It execute the action when the button is clicked
    override fun performClick(): Boolean {
        /*
        I update here the buttonState and say it that it is clicked
         */
        buttonState = ButtonState.Clicked

        return super.performClick()
    }

    /**
     * It get the [progressValue] and create a darker rect that represent the actual state of the download
     */
    private fun createProgressRect() : RectF{
        val progressDimension = (progressValue * rect.right) /100

        return RectF(
            rect.left,
            rect.top,
            progressDimension,
            rect.bottom
        )
    }

    private fun animateCircle(animationDuration: Long) : ValueAnimator{
        val anim = ValueAnimator.ofInt(0, 360)
        anim.duration = animationDuration
        anim.interpolator = LinearInterpolator()
        anim.repeatMode = ValueAnimator.RESTART
        anim.repeatCount = ValueAnimator.INFINITE

        anim.addUpdateListener {
            if(progressCircleValue >= 360) progressCircleValue = 0
            progressCircleValue += 1
            invalidate()
        }
        return anim
    }

    fun setProgressValue(value: Int){
        progressValue = value
        invalidate()

        if(value == 100){
            buttonState = ButtonState.Completed
            invalidate()
        }
    }

    fun getProgressValue() : Int{
        return progressValue
    }


    init {
        isClickable = true

        deviceWidth = Resources.getSystem().displayMetrics.widthPixels
        deviceHeight = Resources.getSystem().displayMetrics.heightPixels

        //In this way the rectangle is always regulated for each screen
        rect.right = deviceWidth - resources.getDimension(R.dimen.loading_button_left_value)
        mArea.left = rect.right/1.5f + resources.getDimension(R.dimen.min_circle)
        mArea.right =  mArea.left + resources.getDimension(R.dimen.min_circle)

        regolateButtonText()

        typedArray.recycle()
    }

    fun regolateButtonText(){
        textToDisplayToButton = when{

            buttonText != null && buttonState == ButtonState.Inactive ->
                buttonText.toString()

            buttonText == null && buttonState == ButtonState.Inactive ->
                resources.getString(R.string.button_label)

            loadingText != null && buttonState == ButtonState.Loading ->
                loadingText.toString()

            loadingText == null && buttonState == ButtonState.Loading ->
                resources.getString(R.string.button_loading_label)

            else -> ""
        }
    }
}