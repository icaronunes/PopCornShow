package applicaton

import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import utils.putString

abstract class BaseFragment : Fragment(), LifecycleOwner {

	abstract val layout: Int

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

	fun snack(anchor: View, txt: Any, block: () -> Unit = {}) {
		Snackbar.make(anchor, txt.putString(requireContext()), Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.ok) { block() }.show()
	}

	fun ops() {
		Toast.makeText(requireContext(), getString(R.string.ops), Toast.LENGTH_LONG).show()
	}
}
