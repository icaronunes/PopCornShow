package adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.ViewType
import domain.movie.ListaItemFilme
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.lista.view.date_oscar
import kotlinx.android.synthetic.main.lista.view.img_lista
import kotlinx.android.synthetic.main.lista.view.progress
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.setPicassoWithCache

/**
 * Created by icaro on 28/08/17.
 */

class ListasDelegateAdapter : ViewTypeDelegateAdapter {

	override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
		ListViewHolder(parent)

	override fun onBindViewHolder(
		holder: RecyclerView.ViewHolder,
		item: ViewType?,
		context: Context?
	) {
		(holder as ListViewHolder).bind(item as ListaItemFilme)
	}

	inner class ListViewHolder(parent: ViewGroup) :
		RecyclerView.ViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.lista, parent, false)
		) {

		fun bind(item: ListaItemFilme) = with(itemView) {

			img_lista.setPicassoWithCache(item.posterPath, 3)
			when (item.mediaType) {
				"tv" -> date_oscar.text =
					if (!item.first_air_date.isNullOrEmpty() && item.first_air_date.length > 3)
						item.first_air_date.subSequence(0, 4) else "-"
				"movie" -> date_oscar.text =
					if (!item.releaseDate.isNullOrEmpty() && item.releaseDate.length > 3)
						item.releaseDate.subSequence(0, 4) else "-"
			}

			progress.visibility = View.GONE
			itemView.setOnClickListener {
				when (item.mediaType) {
					"tv" -> {
						val intent = Intent(context, TvShowActivity::class.java).apply {
							putExtra(Constant.ID, item.id)
							putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(img_lista))
						}
						context.startActivity(intent)
					}
					"movie" -> {
						val intent = Intent(context, MovieDetailsActivity::class.java).apply {
							putExtra(Constant.ID, item.id)
							putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(img_lista))
						}
						context.startActivity(intent)
					}
				}
			}
		}
	}
}
