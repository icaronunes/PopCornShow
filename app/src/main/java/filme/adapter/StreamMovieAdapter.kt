package filme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.reelgood.Availability
import kotlinx.android.synthetic.main.sources_item_layout.view.icon_source
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import utils.setPicasso

class StreamMovieAdapter(private val avaliability: List<Availability>?) : RecyclerView.Adapter<StreamMovieAdapter.StreamMovieHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = StreamMovieHolder(parent)

    override fun getItemCount() = avaliability?.size ?: 0

    override fun onBindViewHolder(holder: StreamMovieHolder, position: Int) {
        holder.bind(avaliability?.get(position))
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_layout, parent, false)) {
        fun bind(avaliability: Availability?) = with(itemView) {
            icon_source.setPicasso("", img_erro = R.drawable.starz)
            source_sd.text = avaliability?.purchaseCostSd.toString()
            source_hd.text = avaliability?.purchaseCostHd.toString()
        }
    }
}


