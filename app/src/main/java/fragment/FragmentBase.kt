package fragment

import android.view.View
import androidx.fragment.app.Fragment
import br.com.icaro.filme.R
import com.github.clans.fab.FloatingActionMenu
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import rx.subscriptions.CompositeSubscription
import utils.UtilsKt
import utils.putString

open class FragmentBase : Fragment() {

    protected var subscriptions = CompositeSubscription()
    protected lateinit var fab_menu: FloatingActionMenu

    override fun onResume() {
        super.onResume()
        fab_menu = requireActivity().findViewById(R.id.fab_menu)
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
