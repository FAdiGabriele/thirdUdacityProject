package com.udacity.uicomponent

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var internetOn = false

    private val valueAnimator = ValueAnimator()

    private var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, old, new ->
            when(new){
                ButtonState.Clicked->{
                    //TODO: fai partire l'animazione
                }
                ButtonState.Loading->{
                    //TODO: l'animazione è in corso
                    //TODO: l'animazione dura quanto il tempo di download, quindi bisogna osservare i tempi di download
                }
                ButtonState.Completed->{
                    //TODO: l'animazione si interrompe
                    //TODO: fa partire l'animazione del cerchio giallo
                    //TODO: fa la navigazione
                }
            }
    }

    /*
    I set the override of OnCLickListener because i want that every overload of the method setOnClickListener
    sets the value of buttonState
     */
    override fun setOnClickListener(l : OnClickListener?){
        super.setOnClickListener(l)
        /*
        I update here the buttonState and say it that it is clicked
         */
        buttonState = ButtonState.Clicked
    }

    fun setOnClickListener(l : OnClickListener? , something: Any? = null , internetOn : Boolean = true){
        this.setOnClickListener(l)

        /*
     It is necessary for understand if we need to start the animation without internet connection
      */
        this.internetOn = internetOn
    }


    init {

    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        //TODO: bisogna disegnare la figura che avrà l'aspetto di un bottone

    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}