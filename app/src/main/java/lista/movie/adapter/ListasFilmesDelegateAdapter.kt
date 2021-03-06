package lista.movie.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import domain.ViewType
import domain.movie.ListaItemFilme
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.adapter_filmes_list.view.imgFilmes
import kotlinx.android.synthetic.main.adapter_filmes_list.view.progress_filmes_lista
import kotlinx.android.synthetic.main.adapter_filmes_list.view.title_filmes_lista
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant
import utils.UtilsApp

class ListasFilmesDelegateAdapter : ViewTypeDelegateAdapter {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ListViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType?, context: Context?) {
        this.context = context
        (holder as ListViewHolder).bind(item as ListaItemFilme)
    }

    inner class ListViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_filmes_list, parent, false)) {

        fun bind(item: ListaItemFilme) = with(itemView) {
            putdata(context, item, title_filmes_lista)

            Picasso.get().load(UtilsApp
                .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 4)) + item.posterPath)
                    .error(R.drawable.poster_empty)
                    .into(imgFilmes, object : Callback {
                        override fun onError(e: Exception?) {
                            progress_filmes_lista.visibility = View.GONE
                            val dataLancamento = if (!item.releaseDate.isNullOrEmpty() && item.releaseDate.length > 3) item.releaseDate.subSequence(0, 4) else "-"
                            title_filmes_lista.text = "${item.title} - $dataLancamento"
                            title_filmes_lista.visibility = View.VISIBLE
                        }

                        override fun onSuccess() {
                            progress_filmes_lista.visibility = View.GONE
                            title_filmes_lista.visibility = View.GONE
                        }
                    })

            itemView.setOnClickListener {
                val intent = Intent(context, MovieDetailsActivity::class.java)
                intent.putExtra(Constant.ID, item.id)
                intent.putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(imgFilmes))
                context.startActivity(intent)
            }
        }

        private fun putdata(context: Context, item: ListaItemFilme?, title_filmes_lista: TextView) {
            when (context.javaClass.simpleName) {
                "FilmesActivity" -> { title_filmes_lista.visibility = View.GONE }
                else -> { title_filmes_lista.text = if (!item?.releaseDate.isNullOrEmpty() && item?.releaseDate?.length!! > 3) item.releaseDate.subSequence(0, 4) else "-" }
            }
        }
    }
}
