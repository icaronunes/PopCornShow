package busca.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import br.com.icaro.filme.R.drawable
import domain.ViewType
import domain.search.Result
import filme.activity.MovieDetailsActivity
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant
import utils.gone
import utils.loadPallet
import utils.setPicassoWithCache
import utils.visible

class SearchMovieAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup) = HolderSearch(parent)
    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as HolderSearch).bind(movie = item as Result)
    }

    inner class HolderSearch(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
        .inflate(R.layout.search_list_adapter, parent, false)) {

        private val poster: ImageView = itemView.findViewById(R.id.img_search)
        private val searchNome: TextView = itemView.findViewById(R.id.search_name)
        private val searchDataLancamento: TextView = itemView.findViewById(R.id.search_data_lancamento)
        private val searchVotoMedia: TextView = itemView.findViewById(R.id.search_voto_media)
        private val searchTitleOriginal: TextView = itemView.findViewById(R.id.search_title_original)
        private val groupStar: Group = itemView.findViewById(R.id.group_star)

        fun bind(movie: Result) = with(itemView) {
            poster.setPicassoWithCache(movie.posterPath, 4, img_erro = drawable.poster_empty)
            movie.title.let { searchNome.text = it }
            movie.originalTitle.let { searchTitleOriginal.text = it }

            itemView.setOnClickListener {
                context.startActivity(Intent(itemView.context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, poster.loadPallet())
                    putExtra(Constant.FILME_ID, movie.id)
                    putExtra(Constant.NOME_FILME, movie.title)
                })
            }

            movie.voteAverage.let {
                searchVotoMedia.text = if (it != 0f) {
                    groupStar.visible()
                    it.toString()
                } else {
                    groupStar.gone()
                    ""
                }
            }

            movie.releaseDate.let {
                searchDataLancamento.text =
                    if (it.length >= 4) it.substring(0, 4)
                    else {
                        if (it.isBlank()) {
                            searchDataLancamento.gone()
                            ""
                        } else {
                            it
                        }
                    }
            }
        }
    }
}