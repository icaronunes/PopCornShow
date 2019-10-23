package filme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.ViewType
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_chip_layout.view.tv_price_stream
import kotlinx.android.synthetic.main.sources_item_layout.view.icon_source
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.getPricePurchase
import utils.setPicasso

class StreamMovieAmazonAdapterAdapter(val subscription: Boolean = false, val purchase: Boolean = false) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType, context: Context?) {
        (holder as StreamMovieHolder).bind(item as Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_chip_layout, parent, false)) {
        fun bind(availability: Availability) = with(itemView) {
            icon_source.setPicasso("", img_erro = R.drawable.amazon)
            if (!subscription) {
                tv_price_stream.apply {
                    text = if (purchase) availability.getPricePurchase() else "190.00 - 156.00" // availability.getPriceRental()
                    //if(text.length > 13) setTextSize(TypedValue.COMPLEX_UNIT_SP,  resources.getDimension(R.dimen.trade_txt_size_sp_12));

                    //   source_sd.text = if (purchase) "SD: ${availability.purchaseCostSd}" else "SD: ${availability.rentalCostSd}"
                }
            }
        }
    }
}