package fragment

import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import rx.subscriptions.CompositeSubscription
import utils.UtilsKt
import utils.putString

open class FragmentBase : Fragment(), LifecycleOwner {

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
        UtilsKt.setAdMob(adView)
    }

    fun snackWithAction(anchor: View, txt: Any,  block: () -> Unit = {}) {
        Snackbar.make(anchor, txt.putString(requireContext()), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { block() }.show()
    }


    fun snack(anchor: View, txt: Any,  block: () -> Unit = {}) {
        Snackbar.make(anchor, txt.putString(requireContext()), Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.ok) { block() }.show()
    }
}
