package seguindo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.EpisodesItem
import domain.UserEp
import domain.UserTvshow
import domain.tvshow.Tvshow
import org.apache.commons.lang3.tuple.MutablePair
import seguindo.ProximosAdapter.CalendarViewHolder
import temporada.TemporadaActivity
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

    private var colorTop: Int = 0
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
            view.proximo.text = "S${new.left?.seasonNumber}/E${new.left?.episodeNumber}"
            view.epTitle.text = new.left.name
            view.faltando.text = "$epVistos/${new.right.numberOfEpisodes}"
            view.progressBar.isIndeterminate = false

            epVistos?.let {
                view.progressBar.progress = it
            }
            view.progressBar.max = totalSemSeasonZero(new)
            view.date.text = when {
                epNubmber(new)?.let { totalSemSeasonZero(new).minus(it) } == 0 -> {
                    view.newSeguindo.visibility = View.GONE
                    new.right.lastAirDate
                }

                isEpLancado(new.left.airDate) -> {
                    view.newSeguindo.visibility = View.VISIBLE
                    new.left.airDate
                }

                new.right.lastAirDate.equals(new.left.airDate) -> {
                    view.newSeguindo.visibility = View.VISIBLE
                    new.right.lastAirDate
                }

                else -> {
                    view.newSeguindo.visibility = View.GONE
                    new.right.lastAirDate
                    //Todo contagem errada. quando falta um, monstra que falta 0
                }
            }

            view.epsFaltantes.text = if ((epVistos?.let { new.right.numberOfEpisodes?.minus(it) }) == 0) {
                view.epsFaltantes.visibility = View.GONE
                ""
            } else {
                "${(epVistos?.let { new.right.numberOfEpisodes?.minus(it) })}"
            }

            setImage(view.poster, new.right.posterPath)

        } else {
            view.title.text = old.second.nome
            view.proximo.text = ""//"S${new.left.seasonNumber}/E${new.left.episodeNumber}"
            view.epTitle.text = old.second.nome
            view.faltando.text = "${old.first.episodeNumber}/${old.second.numberOfEpisodes}"
            view.progressBar.isIndeterminate = true
            view.date.text = old.first.dataEstreia

            setImage(view.poster, old.second.poster)
        }

        view.itemView.setOnClickListener { _ ->
            context.startActivity(Intent(context, TemporadaActivity::class.java).apply {
                putExtra(Constantes.TVSHOW_ID, old.second.id)
                putExtra(Constantes.TEMPORADA_ID, old.first.seasonNumber)
                putExtra(Constantes.TEMPORADA_POSITION, temporadaZero(old?.second?.seasons?.get(0)?.seasonNumber, old.first.seasonNumber)) //old.first.episodeNumber)
                putExtra(Constantes.NOME, new?.right?.name)
                putExtra(Constantes.COLOR_TOP, colorTop)
            })
        }

        view.poster.setOnClickListener {
            context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                putExtra(Constantes.COLOR_TOP, colorTop)
                putExtra(Constantes.TVSHOW_ID, old.second.id)
                putExtra(Constantes.NOME_TVSHOW, old.second.nome)
            })
        }
    }

    private fun temporadaZero(new: Int?, episodeNumber: Int): Int {
        return if (new == 0)  episodeNumber else episodeNumber - 1
    }

    private fun isEpLancado(airDate: String?): Boolean {
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd")
            val dateEpNew = sdf.parse(airDate)
            val toDay = Calendar.getInstance()
            toDay.add(Calendar.DAY_OF_YEAR, -1)
            return dateEpNew.after(toDay.time)
        } catch (Ex: java.lang.Exception) {
            return false
        }
    }

    private fun totalSemSeasonZero(new: MutablePair<EpisodesItem, Tvshow>?): Int {
        return if (new?.right?.seasons?.get(0)?.seasonNumber == 0) {
            new.right?.numberOfEpisodes?.minus(new.right?.seasons!![0]?.episodeCount!!)!!
        } else {
            new?.right?.numberOfEpisodes!!
        }
    }

    private fun epNubmber(new: MutablePair<EpisodesItem, Tvshow>?): Int? {
        var epTotoal = 0
        new?.right?.seasons?.forEach {
            if (it?.seasonNumber != 0 && new.left.seasonNumber != it?.seasonNumber) {
                epTotoal += it?.episodeCount!!
            }
        }
        new?.left?.episodeNumber?.let {
            epTotoal + it
        }
        return epTotoal
    }

    private fun setImage(viewImage: ImageView, url: String?) {
        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2))!! + url)
                .placeholder(R.drawable.poster_empty)
                .into(viewImage, object : Callback {
                    override fun onError(e: Exception?) {
                    }

                    override fun onSuccess() {
                        colorTop = UtilsApp.loadPalette(viewImage)
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
        internal val epTitle: TextView = itemView.findViewById(R.id.ep_title) as TextView
        internal val date: TextView = itemView.findViewById(R.id.date) as TextView
        internal val epsFaltantes: TextView = itemView.findViewById(R.id.eps_faltantes) as TextView
        internal val newSeguindo: TextView = itemView.findViewById(R.id.new_seguindo) as TextView
        internal val progressBar: ProgressBar = itemView.findViewById(R.id.calendar_progress) as ProgressBar

    }
}
