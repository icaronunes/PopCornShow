package temporada

import Color
import Drawable
import Layout
import Txt
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import domain.EpisodesItem
import domain.UserEp
import domain.UserSeasons
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_img
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_ler_mais
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_nota
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_nota_user
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_votos
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_star
import kotlinx.android.synthetic.main.epsodio_detalhes.view.layout_diretor_nome_visto
import kotlinx.android.synthetic.main.epsodio_detalhes.view.wrapper_rating
import kotlinx.android.synthetic.main.foldin_main.view.folding_cell
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_nota
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_number
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_titulo
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_titulo_resumo
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_visto
import kotlinx.android.synthetic.main.layout_diretor.view.director_name
import kotlinx.android.synthetic.main.layout_diretor.view.grup_director
import kotlinx.android.synthetic.main.layout_diretor.view.grup_writer
import kotlinx.android.synthetic.main.layout_diretor.view.img_director
import kotlinx.android.synthetic.main.layout_diretor.view.writer_img
import kotlinx.android.synthetic.main.layout_diretor.view.writer_name
import utils.Constant
import utils.gone
import utils.makeToast
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by root on 27/02/18.
 */
class SeasonFoldinAdapter(
	val seasonActivity: SeasonActivity,
	private val seasonOnClickListener: SeasonOnClickListener
) :
	RecyclerView.Adapter<SeasonFoldinAdapter.HoldeTemporada>() {
	private var listEp: List<EpisodesItem> = listOf()
	private var seasons: UserSeasons? = null
	private var fallow: Boolean = false
	private var unfoldedIndexes = HashSet<Int>()
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HoldeTemporada(parent)
	override fun onBindViewHolder(holder: HoldeTemporada, position: Int) {
		val ep = listEp[position]
		val epUser = seasons?.userEps?.firstOrNull { ep.id == it.id }
		holder.bind(ep, epUser)
	}

	override fun getItemCount() = listEp.size
	private fun registerToggle(position: Int) =
		if (unfoldedIndexes.contains(position)) registerFold(position)
		else registerUnfold(position)

	private fun registerFold(position: Int) = unfoldedIndexes.remove(position)
	private fun registerUnfold(position: Int) = unfoldedIndexes.add(position)
	fun addSeasonFire(seasons: UserSeasons?) {
		this.seasons = seasons
		if (seasons != null && listEp.isNotEmpty()) notifyDataSetChanged()
	}

	fun addTvSeason(tvSeason: List<EpisodesItem>) {
		this.listEp = tvSeason
		notifyDataSetChanged()
	}

	fun changeFallow(fallow: Boolean) {
		if (this.fallow != fallow) {
			this.fallow = fallow
			notifyDataSetChanged()
		}
	}

	inner class HoldeTemporada(parent: ViewGroup) :
		RecyclerView.ViewHolder(
			LayoutInflater.from(seasonActivity).inflate(Layout.foldin_main, parent, false)
		) {
		fun bind(ep: EpisodesItem, epUser: UserEp?): Unit = with(itemView) {
			epsodio_star.visibility = if (fallow) View.VISIBLE else View.GONE
			item_epsodio_titulo.text = ep.name
			item_epsodio_titulo_resumo.text = ep.overview
			ep.episodeNumber.let { item_epsodio_number.text = it.toString() }
			epsodio_detalhes_votos.text = ep.voteCount.toString()
			epsodio_detalhes_img.setPicassoWithCache(
				ep.stillPath,
				5,
				img_erro = Drawable.empty_popcorn
			)
			epsodio_detalhes_nota.text = ep.overview

			ep.voteAverage?.let {
				epsodio_detalhes_nota.text = it.toString().slice(0..2)
				item_epsodio_nota.text = it.toString().slice(0..2)
			}

			if (folding_cell.isUnfolded) {
				if (unfoldedIndexes.contains(layoutPosition)) {
					folding_cell.folding_cell.unfold(true)
				} else {
					folding_cell.fold(true)
				}
				registerToggle(layoutPosition)
			}

			ep.crew?.firstOrNull { it?.job == Constant.DIRECTOR }?.let {
				grup_director.visible()
				director_name.text = it.name
				img_director.setPicassoWithCache(it.profilePath, 3, img_erro = Drawable.person)
			}

			ep.crew?.firstOrNull { it?.job == Constant.WRITER }?.let {
				grup_writer.visible()
				writer_name.text = it.name
				writer_img.setPicassoWithCache(it.profilePath, 3, img_erro = Drawable.person)
			}

			epUser?.nota?.let {
				epsodio_detalhes_nota_user.text = it.toString()
				epsodio_star.rating = it
			}

			layout_diretor_nome_visto.setOnClickListener {
				seasonOnClickListener.onClickVerTemporada(
					if (epUser != null) !epUser.isAssistido else false, ep.id
				)
			}
			epsodio_detalhes_ler_mais.setOnClickListener {
				seasonOnClickListener.onClickTemporada(layoutPosition)
			}

			wrapper_rating.setOnClickListener {
				seasonOnClickListener.onClickSeasonReated(ep, layoutPosition, epUser) {
					notifyItemChanged(layoutPosition)
				}
			}

			folding_cell.setOnClickListener {
				seasonOnClickListener.onClickScrool(layoutPosition)
				try {
					folding_cell.toggle(false)
					registerToggle(layoutPosition)
				} catch (ex: IllegalStateException) {
					seasonActivity.makeToast(Txt.ops)
				}
			}

			if (fallow) {
				when (epUser?.isAssistido == true) {
					true -> {
						item_epsodio_visto.apply {
							setBackgroundColor(ContextCompat.getColor(context, Color.green))
						}
						layout_diretor_nome_visto.apply {
							setBackgroundColor(ContextCompat.getColor(context, Color.green))
						}
					}
					false -> {
						item_epsodio_visto.apply {
							setBackgroundColor(
								ContextCompat.getColor(
									context,
									Color.gray_reviews
								)
							)
						}
						layout_diretor_nome_visto.apply {
							setBackgroundColor(
								ContextCompat.getColor(
									context,
									Color.gray_reviews
								)
							)
						}
					}
				}
			} else {
				layout_diretor_nome_visto.gone()
				epsodio_detalhes_ler_mais.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
			}
		}
	}
}
