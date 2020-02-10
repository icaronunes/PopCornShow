package utils

import activity.BaseActivity
import android.annotation.SuppressLint
import android.widget.Toast
import br.com.icaro.filme.R
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
}
