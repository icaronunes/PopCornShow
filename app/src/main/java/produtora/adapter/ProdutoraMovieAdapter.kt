package produtora.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.ListaItemFilme
import domain.ViewType
import filme.activity.FilmeActivity
import kotlinx.android.synthetic.main.adapter_produtora.view.*
import pessoaspopulares.ViewTypeDelegateAdapter
import utils.Constantes
import utils.UtilsApp


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

    inner class ProdutoraViewHolde (viewGroup: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(viewGroup.context).inflate(R.layout.adapter_produtora, viewGroup, false)) {

        fun bind(item: ListaItemFilme) = with(itemView){
            progress_bar?.visibility = View.VISIBLE
            titleTextView_produtora.text = item.title
            Picasso.with(context)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + item.posterPath)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(imgFilme_produtora, object : Callback {
                        override fun onSuccess() {
                            progress_bar?.visibility = View.GONE

                        }

                        override fun onError() {
                            progress_bar?.visibility = View.GONE
                        }
                    })

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, FilmeActivity::class.java)
                val color = UtilsApp.loadPalette(imgFilme_produtora)
                intent.putExtra(Constantes.COLOR_TOP, color)
                intent.putExtra(Constantes.FILME_ID, item.id)
                intent.putExtra(Constantes.NOME_FILME, item.title)
                itemView.context.startActivity(intent)
            }

        }
    }
}
