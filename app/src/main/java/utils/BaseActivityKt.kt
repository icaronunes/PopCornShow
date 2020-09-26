package utils

import activity.BaseActivity
import android.view.View
import android.widget.Toast
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import rx.subscriptions.CompositeSubscription

/**
 * Created by icaro on 27/08/17.
 */
open class BaseActivityKt : BaseActivity() {
    fun ops() {
        Toast.makeText(baseContext, getString(R.string.ops), Toast.LENGTH_LONG).show()
    }

    fun snack(anchor: View, txt: String = getString(R.string.no_internet), block: () -> Unit = {}) {
        Snackbar.make(anchor, txt, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) { block() }.show()
    }
}
