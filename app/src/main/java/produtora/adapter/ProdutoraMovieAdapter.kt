package produtora.adapter

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
import kotlinx.android.synthetic.main.adapter_produtora.view.img_movie
import kotlinx.android.synthetic.main.adapter_produtora.view.progress_bar
import kotlinx.android.synthetic.main.adapter_produtora.view.year_or_title
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant
import utils.UtilsApp
import utils.parseDateShot
import utils.setPicassoWithCache

/**
 * Created by icaro on 10/08/16.
 */
class ProdutoraMovieAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ProdutoraViewHolde(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType?, context: Context?) {
        (holder as ProdutoraViewHolde).bind(item as ListaItemFilme)
    }

    inner class ProdutoraViewHolde(viewGroup: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_produtora, viewGroup, false)) {

        fun bind(item: ListaItemFilme) = with(itemView) {
            progress_bar?.visibility = View.VISIBLE
            item.releaseDate?.let { year_or_title.text = it.parseDateShot() }

            val failure = {
                progress_bar?.visibility = View.GONE
                item.title?.let {
                    year_or_title.text = it
                }
            }

            img_movie.setPicassoWithCache(item.posterPath, 3,
                    img_erro = R.drawable.poster_empty,
                    error = { failure() },
                    sucesso = { progress_bar?.visibility = View.GONE })

            setOnClickListener {
                it.context.startActivity(Intent(itemView.context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(img_movie))
                    putExtra(Constant.ID, item.id)
                    putExtra(Constant.NOME_FILME, item.title)
                })
            }
        }
    }
}
