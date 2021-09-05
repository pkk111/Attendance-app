package com.pkk.android.attendance.customLayouts

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.View.OnClickListener
import android.view.animation.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.pkk.android.attendance.R
import com.pkk.android.attendance.interfaces.DeviceSelectedListener
import com.pkk.android.attendance.misc.Utils
import com.pkk.android.attendance.models.DetectedDevice
import com.pkk.android.attendance.models.DeviceModel
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.math.cos
import kotlin.math.sin

class PulseLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : RelativeLayout(context, attrs, defStyleAttr) {

    companion object {
        const val INFINITE = ObjectAnimator.INFINITE

        const val INTERP_LINEAR = 0
        const val INTERP_ACCELERATE = 1
        const val INTERP_DECELERATE = 2
        const val INTERP_ACCELERATE_DECELERATE = 3
    }


    private var layout_width = 0
    private var layout_height = 0

    private var count = 4
    private var duration = 7000
    private var repeatCount = INFINITE
    private var color = Utils.getColorFromResource(context, R.color.pulsecolor)
    private var interpolator = INTERP_LINEAR
    private var detectedDevicesList: ArrayList<DetectedDevice>
    private var detectedDevices: HashMap<String, DetectedDevice>
    private var deviceViews: HashMap<DetectedDevice, View>
    private var avatars = ArrayList<Int>()
    private var random: Random
    private var listener: DeviceSelectedListener? = null

    private var startFromScratch = true
    private var isStarted = false
    private var detectedDeviceView: RelativeLayout? = null
    private var views = ArrayList<View>()
    private var prevRadi = -1
    private var prevCoordinate = 0

    private val paint: Paint
    private var centerX = 0f
    private var centerY = 0f
    private var radius = 0f
    private var regions: ArrayList<Int?>

    private var animatorSet: AnimatorSet?
    private var animatorListener: Animator.AnimatorListener = object : Animator.AnimatorListener {
        override fun onAnimationStart(p0: Animator?) {
            isStarted = true
        }

        override fun onAnimationEnd(p0: Animator?) {
            isStarted = false
        }

        override fun onAnimationCancel(p0: Animator?) {
            isStarted = false
        }

        override fun onAnimationRepeat(p0: Animator?) {
        }
    }

    private val onClickListener = OnClickListener { view ->
        val device = view.getTag(R.id.TAG_DEVICE) as DeviceModel
        if (listener != null)
            listener!!.onDeviceSelected(device)
    }

    fun clearedDetectedDevices() {
        detectedDeviceView!!.removeAllViews()
        detectedDevicesList.clear()
        prevRadi = -1
    }

    fun addDetectedDevice(device: DeviceModel) {
        val toDegree = (getRandomCoordinatesInPercentage() / 100f) * (2 * Math.PI)
        val radi = getRandomRadius()
        val corX = (centerX + cos(toDegree) * regions[radi]!!).toFloat()
        val corY = (centerX + sin(toDegree) * regions[radi]!!).toFloat()
        addImageview(corX, corY, device)
    }

    fun removeDetectedDevice(endpoint: String) {
        if (detectedDevices.containsKey(endpoint)) {
            val device = detectedDevices[endpoint]
            val view = deviceViews[device]

            removeView(view)
            detectedDeviceView!!.removeView(view)
            detectedDevicesList.remove(device)
            deviceViews.remove(device)
            detectedDevices.remove(endpoint)
        }
    }

