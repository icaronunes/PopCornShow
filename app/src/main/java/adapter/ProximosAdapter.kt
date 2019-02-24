package adapter

import adapter.ProximosAdapter.CalendarViewHolder
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.icaro.filme.R
import domain.EpisodesItem
import domain.UserEp
import domain.UserTvshow
import domain.tvshow.Tvshow
import org.apache.commons.lang3.tuple.MutablePair

/**
 * Created by icaro on 25/11/16.
 */
class ProximosAdapter(private val context: FragmentActivity)
    : RecyclerView.Adapter<CalendarViewHolder>() {

    private var color: Int = 0
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
            view.title.text = new.right.name
            view.proximo.text = "${new.left.seasonNumber}/${new.left.episodeNumber}"
            view.ep_title.text =  new.left.name
            view.faltando.text = "${new.left.episodeNumber}/${new.right.numberOfEpisodes}"
            view.progressBar.max = new.right.numberOfEpisodes!!
            view.progressBar.progress = new.left.episodeNumber!!
        } else {

        }
    }

    override fun getItemCount(): Int {
        return userTvshows.size

    }

    fun addSeries(series: Pair<UserEp, UserTvshow>, atual: MutablePair<EpisodesItem, Tvshow>? = null) {
        userTvshows.add(MutablePair(series, atual))
        notifyItemChanged(userTvshows.size)
    }

    fun addAtual(ultima: MutablePair<EpisodesItem, Tvshow>) {
        val t = userTvshows.find {
           it.left.first.id == ultima.left.id
        }
        val index = userTvshows.indexOf(t)
        if(index != -1) {
        userTvshows[index].right = ultima
        notifyItemChanged(index)
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
