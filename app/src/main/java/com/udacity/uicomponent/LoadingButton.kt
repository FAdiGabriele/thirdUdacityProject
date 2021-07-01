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


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var textToDisplay = resources.getString(R.string.button_label)
    private var typedArray = context.obtainStyledAttributes(
        attrs,
        R.styleable.LoadingButton,
        defStyleAttr,
        0
    )


    private var progressValue = typedArray.getInt(R.styleable.LoadingButton_progressValue, 0)
    private var progressColor = typedArray.getColor(R.styleable.LoadingButton_progessColor, resources.getColor(R.color.colorPrimaryDark, null))

    //remember to call invalidate when you want to update the UI after user interact with this view
    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Inactive) { p, old, new ->
        when (new) {
            //this is the default state
            ButtonState.Inactive -> {
                progressValue = -1
                textToDisplay = resources.getString(R.string.button_label)
                invalidate()
            }
            ButtonState.Clicked -> {
                buttonState = ButtonState.Loading
            }
            ButtonState.Loading -> {
                textToDisplay = resources.getString(R.string.button_loading_label)
                invalidate()

            }
            ButtonState.Completed -> {
                progressValue = 100
                invalidate()

                Handler().postDelayed({
                    buttonState = ButtonState.Inactive
                }, 2000)

            }
        }
    }


    //used device size
    private val deviceHeight : Int
    private val deviceWidth : Int

    //Components for drawing the loading button
    private val rect = RectF( //basic shape of rect
        resources.getDimension(R.dimen.loading_button_left_value),
        resources.getDimension(R.dimen.loading_button_top_value)+ resources.getDimension(R.dimen.loading_button_bottom_value).toInt(),
        0f,
        resources.getDimension(R.dimen.loading_button_bottom_value) + resources.getDimension(R.dimen.loading_button_bottom_value).toInt()
    )

    private val rectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = resources.getColor(R.color.colorPrimary, null)
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color =  Color.WHITE
        textSize = resources.getDimension(R.dimen.loading_button_text_size)
        textAlign = Paint.Align.CENTER

    }

    private val progressRectPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = progressColor
    }

    private val penguinAnimator = animatePenguin(TimeUnit.SECONDS.toMillis(2))
    val penguinBitmap = BitmapFactory.decodeResource(context.resources,R.drawable.penguin_icon)


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(rect, rectPaint)

        if(progressValue in 1..100){
            val progressRect = createProgressRect()
            canvas.drawRect(progressRect, progressRectPaint)
        }

        canvas.drawText(
            textToDisplay,
            rect.centerX(),
            rect.centerY() + resources.getDimension(R.dimen.loading_button_top_value) / 1.5f,
            textPaint
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
        heightSize =  resources.getDimension(R.dimen.loading_button_bottom_value).toInt() +
                resources.getDimension(R.dimen.loading_button_top_value).toInt() +
                resources.getDimension(R.dimen.loading_button_bottom_value).toInt()
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
            resources.getDimension(R.dimen.loading_button_left_value),
            resources.getDimension(R.dimen.loading_button_top_value),
            progressDimension,
            resources.getDimension(R.dimen.loading_button_bottom_value)
        )
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

    private fun animatePenguin(animationDuration: Long) : ValueAnimator{
        val anim = ValueAnimator.ofInt(0, 100)
        anim.duration = animationDuration
        anim.interpolator = LinearInterpolator()
        anim.repeatMode = ValueAnimator.RESTART
        anim.repeatCount = ValueAnimator.INFINITE

        anim.addUpdateListener {

        }
        return anim
    }


    init {
        isClickable = true

        deviceWidth = Resources.getSystem().displayMetrics.widthPixels
        deviceHeight = Resources.getSystem().displayMetrics.heightPixels

        //In this way the rectangle is always regulated for each screen
        rect.right = deviceWidth - resources.getDimension(R.dimen.loading_button_left_value)
    }
}