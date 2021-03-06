package lista.tvshow.adapter

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
import domain.ListaItemSerie
import domain.ViewType
import kotlinx.android.synthetic.main.adapter_filmes_list.view.imgFilmes
import kotlinx.android.synthetic.main.adapter_filmes_list.view.progress_filmes_lista
import kotlinx.android.synthetic.main.adapter_filmes_list.view.title_filmes_lista
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.getNameTypeReel
import utils.yearDate

class ListasSeriesDelegateAdapter : ViewTypeDelegateAdapter {

    var context: Context? = null

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ListViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType?, context: Context?) {
        this.context = context
        (holder as ListViewHolder).bind(item as ListaItemSerie)
    }

    inner class ListViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_filmes_list, parent, false)) {

        fun bind(item: ListaItemSerie?) = with(itemView) {
            putdata(context, item, title_filmes_lista)

            Picasso.get().load(UtilsApp
                .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 4)) + item?.posterPath)
                    .error(R.drawable.poster_empty)
                    .into(imgFilmes, object : Callback {
                        override fun onError(e: Exception?) {
                            progress_filmes_lista?.visibility = View.GONE
                            if (!item?.firstAirDate.isNullOrEmpty() && item?.firstAirDate?.length!! > 3) {
                                title_filmes_lista.text = "${item.name} - ${item.firstAirDate.subSequence(0, 4)}"
                            }
                            title_filmes_lista.visibility = View.VISIBLE
                        }

                        override fun onSuccess() {
                            progress_filmes_lista?.visibility = View.GONE
                        }
                    })

            setOnClickListener {
                val intent = Intent(context, TvShowActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(imgFilmes))
                    putExtra(Constant.ID, item?.id)
                    putExtra(Constant.NOME_TVSHOW, item?.name)
                    putExtra(Constant.ID_REEL, "${item?.originalName?.getNameTypeReel()}-${item?.firstAirDate?.yearDate()}")
                }
                context.startActivity(intent)
            }
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
