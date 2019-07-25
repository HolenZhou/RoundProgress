package com.zjlib.workout.ui.roundprogress

/**
 * com.popularapp.instaget.common
 *
 * @author 朱啸原 Create at 16/3/18 10:11
 */

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View


class RoundProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) :
    View(context, attrs, defStyle) {
    /**
     * 画笔对象的引用
     */
    private val paint: Paint = Paint()

    /**
     * 圆环的颜色
     */
    private var circeColor: Int = 0

    /**
     * 圆环进度的颜色
     */
    private var circleProgressColor = 0

    /**
     * 中间进度百分比的字符串的颜色
     */
    private var textColor: Int = 0

    /**
     * 中间进度百分比的字符串的字体
     */
    private var textSize: Float = 0.toFloat()

    /**
     * 圆环的宽度
     */
    private var roundWidth: Float = 0.toFloat()

    /**
     * 最大进度
     */
    private var max = 0

    /**
     * 当前进度
     * 获取进度.需要同步
     * 设置进度，此为线程安全控件，由于考虑多线程的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     */
    @get:Synchronized
    var progress: Int = 0
        @Synchronized set(progress) {
            var progress = progress
            if (progress < 0) {
                throw IllegalArgumentException("progress not less than 0")
            }
            if (progress > max) {
                progress = max
            }
            if (progress <= max) {
                field = progress
                postInvalidate()
            }

        }
    /**
     * 是否显示中间的进度
     */
    private val textIsDisplayable: Boolean

    /**
     * 进度的风格，实心或者空心
     */
    private val style: Int

    init {


        val mTypedArray = context.obtainStyledAttributes(
            attrs,
            R.styleable.RoundProgressBar
        )

        //获取自定义属性和默认值
        circeColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundColor, Color.RED)
        circleProgressColor = mTypedArray.getColor(R.styleable.RoundProgressBar_roundProgressColor, Color.GREEN)
        textColor = mTypedArray.getColor(R.styleable.RoundProgressBar_textColor, Color.GREEN)
        textSize = mTypedArray.getDimension(R.styleable.RoundProgressBar_textSize, 15f)
        roundWidth = mTypedArray.getDimension(R.styleable.RoundProgressBar_roundWidth, 5f)
        max = mTypedArray.getInteger(R.styleable.RoundProgressBar_max, 100)
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.RoundProgressBar_textIsDisplayable, true)
        style = mTypedArray.getInt(R.styleable.RoundProgressBar_style, 0)

        mTypedArray.recycle()
    }


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        /**
         * 画最外层的大圆环
         */
        val centre = width / 2 //获取圆心的x坐标
        val radius = (centre - roundWidth / 2).toInt() //圆环的半径
        paint.color = circeColor //设置圆环的颜色
        paint.style = Paint.Style.STROKE //设置空心
        paint.strokeWidth = roundWidth //设置圆环的宽度
        paint.isAntiAlias = true  //消除锯齿
        canvas.drawCircle(centre.toFloat(), centre.toFloat(), radius.toFloat(), paint) //画出圆环

        /**
         * 画圆弧 ，画圆环的进度
         */

        paint.color = circleProgressColor  //设置进度的颜色

        when (style) {
            STROKE -> {
                val oval = RectF(
                    (centre - radius).toFloat(),
                    (centre - radius).toFloat(),
                    (centre + radius).toFloat(),
                    (centre + radius).toFloat()
                )  //用于定义的圆弧的形状和大小的界限
                paint.strokeWidth = roundWidth //设置圆环的宽度
                paint.style = Paint.Style.STROKE
                canvas.drawArc(oval, -90f, (360 * this.progress / max).toFloat(), false, paint)  //根据进度画圆弧
            }
            FILL -> {
                val ovalFill = RectF(
                    (centre - centre).toFloat(),
                    (centre - centre).toFloat(),
                    (centre + centre).toFloat(),
                    (centre + centre).toFloat()
                )  //用于定义的圆弧的形状和大小的界限
                paint.style = Paint.Style.FILL
                paint.strokeWidth = roundWidth
                if (this.progress != 0)
                    canvas.drawArc(ovalFill, -90f, (360 * this.progress / max).toFloat(), true, paint)  //根据进度画圆弧
            }
        }
        /**
         * 画进度百分比
         */
        if (textIsDisplayable) {
            paint.style = Paint.Style.FILL
            paint.strokeWidth = 0f
            paint.color = textColor
            paint.textSize = textSize
            val font = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
            paint.typeface = font
            val percent = (this.progress.toFloat() / max.toFloat() * 100).toInt()  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
            val textWidth = paint.measureText("$percent%")   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间
            canvas.drawText("$percent%", centre - textWidth / 2, centre + textSize * 2 / 5, paint) //画出进度百分比
        }
    }


    @Synchronized
    fun getMax(): Int {
        return max
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    @Synchronized
    fun setMax(max: Int) {
        if (max < 0) {
            throw IllegalArgumentException("max not less than 0")
        }
        this.max = max
    }

    companion object {

        val STROKE = 0
        val FILL = 1
    }


}