    private fun addImageview(corX: Float, corY: Float, device: DeviceModel) {
        val x = corX - (regions[0]!! / 2f)
        val y = corY - (regions[0]!! / 2f)
        if (!detectedDevicesList.contains(DetectedDevice(x, y))) {
            //Profile coordinates and functionality
            var params = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
            val linearLayout = LinearLayout(context)
            linearLayout.gravity = Gravity.CENTER
            linearLayout.x = x
            linearLayout.y = y
            linearLayout.setTag(R.id.TAG_DEVICE, device)
            linearLayout.orientation = LinearLayout.VERTICAL
            linearLayout.layoutParams = params
            linearLayout.setOnClickListener(onClickListener)

            //Profile Image
            params = LayoutParams(
                resources.getDimension(R.dimen.fourtyeight).toInt(),
                resources.getDimension(R.dimen.fourtyeight).toInt()
            )
            val avatar = ImageView(context)
            avatar.layoutParams = params
            avatar.setImageDrawable(
                resize(
                    Utils.getDrawableFromResource(
                        context,
                        getRandomAvatar()
                    ), regions[0]!!
                )
            )
            linearLayout.addView(avatar)

            //name of the device
            val name = TextView(context)
            name.setTextColor(color)
            name.layoutParams = layoutParams
            name.text = if (TextUtils.isEmpty(device.deviceName)) "UNKNOWN" else device.deviceName
            name.gravity = CENTER_HORIZONTAL
            name.textSize = 12F
            name.maxLines = 1
            linearLayout.addView(name)
            val detectedDevice = DetectedDevice(x, y)
            detectedDevices[device.endpointID] = detectedDevice
            deviceViews[detectedDevice] = linearLayout
            detectedDeviceView!!.addView(linearLayout)
            detectedDevicesList.add(detectedDevice)
        }
    }

    fun setListener(listener: DeviceSelectedListener) {
        this.listener = listener
    }

    fun setAvatars(avatars: ArrayList<Int>) {
        this.avatars = avatars
    }

    /**
     * Sets the current color of the pulse effect in integer
     * Takes effect immediately
     * Usage: Color.parseColor("<hex-value>") or getResources().getColor(R.color.colorAccent)
     *
     * @param color : an integer representation of color
    </hex-value> */
    fun setColor(color: Int) {
        if (color != color) {
            this.color = color
            paint.color = color
        }
    }

    /**
     * Start pulse animation.
     */
    @Synchronized
    fun start() {
        if (animatorSet == null || isStarted) {
            return
        }

        animatorSet!!.start()
        if (!startFromScratch) {
            val animators: java.util.ArrayList<Animator> = animatorSet!!.childAnimations
            for (animator in animators) {
                val objectAnimator = animator as ObjectAnimator
                val delay = objectAnimator.startDelay
                objectAnimator.startDelay = 0
                objectAnimator.currentPlayTime = duration - delay
            }
        }
    }

    @Synchronized
    fun stop() {
        if (animatorSet == null || !isStarted) {
            return
        }
        animatorSet!!.end()
    }

    init {
        val arr: TypedArray =
            context.theme.obtainStyledAttributes(attrs, R.styleable.PulseStyle, 0, 0)
        startFromScratch = true
        detectedDevicesList = ArrayList()
        detectedDevices = HashMap()
        deviceViews = HashMap()
        random = Random()

        //Loading the style attributes
        try {
            count = arr.getInt(R.styleable.PulseStyle_pulse_count, count)
            duration = arr.getInt(R.styleable.PulseStyle_pulse_duration, duration)
            repeatCount = arr.getInt(R.styleable.PulseStyle_pulse_repeat, repeatCount)
            startFromScratch =
                arr.getBoolean(R.styleable.PulseStyle_pulse_startFromScratch, startFromScratch)
            color = arr.getColor(R.styleable.PulseStyle_pulse_color, color)
            interpolator = arr.getInt(R.styleable.PulseStyle_pulse_interpolator, interpolator)
        } catch (e: Exception) {
            Log.e("PulseLayout", "error in getting style attributes, Error: $e")
        } finally {
            arr.recycle()
        }

        //creating paint
        paint = Paint()
        paint.isAntiAlias = true
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 8f
        paint.color = color

        regions = ArrayList()
        animatorSet = AnimatorSet()

        build()
    }

