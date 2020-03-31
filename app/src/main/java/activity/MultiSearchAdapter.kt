package activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.busca.MultiSearch
import filme.activity.MovieDetailsActivity
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.enums.EnumTypeMedia
import utils.setPicassoWithCache

class MultiSearchAdapter(val application: Context, private val multiReturn: MultiSearch, private val icon: Drawable?) :
    RecyclerView.Adapter<MultiSearchAdapter.HolderView>() {

    override fun onBindViewHolder(holder: HolderView, position: Int) {
        val item = multiReturn.results?.get(position)!!

        when (item.mediaType) {
            EnumTypeMedia.MOVIE.type -> {

                holder.poster.setPicassoWithCache(item.posterPath, 1)
                holder.itemView.setOnClickListener {
                    application.startActivity(Intent(application, MovieDetailsActivity::class.java).apply {
                        putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constant.FILME_ID, item.id)
                        putExtra(Constant.NOME_FILME, item.title)
                    })
                    icon?.alpha = 255
                }

                item.originalTitle?.let {
                    holder.searchTitleOriginal.text = it
                }
                item.title?.let {
                    holder.searchNome.text = it
                }

                item.releaseDate?.let {
                    holder.searchDataLancamento.text = if (it.length >= 4) it.substring(0, 4) else "--"
                }
            }

            EnumTypeMedia.TV.type -> {
                holder.poster.setPicassoWithCache(item.posterPath, 1)

                holder.itemView.setOnClickListener {
                    application.startActivity(Intent(application, TvShowActivity::class.java).apply {
                        putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constant.TVSHOW_ID, item.id)
                        putExtra(Constant.NOME_TVSHOW, item.name)
                    })
                    icon?.alpha = 255
                }

                if (item.originalTitle.isNullOrEmpty()) holder.searchTitleOriginal.visibility = View.GONE
                else holder.searchTitleOriginal.text = item.originalName

                item.name?.let {
                    holder.searchNome.text = it
                }

                item.firstAirDate?.let {
                    holder.searchDataLancamento.text = if (it.length >= 4) it.substring(0, 4)
                    else "--"
                }
            }

            EnumTypeMedia.PERSON.type -> {
                holder.poster.setPicassoWithCache(item.profile_path, 1)
                holder.itemView.setOnClickListener {
                    application.startActivity(Intent(application, PersonActivity::class.java).apply {
                        putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constant.PERSON_ID, item.id)
                        putExtra(Constant.NOME_PERSON, item.name)
                    })
                    icon?.alpha = 255
                }

                item.name.let {
                    holder.searchNome.text = it
                }

                holder.searchTitleOriginal.visibility = View.GONE
                holder.searchDataLancamento.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderView {
        val view = LayoutInflater.from(application).inflate(R.layout.search_list_multi_adapter, parent, false)
        return HolderView(view)
    }

    override fun getItemCount(): Int = multiReturn.results?.size!!

    inner class HolderView(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var poster: ImageView = itemView.findViewById(R.id.img_muitl_search)
        var searchNome: TextView = itemView.findViewById(R.id.search_muitl_nome)
        var searchDataLancamento: TextView = itemView.findViewById(R.id.search_muitl_data_lancamento)
        var searchTitleOriginal: TextView = itemView.findViewById(R.id.search_muitl_title_original)
    }
}
