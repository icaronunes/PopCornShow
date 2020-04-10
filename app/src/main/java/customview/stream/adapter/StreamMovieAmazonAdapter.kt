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
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream.amazonPackage

class StreamMovieAmazonAdapter(val subscription: Boolean = false, val purchase: Boolean = false, type: TypeEnumStream) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(val parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = resources.getDrawable(R.drawable.amazon, null)
            if (!subscription) {
                sourceSd = if (purchase) "SD: ${availability?.purchaseCostSd
                    ?: "--"}" else "SD: ${availability?.rentalCostSd ?: "--"}"
                sourceHd = if (purchase) "HD: ${availability?.purchaseCostHd
                    ?: "--"}" else "HD: ${availability?.rentalCostHd ?: "--"}"
            }
            setOnClickListener {
                try {
                    callAppOrWeb(availability, amazonPackage) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val link = getLink(it)
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
        if (availability == null) return "https://www.amazon.com/"
        val id = availability.sourceData?.references?.web?.movieId
            ?: availability.sourceData?.references?.android?.movieId
            ?: availability.sourceData?.references?.ios?.movieId
        return if (id != null) {
            "https://www.amazon.com/gp/product/$id?tag=reelgood06-20"
        } else "https://www.amazon.com/"
    }
}