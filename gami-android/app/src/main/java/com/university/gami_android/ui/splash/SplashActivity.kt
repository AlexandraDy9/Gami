package com.university.gami_android.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.university.gami_android.R
import com.university.gami_android.preferences.PreferenceHandler
import com.university.gami_android.ui.login.LoginActivity
import pl.bclogic.pulsator4droid.library.PulsatorLayout
import com.university.gami_android.ui.main.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    override fun onStart() {
        super.onStart()

        val handler = Handler()
        val runnable = Runnable {
            if (PreferenceHandler.getAuthorization().isEmpty()) {
                startActivity(Intent(applicationContext, LoginActivity::class.java))
            } else {
                startActivity(Intent(applicationContext, MainActivity::class.java))
            }
            finish()
        }
        handler.postDelayed(runnable, 2000)
    }
}