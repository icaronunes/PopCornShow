package filme.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import customview.TypeEnumStream
import domain.ViewType
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.getNameTypeReel

class StreamMovieGenericWebAdapter(val subscription: Boolean, val purchase: Boolean, private val title: String = "", type: TypeEnumStream) : BaseStream(), ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)
    override val typeStream: String = ""
    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(val parent: ViewGroup) :
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            getImgStreamService(
                availability,
                onResource = {
            iconSource = resources.getDrawable(it, null)
                }
            ) {
                val imageView = ImageView(context)
                imageView.setImageBitmap(it)
                iconSource = imageView.drawable
            }
            if (!subscription) {
                sourceSd = if (purchase) "SD: ${availability?.purchaseCostSd
                    ?: "--"}" else "SD: ${availability?.rentalCostSd ?: "---"}"
                sourceHd = if (purchase) "HD: ${availability?.purchaseCostHd
                    ?: "--"}" else "HD: ${availability?.rentalCostHd ?: "--"}"
            }
            setOnClickListener { callWeb(availability, context) }
        }
    }

    fun callWeb(availability: Availability?, context: Context) {
        try {
            startActivity(context, Intent(Intent.ACTION_VIEW, Uri.parse(getLink(availability))).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, null)
        } catch (e: Exception) {
            Crashlytics.log("Error no Stream - ${title.getNameTypeReel()} ${availability.toString()}")
            startActivity(context, Intent(Intent.ACTION_VIEW,

                Uri.parse("https://www.google.com/search?q=$title ${availability?.sourceName ?: ""}")).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }, null)
        }
    }

    private fun getLink(availability: Availability?) = getSomeLink(availability, null)
        ?: getStreamWebLink(availability, title = title.getNameTypeReel())
}
