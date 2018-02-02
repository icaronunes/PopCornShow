package listafilmes.adapter

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
import domain.ListaItemFilme
import domain.ViewType
import filme.activity.FilmeActivity
import kotlinx.android.synthetic.main.adapter_filmes_list.view.*
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constantes
import utils.UtilsApp

class ListasFilmesDelegateAdapter : ViewTypeDelegateAdapter {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ListViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
        this.context = context
        (holder as ListViewHolder).bind(item as ListaItemFilme)

    }

    inner class ListViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_filmes_list, parent, false)) {


        fun bind(item: ListaItemFilme) = with(itemView) {
            putdata(context, item, title_filmes_lista)

            Picasso.with(context).load(UtilsApp
                    .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + item.posterPath)
                    .error(R.drawable.poster_empty)
                    .into(imgFilmes, object: Callback {
                        override fun onSuccess() {
                            progress_filmes_lista.visibility = View.GONE
                        }

                        override fun onError() {
                            progress_filmes_lista.visibility = View.GONE
                            val dataLancamento = if (!item.releaseDate.isNullOrEmpty() && item.releaseDate?.length!! > 3) item.releaseDate.subSequence(0,4) else "-"
                            title_filmes_lista.text = "${item?.title} - $dataLancamento"
                            title_filmes_lista.visibility = View.VISIBLE
                        }
                    } )

            itemView.setOnClickListener({
                val intent = Intent(context, FilmeActivity::class.java)
                intent.putExtra(Constantes.FILME_ID, item.id)
                intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(imgFilmes))
                context.startActivity(intent)
            })
        }

        private fun putdata(context: Context, item: ListaItemFilme?, title_filmes_lista: TextView){
            when(context.javaClass.simpleName) {

                "FilmesActivity" -> {title_filmes_lista.visibility = View.GONE}
                else -> {title_filmes_lista.text = if (!item?.releaseDate.isNullOrEmpty() && item?.releaseDate?.length!! > 3) item.releaseDate .subSequence(0,4) else "-"}
            }
        }

    }
}