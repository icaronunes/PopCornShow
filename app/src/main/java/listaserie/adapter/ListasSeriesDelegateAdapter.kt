package listaserie.adapter

import activity.TvShowActivity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.ListaItemSerie
import domain.ViewType
import kotlinx.android.synthetic.main.adapter_filmes_list.view.*
import pessoaspopulares.ViewTypeDelegateAdapter
import utils.Constantes
import utils.UtilsApp

class ListasSeriesDelegateAdapter : ViewTypeDelegateAdapter {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ListViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
        this.context = context
        (holder as ListViewHolder).bind(item as ListaItemSerie)

    }

    inner class ListViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_filmes_list, parent, false)) {


        fun bind(item: ListaItemSerie?) = with(itemView) {
            putdata(context, item, title_filmes_lista)

            Picasso.with(context).load(UtilsApp
                    .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + item?.posterPath)
                    .error(R.drawable.poster_empty)
                    .into(imgFilmes, object : Callback {
                        override fun onSuccess() {
                            progress_filmes_lista.visibility = View.GONE
                        }

                        override fun onError() {
                            progress_filmes_lista.visibility = View.GONE
                            title_filmes_lista.text = "${item?.name} - ${item?.firstAirDate?.subSequence(0, 4)}"
                            title_filmes_lista.visibility = View.VISIBLE
                        }
                    })

            itemView.setOnClickListener({
                val intent = Intent(context, TvShowActivity::class.java)
                intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(imgFilmes))
                intent.putExtra(Constantes.TVSHOW_ID, item?.id)
                intent.putExtra(Constantes.NOME_TVSHOW, item?.name)
                context.startActivity(intent)
            })
        }

        private fun putdata(context: Context, item: ListaItemSerie?, title_filmes_lista: TextView) {
            when (context.javaClass.simpleName) {

                "TvShowsActivity" -> {
                    title_filmes_lista.visibility = View.GONE
                }
                else -> {
                    title_filmes_lista.text = item?.firstAirDate?.subSequence(0, 4)
                }
            }
        }

    }
}