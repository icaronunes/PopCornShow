package customview

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
import br.com.icaro.filme.R
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

class PhotoLoadingDelegate(private val context: Context) :
	ReadWriteProperty<View?, View> {
	private var value: View = TextView(context)

	override fun setValue(thisRef: View?, property: KProperty<*>, value: View) {
		this.value = value
	}

	override fun getValue(thisRef: View?, property: KProperty<*>): View {
		return ImageView(context).apply {
			setBackgroundResource(R.drawable.background_loading_shimmer)
			layoutParams = LinearLayout
				.LayoutParams(
					LayoutParams.MATCH_PARENT,
					resources.getDimensionPixelSize(R.dimen.poster_main_height)
				).apply {
				}
		}
	}
}