package customview

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.widget.FrameLayout
import br.com.icaro.filme.R
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.sources_item_layout.view.icon_source
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import kotlin.properties.Delegates

class SourceItem : FrameLayout {

	constructor(context: Context) : super(context)
	constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
	constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
		context,
		attrs,
		defStyleAttr
	)

	init {
		inflate(context, R.layout.sources_item_layout, this)
	}

	var sourceSd: String by Delegates.observable<String>("") { _, old: String, new: String ->
		if (new.isNotBlank() && new != "SD: 0.0") {
			source_sd.apply {
				text = new
			}
		} else {
			source_sd.text = "SD: --"
		}
	}

	var sourceHd: String by Delegates.observable("") { _, old: String, new: String ->
		if (new.isNotBlank() && new != "HD: 0.0") {
			source_hd.apply {
				text = new
			}
		} else {
			source_hd.text = "HD: --"
		}
	}

	var iconSource: Drawable? by Delegates.observable<Drawable?>(null) { _, _, newIcon ->
		icon_source.setImageDrawable(newIcon)
	}

	fun callAppOrWeb(
		availability: Availability?,
		packagerCall: String,
		callActivity: (Availability?) -> Unit
	) {
		val pack = if (packagerCall.isNotEmpty()) context.packageManager.getLaunchIntentForPackage(
			packagerCall
		) else null
		if (pack != null) {
			try {
				val intent = Intent(Intent.ACTION_MAIN)
				intent.setClassName(
					pack.component?.packageName ?: "",
					pack.component?.className ?: ""
				)
				intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
				intent.data = Uri.parse(availability?.sourceData?.links?.android)
				context.startActivity(intent)
			} catch (e: Exception) {
				callActivity(availability)
			}
		} else {
			callActivity(availability)
		}
	}
}