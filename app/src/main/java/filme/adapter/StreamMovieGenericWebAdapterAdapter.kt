package filme.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import domain.ViewType
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

class StreamMovieGenericWebAdapterAdapter(val subscription: Boolean, val purchase: Boolean, private val title: String? = "") : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(val parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = resources.getDrawable(getImg(availability), null)
            if (!subscription) {
                sourceSd = if (purchase) "SD: ${availability?.purchaseCostSd
                    ?: "--"}" else "SD: ${availability?.rentalCostSd ?: "---"}"
                sourceHd = if (purchase) "HD: ${availability?.purchaseCostHd
                    ?: "--"}" else "HD: ${availability?.rentalCostHd ?: "--"}"
            }
            setOnClickListener {
                try {
                    callWeb(availability, context)
                } catch (ex: Exception) {
                    Crashlytics.log("Erro no Stream - ${availability.toString()}")
                }
            }
        }
    }

    private fun getImg(availability: Availability?): Int {
        if (availability == null) return R.drawable.question
        return when (availability.sourceName) {
            "imdb_tv" -> R.drawable.imdb_tv
            "showtime" -> R.drawable.showtime
            "epix" -> R.drawable.epix
            "microsoft" -> R.drawable.microsoft
            "vudu" -> R.drawable.vudu
            "sony" -> R.drawable.sony
            "youtube_purchase" -> R.drawable.youtube
            "verizon_on_demand" -> R.drawable.verizon
            "mgo" -> R.drawable.mgo
            "itunes" -> R.drawable.itunes
            else -> R.drawable.question
        }
    }

    private fun getLink(availability: Availability?): String {
        if (availability == null) return ""
        val url = availability.sourceData?.links?.web
            ?: availability.sourceData?.links?.android
            ?: availability.sourceData?.links?.ios
        return url ?: ""
    }

    fun callWeb(availability: Availability?, context: Context) {
        try {
            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(getLink(availability))).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, null)
        } catch (e: Exception) {
            startActivity(context, Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/search?q=$title")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, null)
        }
    }
}
