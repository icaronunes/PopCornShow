package utils

import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdView
import rx.subscriptions.CompositeSubscription

/**
 * Created by icaro on 27/08/17.
 */
open class BaseActivityKt : AppCompatActivity() {

    protected var subscriptions = CompositeSubscription()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
        subscriptions.unsubscribe()
    }

    fun setAdMob(adView: AdView) {
        adView.loadAd(com.google.android.gms.ads.AdRequest.Builder()
               // .addTestDevice(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("8515241CF1F20943DD64804BD3C06CCB")  // An example device ID
                .build())
    }
}