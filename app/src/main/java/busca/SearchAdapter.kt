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
import br.com.icaro.filme.R.drawable
import filme.activity.MovieDetailsActivity
import info.movito.themoviedbapi.model.MovieDb
import info.movito.themoviedbapi.model.Multi
import info.movito.themoviedbapi.model.people.Person
import info.movito.themoviedbapi.model.tv.TvSeries
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 18/09/16.
 */
class SearchAdapter(val context: SearchMultiActivity, private val multis: List<Multi>) : RecyclerView.Adapter<SearchAdapter.HolderSearch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderSearch(parent)
    override fun onBindViewHolder(holder: HolderSearch, position: Int) { holder.bind(multis[position]) }
    override fun getItemCount() = multis.size

    inner class HolderSearch(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_list_adapter, parent, false)) {

        private val poster: ImageView = itemView.findViewById(R.id.img_search)
        private val searchNome: TextView = itemView.findViewById(R.id.search_name)
        private val searchDataLancamento: TextView = itemView.findViewById(R.id.search_data_lancamento)
        private val searchVotoMedia: TextView = itemView.findViewById(R.id.search_voto_media)
        private val searchTitleOriginal: TextView = itemView.findViewById(R.id.search_title_original)
        private val groupStar: Group = itemView.findViewById(R.id.group_star)

        fun bind(item: Multi) = with(itemView) {
            when (item.mediaType!!) {
                Multi.MediaType.MOVIE -> setMovie(this@HolderSearch, this)
                Multi.MediaType.TV_SERIES -> setTvShow(this@HolderSearch, this)
                Multi.MediaType.PERSON -> setPerson(this@HolderSearch, this)
            }
        }

        private fun setPerson(holderSearch: HolderSearch, view: View) {
            val person = multis[holderSearch.adapterPosition] as Person
            poster.setPicassoWithCache(person.profilePath, 4, img_erro = drawable.person)
            person.name?.let { searchNome.text = it }

            itemView.setOnClickListener {
                context.startActivity(Intent(view.context, PersonActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                    putExtra(Constantes.PERSON_ID, person.id)
                    putExtra(Constantes.NOME_PERSON, person.name)
                })
            }
            listOf(searchTitleOriginal,searchVotoMedia, groupStar ).forEach { it.gone() }
        }

        private fun setTvShow(holderSearch: HolderSearch, view: View) {
            val series = multis[holderSearch.adapterPosition] as TvSeries
            poster.setPicassoWithCache(series.posterPath, 4, img_erro = drawable.poster_empty)
            series.originalName?.let { searchTitleOriginal.text = it }
            series.name?.let { searchNome.text = it }

            view.setOnClickListener {
                context.startActivity(Intent(view.context, TvShowActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                    putExtra(Constantes.TVSHOW_ID, series.id)
                    putExtra(Constantes.NOME_TVSHOW, series.name)
                })
            }

            series.voteAverage.let {
                searchVotoMedia.text = if (it != 0f) it.toString() else "- -"
                groupStar.visible()
            }

            series.firstAirDate?.let {
                searchDataLancamento.text =
                    if (it.length >= 4) it.substring(0, 4)
                    else it
            }
        }

        private fun setMovie(holderSearch: HolderSearch, itemView: View) {
            val movieDb = multis[holderSearch.adapterPosition] as MovieDb
            poster.setPicassoWithCache(movieDb.posterPath, 4, img_erro = drawable.poster_empty)
            movieDb.title.let { searchNome.text = it }
            movieDb.originalTitle?.let { searchTitleOriginal.text = it }

            itemView.setOnClickListener {
                context.startActivity(Intent(itemView.context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                    putExtra(Constantes.FILME_ID, movieDb.id)
                    putExtra(Constantes.NOME_FILME, movieDb.title)
                })
            }

            movieDb.voteAverage.let {
                searchVotoMedia.text = if (it != 0f) it.toString() else "- -"
                groupStar.visible()
            }

            movieDb.releaseDate?.let {
                searchDataLancamento.text =
                    if (it.length >= 4) it.substring(0, 4) else it
            }
        }
    }
}
