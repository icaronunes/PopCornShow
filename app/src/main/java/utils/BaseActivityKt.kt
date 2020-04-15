package utils

import activity.BaseActivity
import android.view.View
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import rx.subscriptions.CompositeSubscription

/**
 * Created by icaro on 27/08/17.
 */
open class BaseActivityKt : BaseActivity() {

    var subscriptions = CompositeSubscription()

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
        subscriptions.unsubscribe()
    }

    fun ops() {
        Toast.makeText(baseContext, getString(R.string.ops), Toast.LENGTH_LONG).show()
    }

    fun snack(anchor: View, txt: String,  block: () -> Unit = {}) {
        Snackbar.make(anchor, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { block() }.show()
    }
}
