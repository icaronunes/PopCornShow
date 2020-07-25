package customview

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class CustomLoadingDelegate(val context: Context, val layout: Int) :
	ReadWriteProperty<View?, View> {
	private var value: View? = null

	override fun getValue(thisRef: View?, property: KProperty<*>): View {
		return ConstraintLayout.inflate(context, layout, null).apply {
		}
	}

	override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
		this.value = value
	}
}
