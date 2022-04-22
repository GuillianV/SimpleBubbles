package com.example.myapplication

import android.annotation.SuppressLint
import android.app.Service
import android.app.usage.UsageEvents
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.util.Log
import android.view.*
import android.view.View.OnTouchListener
import android.widget.FrameLayout
import android.view.MotionEvent
import com.example.myapplication.TapStatus


enum class TapStatus {
   Tap,Drag,Nothing
}

class MainService : Service()  {
    private var windowManager: WindowManager? = null
    private var floatyView: View? = null
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        addOverlayView()
    }



    @SuppressLint("WrongConstant")
    private fun addOverlayView() {
        val params: WindowManager.LayoutParams
        val layoutParamsType: Int
        layoutParamsType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_PHONE
        }
        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            layoutParamsType,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
            PixelFormat.TRANSLUCENT
        )

        params.gravity = Gravity.NO_GRAVITY
        params.x = 0
        params.y = 0
        val interceptorLayout: FrameLayout = object : FrameLayout(this) {


            private var initialX = 0
            private var initialY = 0
            private var initialTouchX = 0f
            private var initialTouchY = 0f
            private var dragVale = 0
            private var status= TapStatus.Nothing



            override fun onTouchEvent(event: MotionEvent?): Boolean {

                if (event?.action == MotionEvent.ACTION_OUTSIDE)
                    return super.onTouchEvent(event)

                else{

                    when (event!!.action) {
                        MotionEvent.ACTION_DOWN -> {
                            initialX = params.x
                            initialY = params.y
                            initialTouchX = event!!.rawX
                            initialTouchY = event!!.rawY

                        }
                        MotionEvent.ACTION_UP -> {

                            if (dragVale <=25)
                                status = TapStatus.Tap
                            else{
                                status = TapStatus.Drag
                            }

                            dragVale = 0
                        }
                        MotionEvent.ACTION_MOVE -> {
                            params.x = initialX + (event!!.rawX - initialTouchX).toInt()
                            params.y = initialY + (event!!.rawY - initialTouchY).toInt()
                            dragVale += Math.abs(initialX + (event!!.rawX - initialTouchX).toInt())
                            dragVale += Math.abs(initialY + (event!!.rawY - initialTouchY).toInt())
                            windowManager!!.updateViewLayout(floatyView, params)

                        }

                    }

                    interact(status)
                    return true

                }
            }

        }
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        if (inflater != null) {
            floatyView = inflater.inflate(R.layout.bubble,interceptorLayout)
            windowManager!!.addView(floatyView, params)
        } else {
            Log.e(
                "SAW-example",
                "Layout Inflater Service is null; can't inflate and display R.layout.floating_view"
            )
        }
    }

    //Close service
    override fun onDestroy() {
        super.onDestroy()
        if (floatyView != null) {
            windowManager!!.removeView(floatyView)
            floatyView = null
        }
    }

    fun interact(status: TapStatus){
        when (status) {
            TapStatus.Drag -> {

                //If bubble is drag

            }
            TapStatus.Tap -> {

                //If bubble is tap
                onDestroy()
            }
            else -> {

            }

        }

    }


}
