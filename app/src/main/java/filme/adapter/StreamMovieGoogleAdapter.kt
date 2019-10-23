package filme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.ViewType
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_layout.view.icon_source
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import kotlinx.android.synthetic.main.sources_item_view.view.*
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.setPicasso

class StreamMovieGoogleAdapterAdapter(val subscription: Boolean = false, val purchase: Boolean = false) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType, context: Context?) {
        (holder as StreamMovieHolder).bind(item as Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability) = with(itemView.source_item) {
            iconSource = resources.getDrawable(R.drawable.google, null)
            if (!subscription) {
                sourceSd = if (purchase) "SD: ${availability.purchaseCostSd}" else "SD: ${availability.rentalCostSd}"
                sourceHd = if (purchase) "HD: ${availability.purchaseCostHd}" else "HD: ${availability.rentalCostHd}"
            }

            setOnClickListener {
                link = availability.sourceData.links.web
            }
        }
    }
}