package com.udacity.uicomponent

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.util.Log
import android.widget.ProgressBar
import com.udacity.R

class FilledCircleProgressBar @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ProgressBar(context, attrs, defStyleAttr) {

   private var typedArray = context.obtainStyledAttributes(attrs, R.styleable.FilledCircleProgressBar, defStyleAttr, 0)

    var circleProgressValue : Float =  typedArray.getFloat(R.styleable.FilledCircleProgressBar_circleProgressValue, 0f)
        set(value) {
            field = value
            invalidate()
        }

    private val startAngle = 0f

    private var mArea = RectF(
            resources.getDimension(R.dimen.min_circle),
            resources.getDimension(R.dimen.min_circle),
            resources.getDimension(R.dimen.min_circle)+resources.getDimension(R.dimen.max_circle),
            resources.getDimension(R.dimen.min_circle) +resources.getDimension(R.dimen.max_circle))
    private var paint = Paint()

    override fun onDraw(canvas: Canvas) {
        paint.color = resources.getColor(R.color.colorAccent,null)
        canvas.drawArc(mArea, startAngle, circleProgressValue, true, paint)
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
        setMeasuredDimension((2*resources.getDimension(R.dimen.min_circle)+resources.getDimension(R.dimen.max_circle)).toInt(),
                (2*resources.getDimension(R.dimen.min_circle) +resources.getDimension(R.dimen.max_circle)).toInt())
    }

}