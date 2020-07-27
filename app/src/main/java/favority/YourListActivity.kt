package favority

import activity.BaseActivityAb
import android.os.Bundle


class YourList(override var layout: Int = Layout.activity_usuario_list) : BaseActivityAb() {

	val model: YourListViewModel by lazy { createViewModel(YourListViewModel::class.java, this) }

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)

	}

}