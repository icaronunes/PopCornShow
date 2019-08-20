package utils

import androidx.appcompat.app.AppCompatActivity
import rx.subscriptions.CompositeSubscription

/**
 * Created by icaro on 27/08/17.
 */
class BaseActivityKt : AppCompatActivity() {

    protected var subscriptions = CompositeSubscription()

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
        subscriptions.unsubscribe()

    }
}