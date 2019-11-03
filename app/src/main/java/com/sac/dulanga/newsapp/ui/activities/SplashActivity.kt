package com.sac.dulanga.newsapp.ui.activities


import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.view.animation.AnimationUtils
import com.sac.dulanga.newsapp.R
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*

/**
 * Created by chamithdulanga on 2019-11-02.
 */

class SplashActivity : Activity() {

    private  val TAG = this@SplashActivity.javaClass.simpleName


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            // Remove title bar
            this.requestWindowFeature(Window.FEATURE_NO_TITLE)
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

            // Remove notification bar
            this.window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
            setContentView(R.layout.activity_splash)

            val animZoomIn = AnimationUtils.loadAnimation(applicationContext, R.anim.zoom_in)

            img_logo.startAnimation(animZoomIn)


            Timer().schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        val intent = Intent(this@SplashActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }, 2000)

        } catch (ex: Exception) {
            Log.e(TAG, "onCreate: $ex")
        }

    }
}
