package customview.stream.adapter

import Txt
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import customview.stream.BaseStreamAb
import customview.stream.TypeEnumStream
import domain.ViewType
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream

class MovieHuluAdapterStreamAb(val subscription: Boolean = false,
    val purchase: Boolean = false,
    val type: TypeEnumStream):
    BaseStreamAb(), ViewTypeDelegateAdapter {

    override val typeStream: String = TypeStream.huluPackage
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = ContextCompat.getDrawable(context, R.drawable.hulu)
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
                    Toast.makeText(context, context.getString(Txt.ops), Toast.LENGTH_SHORT).show()
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