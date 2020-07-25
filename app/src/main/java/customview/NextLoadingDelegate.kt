package customview

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import br.com.icaro.filme.R.layout
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class NextLoadingDelegate(val context: Context) : ReadWriteProperty<View?, View> {
	private var value: View? = null

	override fun getValue(thisRef: View?, property: KProperty<*>): View {
		return ConstraintLayout.inflate(context, layout.calendar_adapter_loading, null).apply {
		}
	}

	override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
		this.value = value
	}
}
