package fragment

import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import rx.subscriptions.CompositeSubscription

open class FragmentBase: Fragment() {
    
    protected var subscriptions = CompositeSubscription()
    
    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
        
    }
    
    override fun onPause() {
        super.onPause()
        subscriptions.unsubscribe()
        subscriptions.clear()
    }

    protected fun setAdMob(adView: AdView) {
        adView.loadAd(AdRequest.Builder()
                //.addTestDevice(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("8515241CF1F20943DD64804BD3C06CCB")  // An example device ID
                .build())
    }
}