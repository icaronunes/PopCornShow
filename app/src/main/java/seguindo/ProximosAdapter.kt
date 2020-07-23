package seguindo

import Color
import Drawable
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.EpisodesItem
import domain.UserTvshow
import domain.ViewType
import domain.tvshow.Fallow
import domain.tvshow.Tvshow
import domain.tvshow.sumNotWatch
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import temporada.SeasonActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.createIdReal
import utils.kotterknife.findView
import utils.loadPallet
import utils.parseDateShot
import utils.periodLaunch
import utils.setPicassoWithCache

/**
 * Created by icaro on 25/11/16.
 */
class ProximosAdapter : ViewTypeDelegateAdapter {
	//TODO validar com Diff... para não chamar novamente as apis
	private var list: MutableList<Triple<Tvshow, UserTvshow, EpisodesItem>> = mutableListOf()
	private var colorTop: Int = 0

	override fun onCreateViewHolder(parent: ViewGroup) = CalendarViewHolder(parent)
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as CalendarViewHolder).bind(item as Fallow)
	}

	inner class CalendarViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.calendar_adapter, parent, false)
	) {
		fun bind(pair: Fallow) = with(itemView) {
			val (tvshow, userTvshow, epItem) = pair.updated

			val stop = tvshow.sumNotWatch(
				seasonNumber = epItem.seasonNumber,
				epNumber = epItem.episodeNumber
			)

			title.text = userTvshow.nome
			next.text = "S${epItem?.seasonNumber}/E${epItem.episodeNumber}"
			epTitle.text = epItem.name
			faltando.text = "$stop/${tvshow.numberOfEpisodes}"
			date.text = epItem.airDate?.parseDateShot()
			poster.setPicassoWithCache(
				tvshow.posterPath, 3,
				{ colorTop = poster.loadPallet() ?: Color.primary_dark },
				img_erro = Drawable.poster_empty
			)
			progressBar.apply {
				progress = stop
				max = tvshow.numberOfEpisodes
				isIndeterminate = false
			}
			new.visibility = if (epItem.airDate?.periodLaunch() == true) View.VISIBLE else View.GONE

			setOnClickListener {
				fun positionOnFireSeason() = userTvshow.seasons.indexOfFirst {
					it.seasonNumber == epItem.seasonNumber
				}

				context.startActivity(Intent(context, SeasonActivity::class.java).apply {
					putExtra(Constant.TVSHOW_ID, userTvshow.id)
					putExtra(Constant.TEMPORADA_ID, epItem.seasonNumber)
					putExtra(Constant.TEMPORADA_POSITION, positionOnFireSeason())
					putExtra(Constant.NAME, tvshow.name)
					putExtra(Constant.COLOR_TOP, colorTop)
				})
			}

			poster.setOnClickListener {
				context.startActivity(Intent(context, TvShowActivity::class.java).apply {
					putExtra(Constant.COLOR_TOP, colorTop)
					putExtra(Constant.TVSHOW_ID, userTvshow.id)
					putExtra(Constant.NOME_TVSHOW, userTvshow.nome)
					putExtra(Constant.ID_REEL, tvshow.createIdReal())
				})
			}
		}

		private val poster: ImageView by findView(R.id.poster)
		private val next: TextView by findView(R.id.proximo_ver)
		private val title: TextView by findView(R.id.title)
		private val faltando: TextView by findView(R.id.faltante)
		private val epTitle: TextView by findView(R.id.ep_title)
		private val date: TextView by findView(R.id.date)
		private val new: TextView by findView(R.id.new_seguindo)
		private val progressBar: ProgressBar by findView(R.id.calendar_progress)
	}

}
