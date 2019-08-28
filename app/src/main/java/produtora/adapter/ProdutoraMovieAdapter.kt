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
import filme.activity.FilmeActivity
import kotlinx.android.synthetic.main.adapter_produtora.view.*
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constantes
import utils.UtilsApp
import utils.setPicassoWithCache


/**
 * Created by icaro on 10/08/16.
 */
class ProdutoraMovieAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ProdutoraViewHolde(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
        (holder as ProdutoraViewHolde).bind(item as ListaItemFilme)
    }

    inner class ProdutoraViewHolde(viewGroup: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_produtora, viewGroup, false)) {

        fun bind(item: ListaItemFilme) = with(itemView) {
            progress_bar?.visibility = View.VISIBLE
            item.releaseDate?.let { year_or_title.text = it }

            val failer = {
                progress_bar?.visibility = View.GONE
                item.title?.let {
                    year_or_title.text = it
                }
            }

            img_movie.setPicassoWithCache(item.posterPath, 2,
                    img_erro = R.drawable.poster_empty,
                    error = { failer() },
                    sucesso = { progress_bar?.visibility = View.GONE })

            itemView.setOnClickListener {
                itemView.context.startActivity(Intent(itemView.context, FilmeActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(img_movie))
                    putExtra(Constantes.FILME_ID, item.id)
                    putExtra(Constantes.NOME_FILME, item.title)
                })
            }

        }
    }
}
