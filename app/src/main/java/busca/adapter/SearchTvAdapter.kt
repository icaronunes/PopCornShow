package busca.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import domain.ViewType
import domain.search.Result
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.getNameTypeReel
import utils.gone
import utils.setPicassoWithCache
import utils.yearDate

class SearchTvAdapter : ViewTypeDelegateAdapter {
	override fun onCreateViewHolder(parent: ViewGroup) = HolderSearch(parent)
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as HolderSearch).bind(series = item as Result)
	}

	inner class HolderSearch(parent: ViewGroup) : RecyclerView.ViewHolder(
		LayoutInflater.from(parent.context)
			.inflate(R.layout.search_list_adapter, parent, false)
	) {
		private val poster: ImageView = itemView.findViewById(R.id.img_search)
		private val searchNome: TextView = itemView.findViewById(R.id.search_name)
		private val searchDataLancamento: TextView =
			itemView.findViewById(R.id.search_data_lancamento)
		private val searchVotoMedia: TextView = itemView.findViewById(R.id.search_voto_media)
		private val searchTitleOriginal: TextView =
			itemView.findViewById(R.id.search_title_original)
		private val groupStar: Group = itemView.findViewById(R.id.group_star)

		fun bind(series: Result) = with(itemView) {
			poster.setPicassoWithCache(series.posterPath, 4, img_erro = R.drawable.poster_empty)
			series.originalName.let { searchTitleOriginal.text = it }
			series.name.let { searchNome.text = it }

			setOnClickListener {
				context.startActivity(Intent(context, TvShowActivity::class.java).apply {
					putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(poster))
					putExtra(Constant.ID, series.id)
					putExtra(Constant.NOME_TVSHOW, series.name)
					putExtra(
						Constant.ID_REEL,
						"${series.originalName.getNameTypeReel()}-${series.firstAirDate.yearDate()}"
					)
				})
			}

			series.voteAverage.let {
				searchVotoMedia.text = if (it != 0f) it.toString() else {
					groupStar.gone()
					""
				}
			}

			series.firstAirDate.let {
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
