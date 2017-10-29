package fragment

import android.support.v4.app.Fragment
import rx.subscriptions.CompositeSubscription

open class FragmentBase: Fragment() {

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

}