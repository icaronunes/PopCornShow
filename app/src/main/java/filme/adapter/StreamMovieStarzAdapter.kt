package filme.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.ViewType
import domain.reelgood.Availability
import filme.adapter.StreamMovieDelegatesAdapter.Companion.starzPackage
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

class StreamMovieStarzAdapterAdapter(val subscription: Boolean = false, val purchase: Boolean = false) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType, context: Context?) {
        (holder as StreamMovieHolder).bind(item as Availability)
    }

    inner class StreamMovieHolder(val parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability) = with(itemView.source_item) {
            iconSource = resources.getDrawable(R.drawable.starz, null)
            if (!subscription) {
                sourceSd = if (purchase) "SD: ${availability.purchaseCostSd}" else "SD: ${availability.rentalCostSd}"
                sourceHd = if (purchase) "HD: ${availability.purchaseCostHd}" else "HD: ${availability.rentalCostHd}"
            }
            setOnClickListener {
                callAppOrWeb(availability, starzPackage) {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(it.sourceData.links.web)
                    context.startActivity(intent)
                }
            }
        }
    }
}