    /**
     * Build pulse views and animators.
     */
    private fun build() {
        val params = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        val animators = ArrayList<Animator>()
        for (i in 0 until count) {
            val pulseView = PulseView(context)
            pulseView.scaleX = 0f
            pulseView.scaleY = 0f
            pulseView.alpha = 1f
            addView(pulseView, i, params)
            views.add(pulseView)

            val delay = (i * duration / count).toLong()

            val animatorX = ObjectAnimator.ofFloat(pulseView, "scaleX", 0f, 1f)
            animatorX.repeatCount = repeatCount
            animatorX.repeatMode = ObjectAnimator.RESTART
            animatorX.startDelay = delay
            animators.add(animatorX)

            val animatorY = ObjectAnimator.ofFloat(pulseView, "scaleY", 0f, 1f)
            animatorY.repeatCount = repeatCount
            animatorY.repeatMode = ObjectAnimator.RESTART
            animatorY.startDelay = delay
            animators.add(animatorY)

            val animatorAlpha = ObjectAnimator.ofFloat(pulseView, "Alpha", 1f, 0f)
            animatorAlpha.repeatCount = repeatCount
            animatorAlpha.repeatMode = ObjectAnimator.RESTART
            animatorAlpha.startDelay = delay
            animators.add(animatorAlpha)
        }

        animatorSet?.playTogether(animators)
        animatorSet?.interpolator = createInterpolator(interpolator)
        animatorSet?.duration = duration.toLong()
        animatorSet?.addListener(animatorListener)

        detectedDeviceView = RelativeLayout(context)
        addView(detectedDeviceView, params)
    }

    /**
     * Reset views and animations.
     */
    fun reset() {
        prevRadi = -1

        clear()
        build()

        start()
    }

    /**
     * Clear pulse views and animators
     */
    private fun clear() {
        //Remove animator
        stop()

        //removing views
        for (v in views)
            removeView(v)
        views.clear()
    }

    private fun getRandomCoordinatesInPercentage(): Int {
        val percentage = random.nextInt(150) - 75
        if (percentage != prevCoordinate) {
            prevCoordinate = percentage
            return percentage + 25
        }
        return getRandomCoordinatesInPercentage()
    }

    private fun getRandomRadius(): Int {
        val radii = random.nextInt(regions.size - 2) + 1
        if (prevRadi != radii) {
            prevRadi = radii
            return radii
        }
        return getRandomRadius()
    }

    private fun getRandomAvatar(): Int {
        return avatars[random.nextInt(avatars.size)]
    }

    private fun resize(image: Drawable, size: Int): Drawable {
        val b = (image as BitmapDrawable).bitmap
        val bitmapResized = Bitmap.createScaledBitmap(b, size, size, false)
        return BitmapDrawable(resources, bitmapResized)
    }

    /**
     * Create interpolator from type.
     *
     * @param type Interpolator type as int
     * @return Interpolator object of type
     */
    private fun createInterpolator(type: Int): Interpolator {
        return when (type) {
            INTERP_ACCELERATE -> AccelerateInterpolator()
            INTERP_DECELERATE -> DecelerateInterpolator()
            INTERP_ACCELERATE_DECELERATE -> AccelerateDecelerateInterpolator()
            else -> LinearInterpolator()
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        layout_width = MeasureSpec.getSize(widthMeasureSpec) - paddingLeft - paddingRight
        layout_height = MeasureSpec.getSize(heightMeasureSpec) - paddingTop - paddingBottom
        centerX = layout_width * 0.5f
        centerY = layout_height * 0.5f
        radius = layout_width.coerceAtMost(layout_height) * 0.5f

        regions.clear()
        for (i in 1 until count)
            regions.add(((radius / count) * (i + 0.5)).toInt())

        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        if (animatorSet != null) {
            animatorSet!!.cancel()
            animatorSet = null
        }
    }

    inner class PulseView constructor(context: Context) : View(context) {
        override fun onDraw(canvas: Canvas) {
            canvas.drawCircle(centerX, centerY, radius, paint)
        }
    }

}