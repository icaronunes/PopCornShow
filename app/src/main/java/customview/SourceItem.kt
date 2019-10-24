package customview

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import br.com.icaro.filme.R
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_layout.view.icon_source
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import site.Site
import utils.Constantes
import utils.gone
import kotlin.properties.Delegates

class SourceItem : FrameLayout {

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    init {
        View.inflate(context, R.layout.sources_item_layout, this)
    }

    var sourceSd: String? by Delegates.observable<String?>(null) { _, old: String?, new: String? ->
        if (new != old && !new.isNullOrBlank() && new != "SD: 0.0") {
            source_sd.apply {
                text = new
                visibility = View.VISIBLE
                if (new.length > 8) setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            }
        } else {
            source_sd.gone()
        }
    }

    var sourceHd: String? by Delegates.observable(null) { _, old: String?, new: String? ->
        if (new != old && !new.isNullOrBlank() && new != "HD: 0.0") {
            source_hd.apply {
                text = new
                visibility = View.VISIBLE
                if (new.length > 8) setTextSize(TypedValue.COMPLEX_UNIT_SP, 11f)
            }
        } else {
            source_hd.gone()
        }
    }

    var iconSource: Drawable? by Delegates.observable<Drawable?>(null) { _, _, newIcon ->
        icon_source.setImageDrawable(newIcon)
    }

    fun callAppOrWeb(availability: Availability, packagerCall: String, callActivity: (Availability) -> Unit) {
        val pack = context.packageManager.getLaunchIntentForPackage(packagerCall)
        if (pack != null) {
            try {
                val intent = Intent(Intent.ACTION_MAIN)
                intent.setClassName(
                    pack.component.packageName,
                    pack.component.className
                )
                intent.data = Uri.parse(availability.sourceData.links.android)
                context.startActivity(intent)
            } catch (e: Exception) {
                callActivity(availability)
            }
        } else {
            callActivity(availability)
        }
    }

    var link: String by Delegates.observable("") { _, _, linkWeb: String ->
        if (linkWeb.isNotBlank() && linkWeb.contains("http"))
            context.startActivity(Intent(context, Site::class.java).apply {
                putExtra(Constantes.SITE, linkWeb)
            })
    }
}