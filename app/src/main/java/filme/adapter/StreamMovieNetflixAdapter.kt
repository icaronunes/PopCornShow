package filme.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.ViewType
import kotlinx.android.synthetic.main.sources_item_layout.view.icon_source
import kotlinx.android.synthetic.main.sources_item_layout.view.source_hd
import kotlinx.android.synthetic.main.sources_item_layout.view.source_sd
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.setPicasso
import utils.visible

class StreamMovieNetflixAdapter(val subscription: Boolean = false, val purchase: Boolean = false) : ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType, context: Context?) {
        (holder as StreamMovieHolder).bind()
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_layout, parent, false)) {
        fun bind() = with(itemView) {
            icon_source.setPicasso("", img_erro = R.drawable.netflix)
            source_sd.visible()
            source_hd.visible()
        }
    }
}


