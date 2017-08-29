package oscar.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.ListaItem
import domain.ViewType

import kotlinx.android.synthetic.main.lista.view.*
import pessoaspopulares.ViewTypeDelegateAdapter
import utils.UtilsFilme

/**
 * Created by icaro on 28/08/17.
 */

class ListasDelegateAdapter : ViewTypeDelegateAdapter {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ListViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: ViewType, context: Context?) {
        (holder as ListViewHolder).bind(item as ListaItem)
    }


    inner class ListViewHolder(parent: ViewGroup) :
            RecyclerView.ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.lista, parent, false)) {


        fun bind(item: ListaItem) = with(itemView) {

            Picasso.with(context).load(UtilsFilme
                    .getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + item.posterPath)
                    .into(img_lista)
            date_oscar.text = item.releaseDate?.subSequence(0,4)
            progress.visibility = View.GONE

        }
    }
}
