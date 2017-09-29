package adapter

import activity.SimilaresActivity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.ResultsSimilarItem
import filme.activity.FilmeActivity
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 12/08/16.
 */

class SimilaresAdapter(similaresActivity: SimilaresActivity, lista: List<ResultsSimilarItem?>?) :
        RecyclerView.Adapter<SimilaresAdapter.SimilareViewHolde>() {

    private var context: Context? = null
    private var similares: List<ResultsSimilarItem?>? = null

    init {
        this.context = similaresActivity
        this.similares = lista
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilareViewHolde {
        val view = LayoutInflater.from(context).inflate(R.layout.adapter_similares, parent, false)

        return SimilareViewHolde(view)

    }

    override fun onBindViewHolder(holder: SimilareViewHolde, position: Int) {
        holder.similares_nome.text = similares?.get(position)?.title
        holder.similares_data_lancamento.text = similares?.get(position)?.releaseDate
        holder.similares_title_original.text = similares?.get(position)?.originalTitle
        holder.similares_voto_media.text = similares?.get(position)?.voteAverage.toString()

        Picasso.with(context)
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2))!! + similares?.get(position)?.posterPath)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, FilmeActivity::class.java)
            val color = UtilsApp.loadPalette(holder.imageView)
            intent.putExtra(Constantes.COLOR_TOP, color)
            intent.putExtra(Constantes.FILME_ID, similares?.get(position)?.id)
            intent.putExtra(Constantes.NOME_FILME, similares?.get(position)?.title)
            context?.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return similares?.size ?: 0
    }

    inner class SimilareViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById<View>(R.id.img_similares) as ImageView
        val similares_nome: TextView = itemView.findViewById<View>(R.id.similares_nome) as TextView
        val similares_data_lancamento: TextView = itemView.findViewById<View>(R.id.similares_data_lancamento) as TextView
        val similares_voto_media: TextView = itemView.findViewById<View>(R.id.similares_voto_media) as TextView
        val similares_title_original: TextView = itemView.findViewById<View>(R.id.similares_title_original) as TextView


    }
}
