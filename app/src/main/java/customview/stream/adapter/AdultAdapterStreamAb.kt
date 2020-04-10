package customview.stream.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import customview.stream.TypeEnumStream
import customview.stream.TypeEnumStream.EP
import customview.stream.TypeEnumStream.MOVIE
import customview.stream.TypeEnumStream.TV
import domain.ViewType
import domain.reelgood.movie.Availability
import customview.stream.BaseStreamAb
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream
import utils.getNameTypeReel

class AdultAdapterStreamAb(
    val subscription: Boolean = false,
    val purchase: Boolean = false,
    private val titleMovie: String? = "",
    private val type: TypeEnumStream
) : BaseStreamAb(), ViewTypeDelegateAdapter {

    override val typeStream: String = TypeStream.adultswin
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = resources.getDrawable(R.drawable.adult_swim, null)
            if (!subscription) {
                sourceSd = if (purchase) "SD: ${availability?.purchaseCostSd
                    ?: "--"}" else "SD: ${availability?.rentalCostSd ?: "--"}"
                sourceHd = if (purchase) "HD: ${availability?.purchaseCostHd
                    ?: "--"}" else "HD: ${availability?.rentalCostHd ?: "--"}"
            }
            setOnClickListener {
                try {
                    callAppOrWeb(availability, typeStream) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val link = getSomeLink(availability, getLink(availability))
                        intent.data = Uri.parse(link)
                        context.startActivity(intent)
                    }
                } catch (ex: Exception) {
                    Crashlytics.log("Erro no Stream - ${availability.toString()}")
                }
            }
        }
    }

    private fun getLink(availability: Availability?): String {
        if (availability == null) return "http://www.adultswim.com/"
        if (availability.sourceData?.references == null) "http://www.adultswim.com/videos/${titleMovie?.getNameTypeReel()}"

        return when (type) {
            MOVIE -> "http://www.adultswim.com/videos/${getSomeReference(availability, MOVIE)}"
            TV -> "http://www.adultswim.com/videos/${getSomeReference(availability, TV)}"
            EP -> "http://www.adultswim.com/videos/${getSomeReference(availability, TV)}/${getSomeReference(availability, EP)}"
        }
    }
}