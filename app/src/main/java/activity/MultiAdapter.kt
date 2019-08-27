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
import filme.activity.FilmeActivity
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.enums.EnumTypeMedia
import utils.setPicasso

class MultiAdapter(val application: Context, private val multiReturn: MultiSearch, private val icon: Drawable?) : RecyclerView.Adapter<MultiAdapter.HolderView>() {

    override fun onBindViewHolder(holder: HolderView, position: Int) {
        val item = multiReturn.results?.get(position)!!

        when (item.mediaType) {
            EnumTypeMedia.MOVIE.type -> {

                holder.poster.setPicasso(item.posterPath, 1)

                holder.itemView.setOnClickListener {
                    application.startActivity(Intent(application, FilmeActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constantes.FILME_ID, item.id)
                        putExtra(Constantes.NOME_FILME, item.title)
                    })
                    icon?.alpha = 255
                }

                holder.searchTitleOriginal.text = item.originalTitle
                holder.searchNome.text = item.title
                holder.searchDataLancamento.text = if (item.releaseDate != null && item.releaseDate.length >= 4) item.releaseDate.substring(0, 4) else application.getString(R.string.empty_data)
                return
            }

            EnumTypeMedia.TV.type -> {

                holder.poster.setPicasso(item.posterPath, 1)

                holder.itemView.setOnClickListener {
                    application.startActivity(Intent(application, TvShowActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constantes.TVSHOW_ID, item.id)
                        putExtra(Constantes.NOME_TVSHOW, item.name)
                    })
                    icon?.alpha = 255
                }

                if (item.originalTitle.isNullOrEmpty()) holder.searchTitleOriginal.visibility = View.GONE else holder.searchTitleOriginal.text = item.originalName

                holder.searchNome.text = item.name
                holder.searchDataLancamento.text = if (item.firstAirDate != null && item.firstAirDate.length >= 4) item.firstAirDate.substring(0, 4) else application.getString(R.string.empty_data)
                return
            }

            EnumTypeMedia.PERSON.type -> {
                holder.poster.setPicasso(item.profile_path, 1)
                holder.itemView.setOnClickListener {
                    application.startActivity(Intent(application, PersonActivity::class.java).apply {
                        putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                        putExtra(Constantes.PERSON_ID, item.id)
                        putExtra(Constantes.NOME_PERSON, item.name)
                    })
                    icon?.alpha = 255
                }
                holder.searchTitleOriginal.visibility = View.GONE
                holder.searchNome.text = item.name
                holder.searchDataLancamento.visibility = View.GONE
                return
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HolderView {
        val view = LayoutInflater.from(application).inflate(R.layout.search_list_multi_adapter, parent, false)
        return HolderView(view)
    }

    override fun getItemCount(): Int = multiReturn.results?.size!!

    inner class HolderView(itemView: View) : RecyclerView.ViewHolder(itemView) {

        var poster: ImageView = itemView.findViewById<View>(R.id.img_muitl_search) as ImageView
        var searchNome: TextView = itemView.findViewById<View>(R.id.search_muitl_nome) as TextView
        var searchDataLancamento: TextView = itemView.findViewById<View>(R.id.search_muitl_data_lancamento) as TextView
        var searchTitleOriginal: TextView = itemView.findViewById<View>(R.id.search_muitl_title_original) as TextView
    }
}
