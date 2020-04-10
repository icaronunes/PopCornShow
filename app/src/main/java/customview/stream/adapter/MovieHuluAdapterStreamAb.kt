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
import domain.ViewType
import domain.reelgood.movie.Availability
import customview.stream.BaseStreamAb
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream

class MovieHuluAdapterStreamAb(val subscription: Boolean = false,
    val purchase: Boolean = false,
    titleMedia: String = "",
    val type: TypeEnumStream):
    BaseStreamAb(), ViewTypeDelegateAdapter {

    override val typeStream: String = TypeStream.huluPackage
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = resources.getDrawable(R.drawable.hulu, null)
            if (!subscription) {
                source_sd.text = if (purchase) "SD: ${availability?.purchaseCostSd
                    ?: "--"}" else "SD: ${availability?.rentalCostSd ?: "--"}"
                source_hd.text = if (purchase) "HD: ${availability?.purchaseCostHd
                    ?: "--"}" else "SD: ${availability?.rentalCostHd ?: "--"}"
            }

            setOnClickListener {
                try {
                    callAppOrWeb(availability = availability, packagerCall = typeStream) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val link = getLink(availability)
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
        if (availability == null) return "https://www.hulu.com/welcome"
        val id = getSomeReference(availability, type)
        return if (id != null) {
            "http://www.hulu.com/watch/$id"
        } else "https://www.hulu.com/welcome"
    }


}