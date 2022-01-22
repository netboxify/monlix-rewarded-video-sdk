package com.chromaticnoob.rewardedads.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import com.chromaticnoob.rewardedads.R

class RotateLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs) {
    private var angle: Int
    private val rotateMatrix = Matrix()
    private val viewRectRotated = Rect()
    private val tempRectF1 = RectF()
    private val tempRectF2 = RectF()
    private val viewTouchPoint = FloatArray(2)
    private val childTouchPoint = FloatArray(2)
    private var angleChanged = true

    /**
     * Returns current angle of this layout
     */
    fun getAngle(): Int {
        return angle
    }

    /**
     * Sets current angle of this layout.
     */
    fun setAngle(angle: Int) {
        if (this.angle != angle) {
            this.angle = angle
            angleChanged = true
            requestLayout()
            invalidate()
        }
    }

    /**
     * Returns this layout's child or null if there is no any
     */
    val view: View?
        get() = if (childCount > 0) {
            getChildAt(0)
        } else {
            null
        }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val child = view
        if (child != null) {
            if (Math.abs(angle % 180) == 90) {
                measureChild(child, heightMeasureSpec, widthMeasureSpec)
                setMeasuredDimension(
                    resolveSize(child.measuredHeight, widthMeasureSpec),
                    resolveSize(child.measuredWidth, heightMeasureSpec)
                )
            } else if (Math.abs(angle % 180) == 0) {
                measureChild(child, widthMeasureSpec, heightMeasureSpec)
                setMeasuredDimension(
                    resolveSize(child.measuredWidth, widthMeasureSpec),
                    resolveSize(child.measuredHeight, heightMeasureSpec)
                )
            } else {
                val childWithMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
                measureChild(child, childWithMeasureSpec, childHeightMeasureSpec)
                val measuredWidth = Math.ceil(
                    child.measuredWidth * Math.abs(Math.cos(angle_c())) + child.measuredHeight * Math.abs(
                        Math.sin(angle_c())
                    )
                ).toInt()
                val measuredHeight = Math.ceil(
                    child.measuredWidth * Math.abs(Math.sin(angle_c())) + child.measuredHeight * Math.abs(
                        Math.cos(angle_c())
                    )
                ).toInt()
                setMeasuredDimension(
                    resolveSize(measuredWidth, widthMeasureSpec),
                    resolveSize(measuredHeight, heightMeasureSpec)
                )
            }
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        val layoutWidth = r - l
        val layoutHeight = b - t
        if (angleChanged || changed) {
            val layoutRect = tempRectF1
            layoutRect[0f, 0f, layoutWidth.toFloat()] = layoutHeight.toFloat()
            val layoutRectRotated = tempRectF2
            rotateMatrix.setRotate(angle.toFloat(), layoutRect.centerX(), layoutRect.centerY())
            rotateMatrix.mapRect(layoutRectRotated, layoutRect)
            layoutRectRotated.round(viewRectRotated)
            angleChanged = false
        }
        val child = view
        if (child != null) {
            val childLeft = (layoutWidth - child.measuredWidth) / 2
            val childTop = (layoutHeight - child.measuredHeight) / 2
            val childRight = childLeft + child.measuredWidth
            val childBottom = childTop + child.measuredHeight
            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        canvas.save()
        canvas.rotate(-angle.toFloat(), width / 2f, height / 2f)
        super.dispatchDraw(canvas)
        canvas.restore()
    }

    override fun invalidateChildInParent(location: IntArray, dirty: Rect): ViewParent {
        invalidate()
        return super.invalidateChildInParent(location, dirty)
    }

    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        viewTouchPoint[0] = event.x
        viewTouchPoint[1] = event.y
        rotateMatrix.mapPoints(childTouchPoint, viewTouchPoint)
        event.setLocation(childTouchPoint[0], childTouchPoint[1])
        val result = super.dispatchTouchEvent(event)
        event.setLocation(viewTouchPoint[0], viewTouchPoint[1])
        return result
    }

    /**
     * Circle angle, from 0 to TAU
     */
    private fun angle_c(): Double {
        // True circle constant, not that petty imposter known as "PI"
        val TAU = 2 * Math.PI
        return TAU * angle / 360
    }

    init {
        val a = context.obtainStyledAttributes(attrs, R.styleable.RotateLayout)
        angle = a.getInt(R.styleable.RotateLayout_angle, 0)
        a.recycle()
        setWillNotDraw(false)
    }
}