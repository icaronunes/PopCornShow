package main

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.movie.ListaFilmes
import domain.movie.ListaItemFilme
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.poster_main.view.img_poster_grid
import kotlinx.android.synthetic.main.poster_main.view.layout_poster_main
import kotlinx.android.synthetic.main.poster_main.view.progress_poster_grid
import kotlinx.android.synthetic.main.poster_main.view.title_main
import utils.Constant
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 17/02/17.
 */

class MovieMainAdapter(context: FragmentActivity, private val movieDbs: ListaFilmes) : RecyclerView.Adapter<MovieMainAdapter.MovieViewHolder>() {
    private val context: Context
    private var color: Int = 0

    init {
        this.context = context
        this.color = ActivityCompat.getColor(context, R.color.primary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = MovieViewHolder(parent)

    override fun onBindViewHolder(holder: MovieViewHolder, position: Int) {
        val movieDb = movieDbs.results[position]
        holder.bind(movieDb)
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun setHasStableIds(hasStableIds: Boolean) {
        super.setHasStableIds(true)
    }

    override fun getItemCount() = if (movieDbs.results.size < 15) movieDbs.results.size else 15

    inner class MovieViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.poster_main, parent, false)) {

        fun bind(item: ListaItemFilme) = with(itemView) {
            img_poster_grid.setBackgroundColor(ActivityCompat.getColor(context, R.color.primary_dark))
            img_poster_grid
                .setPicassoWithCache(item.posterPath, 3, {
                progress_poster_grid.gone()
                color = UtilsApp.loadPalette(img_poster_grid)
                layout_poster_main.setCardBackgroundColor(color)
            }, {
                progress_poster_grid.gone()
                title_main.apply {
                    text = item.title
                    title_main.visible()
                }
            }, img_erro = R.drawable.poster_empty)

            img_poster_grid.setOnClickListener {
                context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constant.NOME_FILME, item.title)
                    putExtra(Constant.ID, item.id)
                    putExtra(Constant.COLOR_TOP, color)
                })
            }
        }
    }
}
