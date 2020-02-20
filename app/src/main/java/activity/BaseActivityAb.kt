package activity

import android.os.Bundle
import utils.BaseActivityKt

abstract class BaseActivityAb: BaseActivityKt() {

    protected abstract var layout: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
    }

}