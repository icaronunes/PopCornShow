package adapter

import activity.TemporadaActivity
import adapter.ProximosAdapter.CalendarViewHolder
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.EpisodesItem
import domain.UserEp
import domain.UserTvshow
import domain.tvshow.Tvshow
import org.apache.commons.lang3.tuple.MutablePair
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by icaro on 25/11/16.
 */
class ProximosAdapter(private val context: FragmentActivity)
    : RecyclerView.Adapter<CalendarViewHolder>() {

    private var color_top: Int = 0
    private val userTvshows: MutableList<
            MutablePair<
                    Pair<UserEp, UserTvshow>,
                    MutablePair<EpisodesItem, Tvshow>?
                    >> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.calendar_adapter, parent, false)
        return CalendarViewHolder(view)
    }

    override fun onBindViewHolder(view: CalendarViewHolder, position: Int) {
        val (old, new) = userTvshows[position]
        if (new != null) {
            val epVistos = epNubmber(new)
            view.title.text = new.right.name
            view.proximo.text = "S${new.left.seasonNumber}/E${new.left.episodeNumber}"
            view.ep_title.text = new.left.name
            view.faltando.text = "$epVistos/${new.right.numberOfEpisodes}"
            view.progressBar.isIndeterminate = false

            view.progressBar.progress = epVistos
            view.progressBar.max = totalSemSeasonZero(new)
            view.date.text = when {
                totalSemSeasonZero(new).minus(epNubmber(new)) == 0 -> {
                    view.new_seguindo.visibility = View.GONE
                    new.right.lastAirDate
                }

                isEplancado(new.left.airDate) -> {
                    view.new_seguindo.visibility = View.VISIBLE
                    new.right.lastAirDate
                }

                new.right.lastAirDate.equals(new.left.airDate) -> {
                    view.new_seguindo.visibility = View.VISIBLE
                    new.right.lastAirDate
                }

                else -> {
                    view.new_seguindo.visibility = View.GONE
                    new.right.lastAirDate
                    //Todo contagem errada. quando falta um, monstra que falta 0
                }
            }

            view.eps_faltantes.text = if ((new.right.numberOfEpisodes?.minus(epVistos)) == 0) {
                view.eps_faltantes.visibility = View.GONE
                ""
            } else {
                "${(new.right.numberOfEpisodes?.minus(epVistos))}"
            }

            setImage(view.poster, new.right.posterPath)

        } else {
            view.title.text = old.second.nome
            view.proximo.text = ""//"S${new.left.seasonNumber}/E${new.left.episodeNumber}"
            view.ep_title.text = old.second.nome
            view.faltando.text = "${old.first.episodeNumber}/${old.second.numberOfEpisodes}"
            view.progressBar.isIndeterminate = true
            view.date.text = old.first.dataEstreia

            setImage(view.poster, old.second.poster)
        }

        view.itemView.setOnClickListener { _ ->
            val intent = Intent(context, TemporadaActivity::class.java)
            intent.putExtra(Constantes.TVSHOW_ID, old.second.id)
            intent.putExtra(Constantes.TEMPORADA_ID, old.first.seasonNumber)
            intent.putExtra(Constantes.TEMPORADA_POSITION, temporadaZero(old?.second?.seasons?.get(0)?.seasonNumber, old.first.seasonNumber)) //old.first.episodeNumber)
            intent.putExtra(Constantes.NOME, new?.right?.name)
            intent.putExtra(Constantes.COLOR_TOP, color_top)
            context.startActivity(intent)
        }

        view.poster.setOnClickListener {
            val intent = Intent(context, TvShowActivity::class.java)
            intent.putExtra(Constantes.COLOR_TOP, color_top)
            intent.putExtra(Constantes.TVSHOW_ID, old.second.id)
            intent.putExtra(Constantes.NOME_TVSHOW, old.second.nome)
            context.startActivity(intent)
        }
    }

    private fun temporadaZero(new: Int?, episodeNumber: Int): Int {
        if(new == 0){
            return episodeNumber
        }
        return episodeNumber - 1
    }

    private fun isEplancado(airDate: String?): Boolean {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val dateEpNew = sdf.parse(airDate)
        val toDay = Calendar.getInstance()
        toDay.add(Calendar.DAY_OF_YEAR, -1)
        return dateEpNew.after(toDay.time)
    }

    private fun totalSemSeasonZero(new: MutablePair<EpisodesItem, Tvshow>?): Int {

        return if (new?.right?.seasons?.get(0)?.seasonNumber == 0) {
            new.right?.numberOfEpisodes?.minus(new.right?.seasons!![0]?.episodeCount!!)!!
        } else {
            new?.right?.numberOfEpisodes!!
        }
    }

    private fun epNubmber(new: MutablePair<EpisodesItem, Tvshow>?): Int {
        var epTotoal = 0
        new?.right?.seasons?.forEach {
            if (it?.seasonNumber != 0 && new.left.seasonNumber != it?.seasonNumber) {
                epTotoal += it?.episodeCount!!
            }
        }
        epTotoal += new?.left?.episodeNumber!!
        return epTotoal
    }

    private fun setImage(viewImage: ImageView, url: String?) {
        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2))!! + url)
                .placeholder(R.drawable.poster_empty)
                .into(viewImage, object : Callback {
                    override fun onError(e: Exception?) {
                        // holder.progressBarSimilares.visibility = View.GONE
                    }

                    override fun onSuccess() {
                        color_top = UtilsApp.loadPalette(viewImage)
                    }
                })
    }

    override fun getItemCount(): Int {
        return userTvshows.size

    }


    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun addSeries(series: Pair<UserEp, UserTvshow>, atual: MutablePair<EpisodesItem, Tvshow>? = null) {
        userTvshows.add(MutablePair(series, atual))
        notifyItemInserted(userTvshows.size)
    }

    fun addAtual(ultima: MutablePair<EpisodesItem, Tvshow>) {
        val t = userTvshows.find {
            it.left.second.id == ultima.right.id
        }

        userTvshows.forEachIndexed { index, it ->

            if (it.left.second.id == ultima.right.id) {
                userTvshows[index].right = ultima
                notifyItemChanged(index)
            }
        }
    }

    inner class CalendarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val poster: ImageView = itemView.findViewById(R.id.poster) as ImageView
        internal val proximo: TextView = itemView.findViewById(R.id.proximo_ver) as TextView
        internal val title: TextView = itemView.findViewById(R.id.title) as TextView
        internal val faltando: TextView = itemView.findViewById(R.id.faltante) as TextView
        internal val ep_title: TextView = itemView.findViewById(R.id.ep_title) as TextView
        internal val date: TextView = itemView.findViewById(R.id.date) as TextView
        internal val eps_faltantes: TextView = itemView.findViewById(R.id.eps_faltantes) as TextView
        internal val new_seguindo: TextView = itemView.findViewById(R.id.new_seguindo) as TextView
        internal val progressBar: ProgressBar = itemView.findViewById(R.id.calendar_progress) as ProgressBar

    }
}
