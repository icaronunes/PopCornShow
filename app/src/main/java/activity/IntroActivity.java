package activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;

import fragment.FirstSlide;
import fragment.FiveSlide;
import fragment.SecondSlide;

/**
 * Created by icaro on 21/11/16.
 */

public class IntroActivity extends AppIntro {

    public static final String INTRO = "intro";
    public static final String VISTO = "visto";


    // Please DO NOT override onCreate. Use init
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // Add your slide's fragments here
        // AppIntro will automatically generate the dots indicator and buttons.
        SharedPreferences preferences = getSharedPreferences(INTRO, Context.MODE_PRIVATE);
        boolean valor = preferences.getBoolean(VISTO, false);
        if (valor ){
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {

            addSlide(new FirstSlide());
            addSlide(new SecondSlide());
           // addSlide(new ThirdSlide()); netflix
            addSlide(new FiveSlide());
        }

        // OPTIONAL METHODS
        // Override bar/separator color
        setBarColor(Color.parseColor("#3F51B5"));
        setSeparatorColor(Color.parseColor("#2196F3"));

        // Hide Skip button
        showSkipButton(true);

        // Turn vibration on and set intensity
        // NOTE: you will probably need to ask VIBRATE permesssion in Manifest
        setVibrate(true);
        setVibrateIntensity(30);
    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        // Do something when users tap on Skip button.
        super.onSkipPressed(currentFragment);
        SharedPreferences pref = getSharedPreferences(INTRO, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(VISTO, true);
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        // Do something when users tap on Done button.
        SharedPreferences pref = getSharedPreferences(INTRO, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean(VISTO, true);
        editor.apply();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}
