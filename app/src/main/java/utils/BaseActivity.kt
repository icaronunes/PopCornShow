package utils

import android.support.v7.app.AppCompatActivity
import rx.subscriptions.CompositeSubscription

/**
 * Created by icaro on 27/08/17.
 */
open class BaseActivity : AppCompatActivity() {

    protected var subscriptions = CompositeSubscription()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }
}