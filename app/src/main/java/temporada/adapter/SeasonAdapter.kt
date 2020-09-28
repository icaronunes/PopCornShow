package temporada.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.EpisodesItem
import domain.UserEp
import domain.UserSeasons
import kotlinx.android.synthetic.main.adapter_season.view.background_season
import kotlinx.android.synthetic.main.adapter_season.view.date_season
import kotlinx.android.synthetic.main.adapter_season.view.ep_season
import kotlinx.android.synthetic.main.adapter_season.view.favorite_season
import kotlinx.android.synthetic.main.adapter_season.view.title_season
import kotlinx.android.synthetic.main.adapter_season.view.watch_season
import temporada.adapter.SeasonAdapter.*
import utils.invisible
import utils.parseDateShot
import utils.setPicassoWithCache
import utils.visible

class SeasonAdapter(
	var fallow: Boolean,
	val callIntent: (Int) -> Unit,
	val watchSeason: (Boolean, Int) -> Unit,
	val rated: (EpisodesItem, Int, UserEp?) -> Unit,
) : RecyclerView.Adapter<Holder>() {
	private var listEp: List<EpisodesItem> = listOf()
	private var seasons: UserSeasons? = null
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = Holder(parent)
	override fun onBindViewHolder(holder: Holder, position: Int) {
		val ep = listEp[position]
		holder.bind(ep, getEpUser(ep))
	}

	private fun getEpUser(ep: EpisodesItem) = seasons?.userEps?.firstOrNull { ep.id == it.id }
	override fun getItemCount() = listEp.size
	fun addSeasonFire(seasons: UserSeasons?) {
		this.seasons = seasons
		if (seasons != null && listEp.isNotEmpty()) notifyDataSetChanged()
	}

	fun addTvSeason(episodes: List<EpisodesItem>) {
		this.listEp = episodes
		notifyDataSetChanged()
	}

	fun changeFallow(it: Boolean) {
		fallow = it
		notifyDataSetChanged()
	}

	inner class Holder(parent: ViewGroup) : RecyclerView.ViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.adapter_season, parent, false)
	) {
		fun bind(episodesItem: EpisodesItem, epUser: UserEp?) = with(itemView) {
			background_season.setPicassoWithCache(episodesItem.stillPath, 5 ,img_erro =  R.drawable.empty_produtora2)

			ep_season.text = episodesItem.seasonAndEp()
			title_season.text = episodesItem.name
			date_season.text = episodesItem.airDate?.parseDateShot()

			favorite_season.apply {
				if (fallow) {
					visible()
					setOnClickListener {
						rated(episodesItem, adapterPosition, epUser)
					}
					epUser?.let { userEp ->
						setImageResource(
							if (userEp.nota != 0f)
								R.drawable.ic_star
							else
								R.drawable.ic_star_off)
					}
				} else invisible()
			}

			watch_season.apply {
				if (fallow) {
					visible()
					setOnClickListener { watchSeason(!isWatch(epUser), episodesItem.id) }
					epUser?.let { userEp ->
						setImageResource(
							if (userEp.isAssistido)
								R.drawable.ic_check
							else
								R.drawable.ic_check_off)
					}
				} else invisible()
			}

			setOnClickListener { callIntent(adapterPosition) }
			this@with
		}

		private fun isWatch(epUser: UserEp?): Boolean {
			return epUser?.isAssistido ?: return true
		}

	}
}