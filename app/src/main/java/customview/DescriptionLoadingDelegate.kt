package customview

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class DescriptionLoadingDelegate(val context: Context?) :
	ReadWriteProperty<View?, View?> {

	private val MARGINBOTTOMISCRIPTION = 60

	private var view: View? = null
	override fun getValue(thisRef: View?, property: KProperty<*>): View? {
		return LinearLayout(context).apply {
			orientation = LinearLayout.VERTICAL
			layoutParams =
				LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT
				)
			val label by TextLoadingDelegate(context)

			val description by TextLoadingDelegate(context).apply {
				layoutParams =
					LayoutParams(
						LayoutParams.MATCH_PARENT,
						LayoutParams.WRAP_CONTENT
					).apply {
						setMargins(0, 0, 0, MARGINBOTTOMISCRIPTION)
					}
			}
			addView(label)
			addView(description)
		}
	}

	override fun setValue(thisRef: View?, property: KProperty<*>, value: View?) {
		this.view = value
	}
}