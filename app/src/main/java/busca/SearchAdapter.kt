package busca

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.Group
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import filme.activity.MovieDetailsActivity
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.Multi
import info.movito.themoviedbapi.model.people.Person
import info.movito.themoviedbapi.model.tv.TvSeries
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.setPicassoWithCache

/**
 * Created by icaro on 18/09/16.
 */
class SearchAdapter(val context: SearchMultiActivity, private val multis: List<Multi>?) : RecyclerView.Adapter<SearchAdapter.HolderSearch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderSearch {
        val view = LayoutInflater.from(context).inflate(R.layout.search_list_adapter, parent, false)
        return HolderSearch(view)
    }

    override fun onBindViewHolder(holder: HolderSearch, position: Int) {

        when (multis?.get(position)?.mediaType) {
            Multi.MediaType.MOVIE -> {
                val movieDb = multis[position] as MovieDb

                holder.poster.setPicassoWithCache(movieDb.posterPath, 2)

                holder.itemView.setOnClickListener {
                    context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constantes.FILME_ID, movieDb.id)
                        putExtra(Constantes.NOME_FILME, movieDb.title)
                    })
                }

                movieDb.originalTitle?.let {
                    holder.searchTitleOriginal.text = it
                }

                movieDb.voteAverage.let {
                    holder.searchVotoMedia.text = if (it != 0f) it.toString() else "- -"
                    holder.groupStar.visibility = View.VISIBLE
                }

                movieDb.title.let {
                    holder.searchNome.text = it
                }

                movieDb.releaseDate?.let {
                    holder.searchDataLancamento.text =
                            if (it.length >= 4) it.substring(0, 4)
                            else it
                }
            }

            Multi.MediaType.TV_SERIES -> {
                val series = multis[position] as TvSeries

                holder.poster.setPicassoWithCache(series.posterPath, 2)

                holder.itemView.setOnClickListener {
                    context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constantes.TVSHOW_ID, series.id)
                        putExtra(Constantes.NOME_TVSHOW, series.name)
                    })
                }

                series.originalName?.let {
                    holder.searchTitleOriginal.text = it
                }

                series.voteAverage.let {
                    holder.searchVotoMedia.text = if (it != 0f) it.toString() else "- -"
                    holder.groupStar.visibility = View.VISIBLE
                }

                series.name?.let {
                    holder.searchNome.text = it
                }

                series.firstAirDate?.let {
                    holder.searchDataLancamento.text =
                            if (it.length >= 4) it.substring(0, 4)
                            else it
                }
            }

            Multi.MediaType.PERSON -> {
                val person = multis[position] as Person
                holder.poster.setPicassoWithCache(person.profilePath, 2)

                holder.itemView.setOnClickListener {
                    context.startActivity(Intent(context, PersonActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constantes.PERSON_ID, person.id)
                        putExtra(Constantes.NOME_PERSON, person.name)
                    })
                }

                person.name?.let {
                    holder.searchNome.text = it
                }

                holder.searchTitleOriginal.visibility = View.GONE
                holder.searchVotoMedia.visibility = View.GONE
                holder.groupStar.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int {
        return multis?.size ?: 0
    }

    inner class HolderSearch(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val poster: ImageView = itemView.findViewById(R.id.img_search)
        val searchNome: TextView = itemView.findViewById(R.id.search_name)
        val searchDataLancamento: TextView = itemView.findViewById(R.id.search_data_lancamento)
        val searchVotoMedia: TextView = itemView.findViewById(R.id.search_voto_media)
        val searchTitleOriginal: TextView = itemView.findViewById(R.id.search_title_original)
        val groupStar = itemView.findViewById<Group>(R.id.group_star)
    }
}
