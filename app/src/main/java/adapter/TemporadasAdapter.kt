package adapter

import activity.CrewsActivity
import activity.ElencoActivity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.PopupMenu
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.UserTvshow
import domain.tvshow.Tvshow
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 26/08/16.
 */


class TemporadasAdapter(val context: FragmentActivity, private val series: Tvshow?,
     private val onClickListener: TemporadasOnClickListener, private val color: Int, private val userTvshow: UserTvshow?)
    : RecyclerView.Adapter<TemporadasAdapter.HoldeTemporada>() {

    interface TemporadasOnClickListener {
        fun onClickTemporada(view: View, position: Int, color: Int)
        fun onClickCheckTemporada(view: View, position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemporadasAdapter.HoldeTemporada {
        val view = LayoutInflater.from(context).inflate(R.layout.season_layout, parent, false)

        return HoldeTemporada(view)
    }

    private fun showPopUp(ancoraView: View?, seasonNumber: Int) {
        if (ancoraView != null) {
            val popupMenu = PopupMenu(context, ancoraView)
            popupMenu.inflate(R.menu.menu_popup_temporada)

            popupMenu.setOnMenuItemClickListener { item ->

                when (item.itemId) {
                    R.id.elenco_temporada -> {
                        val intent = Intent(context, ElencoActivity::class.java)
                        intent.putExtra(Constantes.ID, series?.id)
                        intent.putExtra(Constantes.TVSEASONS, seasonNumber)
                        intent.putExtra(Constantes.NOME, series?.name)
                        context.startActivity(intent)
                    }

                    R.id.producao_temporada -> {
                        val intent = Intent(context, CrewsActivity::class.java)
                        intent.putExtra(Constantes.ID, series?.id)
                        intent.putExtra(Constantes.TVSEASONS, seasonNumber)
                        intent.putExtra(Constantes.NOME, series?.name)
                        context.startActivity(intent)
                    }
                }
                true
            }
            popupMenu.show()
        }
    }


    override fun onBindViewHolder(holder: TemporadasAdapter.HoldeTemporada, position: Int) {

        holder.temporada.text = "${context.getString(R.string.temporada)} ${series?.seasons!![position]?.seasonNumber!!}"
        Picasso.get().load(UtilsApp.getBaseUrlImagem(2) + series.seasons[position]?.posterPath)
                .error(R.drawable.poster_empty)
                .into(holder.image_temporada)
        holder.data.text = series.seasons[position]?.airDate ?: ""
        holder.itemView.setOnClickListener { view -> onClickListener.onClickTemporada(view, position, color) }

        holder.popup.setOnClickListener { view -> showPopUp(view, series.seasons[position]?.seasonNumber!!) }

        holder.bt_seguindo.setOnClickListener { view -> onClickListener.onClickCheckTemporada(view, position) }

        if (userTvshow == null) {
            holder.bt_seguindo.visibility = View.GONE
        } else {
            if (isVisto(position)) {
                holder.bt_seguindo.setImageResource(R.drawable.icon_visto)
            } else {
                holder.bt_seguindo.setImageResource(R.drawable.icon_movie_now)
            }
        }

    }

    private fun isVisto(position: Int): Boolean {
        if (userTvshow != null) {
            if (userTvshow.seasons.size > position) {
               return userTvshow.seasons[position].isVisto
            }
        }
        return false
    }

    override fun getItemCount(): Int {

        return if (series?.numberOfSeasons!! > 0) {
            series.seasons?.size!!
        } else 0
    }

    inner class HoldeTemporada(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val temporada: TextView = itemView.findViewById(R.id.temporada)
        internal val data: TextView = itemView.findViewById(R.id.date_temporada)
        internal val image_temporada: ImageView = itemView.findViewById(R.id.image_temporada)
        internal val popup: ImageButton = itemView.findViewById(R.id.popup_temporada)
        internal val bt_seguindo: ImageView = itemView.findViewById(R.id.bt_assistido)

    }
}
