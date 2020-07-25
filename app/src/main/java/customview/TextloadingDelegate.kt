package customview

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import br.com.icaro.filme.R
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class TextLoadingDelegate(
	private val context: Context,
	private val leftPadding: Int = 10,
	private val topPadding: Int = 10,
	private val rightPadding: Int = 10,
	private val bottomPadding: Int = 10,
	private val topM: Int = 10,
	private val rightM: Int = 10,
	private val bottomM: Int = 10
) : ReadWriteProperty<View?, View> {
	private var value: View = TextView(context)

	override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
		this.value = value
	}

	override fun getValue(thisRef: View?, property: KProperty<*>): View {
		return TextView(context).apply {
			setBackgroundResource(R.drawable.background_loading_shimmer)
			setPadding(leftPadding, topPadding, rightPadding, bottomPadding)
			layoutParams = LinearLayout
				.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
					setMargins(0, topM, rightM, bottomM)
				}
		}
	}
}