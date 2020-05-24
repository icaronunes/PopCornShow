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
import customview.stream.BaseStreamAb
import domain.UserTvshow
import domain.tvshow.Tvshow
import elenco.ElencoActivity
import producao.CrewsActivity
import temporada.TemporadaActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.gone
import utils.parseDateShot
import utils.setPicassoWithCache

/**
 * Created by icaro on 26/08/16.
 */

class TemporadasAdapter(
    val context: FragmentActivity,
    private val series: Tvshow,
    private val onClickListener: TemporadasOnClickListener,
    private val color: Int,
    private val userTvshow: UserTvshow?,
    private val baseStreamAb: BaseStreamAb
) : RecyclerView.Adapter<TemporadasAdapter.HoldeSeason>() {

    interface TemporadasOnClickListener {
        fun onClickCheckTemporada(view: View, position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldeSeason {
        val view = LayoutInflater.from(context).inflate(R.layout.season_layout, parent, false)
        return HoldeSeason(view)
    }

    private fun showPopUp(ancoraView: View?, seasonNumber: Int) {
        if (ancoraView != null) {
            val popupMenu = PopupMenu(context, ancoraView)
            popupMenu.inflate(R.menu.menu_popup_temporada)

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.elenco_temporada -> {
                        val intent = Intent(context, ElencoActivity::class.java)
                        intent.putExtra(Constant.ID, series.id)
                        intent.putExtra(Constant.TVSEASONS, seasonNumber)
                        intent.putExtra(Constant.NAME, series.name)
                        context.startActivity(intent)
                    }

                    R.id.producao_temporada -> {
                        val intent = Intent(context, CrewsActivity::class.java)
                        intent.putExtra(Constant.ID, series.id)
                        intent.putExtra(Constant.TVSEASONS, seasonNumber)
                        intent.putExtra(Constant.NAME, series.name)
                        context.startActivity(intent)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }

    override fun onBindViewHolder(holder: HoldeSeason, position: Int) {

        holder.season.text = "${context.getString(R.string.temporada)} ${series.seasons.getOrNull(position)?.seasonNumber!! ?: ""} "
        holder.imgSeason.setPicassoWithCache(series.seasons[position].posterPath, 4, {}, { holder.imgSeason.gone() })
        holder.data.text = series.seasons[position].airDate?.parseDateShot() ?: ""
        holder.itemView.setOnClickListener { onClickTemporada(position, color) }
        holder.popup.setOnClickListener { view -> showPopUp(view, series.seasons[position].seasonNumber) }
        holder.btFallow.setOnClickListener { view -> onClickListener.onClickCheckTemporada(view, position) }

        if (userTvshow == null) {
            holder.btFallow.visibility = View.GONE
        } else {
            if (isWatch(position)) {
                holder.btFallow.setImageResource(R.drawable.icon_visto)
            } else {
                holder.btFallow.setImageResource(R.drawable.icon_movie_now)
            }
        }

        if ((context as TvShowActivity).reelGood != null) {
            val seasson = (context as TvShowActivity).reelGood?.seasons?.find {
                it.number == series.seasons[position].seasonNumber
            }

            val listStreams = seasson?.availability?.sources?.map {
                var i: Int = R.drawable.question
                baseStreamAb.getImg(it.sourceName) { drawable ->
                    i = drawable
                }
                i
            }?.toSortedSet()?.toList()
            if (listStreams != null)
                holder.container.adapter = AdapterStream(if (listStreams.isEmpty()) listOf() else listStreams, position)
        }
    }

    private fun isWatch(position: Int): Boolean {
        return if (userTvshow != null && userTvshow.seasons.getOrNull(position) != null) {
            return userTvshow.seasons[position].isVisto
        } else false
    }

    override fun getItemCount() = series.seasons.size

    inner class HoldeSeason(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val season: TextView = itemView.findViewById(R.id.temporada)
        internal val data: TextView = itemView.findViewById(R.id.date_temporada)
        internal val imgSeason: ImageView = itemView.findViewById(R.id.image_temporada)
        internal val popup: ImageButton = itemView.findViewById(R.id.popup_temporada)
        internal val btFallow: ImageView = itemView.findViewById(R.id.bt_assistido)
        internal val container: GridView = itemView.findViewById(R.id.container_stream)
    }

    inner class AdapterStream(val list: List<Int>, val seasonPosition: Int) : BaseAdapter() {
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

    private fun onClickTemporada(position: Int, color: Int) {
        context.startActivity(Intent(context, TemporadaActivity::class.java).apply {
            putExtra(Constant.NAME, "${context.getString(R.string.temporada)} ${series.seasons.getOrNull(position)?.seasonNumber ?: 0}")
            putExtra(Constant.TEMPORADA_ID, series.seasons.getOrNull(position)?.seasonNumber ?: 0)
            putExtra(Constant.TEMPORADA_POSITION, position)
            putExtra(Constant.TVSHOW_ID, series.id)
            putExtra(Constant.COLOR_TOP, color)
        })
    }
}
