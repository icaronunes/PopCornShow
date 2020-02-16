package fragment

import android.view.View
import androidx.fragment.app.Fragment
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_list_medias.frame_list_filme
import rx.subscriptions.CompositeSubscription
import utils.UtilsKt

open class FragmentBase : Fragment() {

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

    fun snack(anchor: View, txt: String,  block: () -> Unit = {}) {
        Snackbar.make(anchor, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { block() }.show()
    }
}
