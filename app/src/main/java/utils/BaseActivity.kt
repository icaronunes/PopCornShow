package utils

import activity.BaseActivity
import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdView
import rx.subscriptions.CompositeSubscription

/**
 * Created by icaro on 27/08/17.
 */
@SuppressLint("Registered")
open class BaseActivityKt : BaseActivity() {

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
