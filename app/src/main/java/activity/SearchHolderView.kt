package activity

import ID
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import domain.ViewType
import domain.search.Result
import filme.activity.MovieDetailsActivity
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.getNameTypeReel
import utils.gone
import utils.loadPallet
import utils.setPicassoWithCache
import utils.yearDate

class SearchHolderView(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context)
    .inflate(R.layout.search_list_multi_adapter, parent, false)) {
	var poster: ImageView = itemView.findViewById(ID.img_muitl_search)
	var name: TextView = itemView.findViewById(ID.search_muitl_nome)
	var dateLaunch: TextView = itemView.findViewById(ID.search_muitl_data_lancamento)
	var titleOriginal: TextView = itemView.findViewById(ID.search_muitl_title_original)
	fun bindMovie(item: ViewType?) = with(itemView) {
		item as Result
		poster.setPicassoWithCache(item.posterPath, 1)
		setOnClickListener {
			context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                putExtra(Constant.COLOR_TOP, poster.loadPallet())
                putExtra(Constant.FILME_ID, item.id)
                putExtra(Constant.NOME_FILME, item.title)
            })
			// icon?.alpha = 255
		}

		item.originalTitle.let { titleOriginal.text = it }
		item.title.let { name.text = it }
		item.releaseDate.let {
			dateLaunch.text = if (it.length >= 4) it.substring(0, 4) else "--"
		}
	}

	fun bindTv(item: ViewType?) = with(itemView) {
		item as Result
		poster.setPicassoWithCache(item.posterPath, 1)
        if (item.originalTitle.isEmpty()) titleOriginal.gone()
        else titleOriginal.text = item.originalName
        name.text = item.name
		setOnClickListener {
			context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(poster))
                putExtra(Constant.TVSHOW_ID, item.id)
                putExtra(Constant.NOME_TVSHOW, item.name)
                putExtra(Constant.ID_REEL,
                    "${item.originalName.getNameTypeReel()}-${item.firstAirDate?.yearDate()}")
            })
			// icon?.alpha = 255
		}
		item.firstAirDate.let {
			dateLaunch.text = if (it.length >= 4) it.substring(0, 4)
			else "--"
		}
	}

	fun bindPerson(item: ViewType?) = with(itemView) {
		item as Result
		poster.setPicassoWithCache(item.profilePath, 1)
        name.text = item.name
        titleOriginal.gone()
        dateLaunch.gone()
		setOnClickListener {
			context.startActivity(Intent(context, PersonActivity::class.java).apply {
                putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(poster))
                putExtra(Constant.PERSON_ID, item.id)
                putExtra(Constant.NOME_PERSON, item.name)
            })
			// icon?.alpha = 255
		}
	}
}