package applicaton

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import applicaton.PopCornApplication
import applicaton.PopCornViewModelFactory
import br.com.icaro.filme.R
import com.google.ads.AdRequest
import com.google.android.gms.ads.AdView
import main.MainFragViewModel

open class BaseFragment : Fragment() {

    fun createViewModel(java: Class<MainFragViewModel>): MainFragViewModel {
        val factory = PopCornViewModelFactory(application = this.activity?.application as PopCornApplication)
        return ViewModelProviders.of(this, factory).get(java)
    }

    fun setAdMob(adView: AdView) {
        adView.loadAd(com.google.android.gms.ads.AdRequest.Builder()
                .addTestDevice(com.google.android.gms.ads.AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build())
    }

}
