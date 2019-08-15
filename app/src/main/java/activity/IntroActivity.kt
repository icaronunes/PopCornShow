package activity

import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.github.paolorotolo.appintro.AppIntro
import fragment.FirstSlide
import fragment.FiveSlide
import fragment.SecondSlide

/**
 * Created by icaro on 21/11/16.
 */

class IntroActivity : AppIntro() {

    // Please DO NOT override onCreate. Use init
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        val preferences = getSharedPreferences(INTRO, Context.MODE_PRIVATE)
        if (preferences.getBoolean(VISTO, false)) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        } else {
            addSlide(FirstSlide())
            addSlide(SecondSlide())
            addSlide(FiveSlide())
        }

        setBarColor(Color.parseColor("#3F51B5"))
        setSeparatorColor(Color.parseColor("#2196F3"))

        // Hide Skip button
        showSkipButton(true)

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true)
        setVibrateIntensity(30)
    }

    private fun finishIntro() {
        val pref = getSharedPreferences(INTRO, 0)
        val editor = pref.edit()
        editor.putBoolean(VISTO, true)
        editor.apply()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onSkipPressed(currentFragment: Fragment?) {
        // Do something when users tap on Skip button.
        super.onSkipPressed(currentFragment)
        finishIntro()
    }

    override fun onDonePressed(currentFragment: Fragment?) {
        super.onDonePressed(currentFragment)
        // Do something when users tap on Done button.
        finishIntro()
    }

    companion object {
        val INTRO = "intro"
        val VISTO = "visto"
    }
}
