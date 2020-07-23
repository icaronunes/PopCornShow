package applicaton

import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView

open class BaseFragment : Fragment(), LifecycleOwner {

	fun <T : AndroidViewModel> createViewModel(java: Class<T>): T {
		val factory = PopCornViewModelFactory(
			application = requireActivity().application,
			activity = requireActivity()
		)
		return ViewModelProviders.of(this.requireActivity(), factory).get(java)
	}

	fun setAdMob(adView: AdView) {
		val adRequest = AdRequest.Builder()
			// .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
			// .addTestDevice("8515241CF1F20943DD64804BD3C06CCB")  // An example device ID
			.build()
		adView.loadAd(adRequest)
	}
}
