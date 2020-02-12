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
import domain.search.Result
import domain.search.SearchMulti
import filme.activity.MovieDetailsActivity
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.enums.EnumTypeMedia
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 18/09/16.
 */
class SearchAdapter(val context: SearchMultiActivity, private val multis: SearchMulti) : RecyclerView.Adapter<SearchAdapter.HolderSearch>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderSearch(parent)
    override fun onBindViewHolder(holder: HolderSearch, position: Int) { holder.bind(multis.results[position]) }
    override fun getItemCount() = multis.results.size

    inner class HolderSearch(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.search_list_adapter, parent, false)) {

        private val poster: ImageView = itemView.findViewById(R.id.img_search)
        private val searchNome: TextView = itemView.findViewById(R.id.search_name)
        private val searchDataLancamento: TextView = itemView.findViewById(R.id.search_data_lancamento)
        private val searchVotoMedia: TextView = itemView.findViewById(R.id.search_voto_media)
        private val searchTitleOriginal: TextView = itemView.findViewById(R.id.search_title_original)
        private val groupStar: Group = itemView.findViewById(R.id.group_star)

        fun bind(item: Result) = with(itemView) {
            when (item.mediaType) {
                EnumTypeMedia.MOVIE.type -> setMovie(this@HolderSearch, this)
                EnumTypeMedia.TV.type-> setTvShow(this@HolderSearch, this)
                EnumTypeMedia.PERSON.type -> setPerson(this@HolderSearch, this)
            }
        }

        private fun setPerson(holderSearch: HolderSearch, view: View) {
            val person = multis.results[holderSearch.adapterPosition]
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
            val series = multis.results[holderSearch.adapterPosition]
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
            val movieDb = multis.results[holderSearch.adapterPosition]
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
