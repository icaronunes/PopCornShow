package applicaton

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import kotlinx.android.synthetic.main.fragment_list_medias.*
import main.MainFragViewModel

open class BaseFragment : Fragment() {

    fun createViewModel(java: Class<MainFragViewModel>): MainFragViewModel {
        val factory = PopCornViewModelFactory(application = this.activity?.application as PopCornApplication)
        return ViewModelProviders.of(this, factory).get(java)
    }

    fun setAdMob(adView: AdView) {
        val adRequest = AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build()
        adView.loadAd(adRequest)
    }


}
