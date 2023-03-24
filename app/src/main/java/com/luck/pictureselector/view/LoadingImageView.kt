package com.luck.pictureselector.view

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import com.luck.pictureselector.R

class LoadingImageView: androidx.appcompat.widget.AppCompatImageView {

    var objectAnimator: ObjectAnimator? = null
    var autoStart: Boolean = true
    var duration: Int = DEFAULT_DURATION

    constructor(context: Context) : super(context) {
        initStyle(context, null)
    }

    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {
        initStyle(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int): super(context, attrs, defStyle) {
        initStyle(context, attrs, defStyle)
    }

    private fun initStyle(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) {
        attrs?.let {
            context.obtainStyledAttributes(it, R.styleable.LoadingImageView).apply {
                autoStart = getBoolean(R.styleable.LoadingImageView_autoStart, true)
                duration = getInteger(R.styleable.LoadingImageView_animationDuration,
                    DEFAULT_DURATION
                )
                recycle()
            }
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if(autoStart) {
            startAnimation()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if(autoStart || objectAnimator != null) {
            cancelAnimation()
        }
    }

    fun startAnimation() {
        objectAnimator = ObjectAnimator.ofFloat(this, "rotation", 0.0f, 359.0f)
        objectAnimator?.duration = duration.toLong()
        objectAnimator?.repeatCount = Animation.INFINITE
        objectAnimator?.repeatMode = ValueAnimator.RESTART
        objectAnimator?.interpolator = LinearInterpolator()
        objectAnimator?.start()
    }

    fun cancelAnimation() {
        objectAnimator?.end()
        objectAnimator = null
    }

    companion object {
        const val DEFAULT_DURATION = 1250
    }
}