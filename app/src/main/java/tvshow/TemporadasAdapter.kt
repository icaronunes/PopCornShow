package tvshow

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.GridView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import customview.stream.BaseStream
import domain.UserTvshow
import domain.reelgood.tvshow.ReelGoodTv
import domain.tvshow.SeasonsItem
import domain.tvshow.Tvshow
import elenco.WorksActivity
import temporada.SeasonActivity
import utils.Constant
import utils.enums.EnumTypeMedia
import utils.gone
import utils.invisible
import utils.parseDateShot
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 26/08/16.
 */
class TemporadasAdapter(
	val context: FragmentActivity,
	private val color: Int,
	private val changeEps: (position: Int, idSeason: Int, numberSeason: Int) -> Unit,
) : RecyclerView.Adapter<TemporadasAdapter.HoldeSeason>() {
	private var fallow: Boolean = false
	var reelGood: ReelGoodTv? = null
	var userTvshow: UserTvshow? = null
	private val baseStreamAb = BaseStream()
	private var series: Tvshow? = null
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HoldeSeason(parent)
	override fun onBindViewHolder(holder: HoldeSeason, position: Int) {
		holder.bind(series?.seasons!![position])
	}

	inner class HoldeSeason(parent: ViewGroup) :
		RecyclerView.ViewHolder(
			LayoutInflater.from(context).inflate(R.layout.season_layout, parent, false)
		) {
		internal val season: TextView = itemView.findViewById(R.id.temporada)
		internal val data: TextView = itemView.findViewById(R.id.date_temporada)
		private val imgSeason: ImageView = itemView.findViewById(R.id.image_temporada)
		private val popup: ImageButton = itemView.findViewById(R.id.popup_temporada)
		private val btFallow: ImageView = itemView.findViewById(R.id.bt_assistido)
		internal val container: GridView = itemView.findViewById(R.id.container_stream)
		fun bind(seasonsItem: SeasonsItem) = with(itemView) {
			season.text = "${context.getString(R.string.temporada)} ${seasonsItem.seasonNumber} "
			imgSeason.setPicassoWithCache(seasonsItem.posterPath, 4, {}, { imgSeason.gone() })
			data.text = seasonsItem.airDate?.parseDateShot() ?: ""
			itemView.setOnClickListener { onClickTemporada(adapterPosition, color) } //hight ordem
			popup.setOnClickListener { view ->
				showPopUp(view,
					seasonsItem.seasonNumber)
			} //hight ordem
			btFallow.setOnClickListener {
				changeEps(
					adapterPosition,
					seasonsItem.id,
					seasonsItem.seasonNumber
				)
			}
			handleIconFallow()
			handleReelGood(adapterPosition, this@HoldeSeason)
		}

		private fun handleIconFallow() {
			if (userTvshow == null || !fallow) {
				btFallow.invisible()
			} else {
				btFallow.visible()
				if (isWatch(adapterPosition)) {
					btFallow.setImageResource(drawable.icon_visto)
				} else {
					btFallow.setImageResource(drawable.icon_movie_now)
				}
			}
		}
	}

	private fun showPopUp(ancoraView: View?, seasonNumber: Int) {
		if (ancoraView != null) {
			val popupMenu = PopupMenu(context, ancoraView)
			popupMenu.inflate(R.menu.menu_popup_temporada)
			popupMenu.setOnMenuItemClickListener { item ->
				val intent = Intent(context, WorksActivity::class.java).apply {
					putExtra(Constant.NAME, series?.name)
					putExtra(Constant.ID, series!!.id)
					putExtra(Constant.MEDIATYPE, EnumTypeMedia.TV)
					putExtra(Constant.TVSEASONS, seasonNumber)
					putExtra(Constant.WORK, when (item.itemId) {
						R.id.elenco_temporada -> Constant.ViewTypesIds.CAST
						R.id.producao_temporada -> Constant.ViewTypesIds.CREWS
						else -> 0
					})
				}
				context.startActivity(intent)
				true
			}
			popupMenu.show()
		}
	}

	private fun handleReelGood(position: Int, holder: HoldeSeason) {
		if (reelGood != null) {
			val seasson = reelGood?.seasons?.find {
				it.number == series?.seasons?.get(position)?.seasonNumber
			}
			val listStreams = seasson?.availability?.sources?.map {
				var i: Int = drawable.question
				baseStreamAb.getImg(it.sourceName) { drawable ->
					i = drawable
				}
				i
			}?.toSortedSet()?.toList()
			if (listStreams != null)
				holder.container.adapter =
					AdapterStream(if (listStreams.isEmpty()) listOf() else listStreams, position)
		}
	}

	fun addStream(result: ReelGoodTv) {
		this.reelGood = result
		notifyDataSetChanged()
	}

	fun addUserTvShow(userTvshow: UserTvshow) {
		this.userTvshow = userTvshow
		notifyDataSetChanged()
	}

	fun addFallow(fallow: Boolean) {
		this.fallow = fallow
		notifyDataSetChanged()
	}

	fun addTvshow(series: Tvshow) {
		this.series = series
		notifyDataSetChanged()
	}

	override fun getItemId(position: Int): Long {
		return position.toLong()
	}

	override fun getItemViewType(position: Int): Int {
		return position
	}

	private fun isWatch(position: Int): Boolean {
		return if (userTvshow != null && userTvshow?.seasons?.getOrNull(position) != null) {
			return userTvshow?.seasons!![position].isVisto
		} else false
	}

	override fun getItemCount() = series?.seasons?.size ?: 0
	private fun onClickTemporada(position: Int, color: Int) {
		context.startActivity(Intent(context, SeasonActivity::class.java).apply {
			putExtra(
				Constant.NAME,
				"${context.getString(R.string.temporada)} ${series?.seasons?.getOrNull(position)?.seasonNumber ?: 0}"
			)
			putExtra(Constant.TEMPORADA_ID, series?.seasons?.getOrNull(position)?.seasonNumber ?: 0)
			putExtra(Constant.TEMPORADA_POSITION, position)
			putExtra(Constant.ID, series?.id)
			putExtra(Constant.COLOR_TOP, color)
		})
	}

	inner class AdapterStream(val list: List<Int>, private val seasonPosition: Int) :
		BaseAdapter() {
		override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
			return ImageView(context).apply {
				setImageResource(list[position])
				setOnClickListener { onClickTemporada(seasonPosition, color) }
			}
		}

		override fun getItem(position: Int): Any {
			return list[position]
		}

		override fun getItemId(position: Int) = position.toLong()
		override fun getCount() = list.size
	}
}
