package oscar.adapter

import activity.FilmeActivity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.ListaItemFilme
import domain.ViewType
import kotlinx.android.synthetic.main.lista.view.*
import pessoaspopulares.ViewTypeDelegateAdapter
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 28/08/17.
 */

class ListasDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder =
            ListViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
        (holder as ListViewHolder).bind(item as ListaItemFilme)
    }

    inner class ListViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lista, parent, false)) {


        fun bind(item: ListaItemFilme) = with(itemView) {

            Picasso.with(context).load(UtilsApp
                    .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + item.posterPath)
                    .into(img_lista)
            date_oscar.text = item.releaseDate?.subSequence(0,4)
            progress.visibility = View.GONE
            itemView.setOnClickListener({
                val intent = Intent(context, FilmeActivity::class.java)
                intent.putExtra(Constantes.FILME_ID, item.id)
                intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(img_lista))
                context.startActivity(intent)
            })
        }
    }
}
