package filme.adapter

import activity.SimilaresActivity
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

class SimilaresListaFilmeAdapter(private val similaresActivity: SimilaresActivity, private val lista: List<ResultsSimilarItem?>?) :
        RecyclerView.Adapter<SimilaresListaFilmeAdapter.SimilareViewHolde>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilareViewHolde {
        val view = LayoutInflater.from(similaresActivity).inflate(R.layout.adapter_similares, parent, false)

        return SimilareViewHolde(view)

    }

    override fun onBindViewHolder(holder: SimilareViewHolde, position: Int) {
        val item = lista!![position]
        holder.similares_nome.text = item?.title
        holder.similares_data_lancamento.text = item?.releaseDate
        holder.similares_title_original.text = item?.originalTitle
        holder.similares_voto_media.text = item?.voteAverage.toString()

        Picasso.with(similaresActivity)
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(similaresActivity, 2)) + item?.posterPath)
                .into(holder.imageView)

        holder.itemView.setOnClickListener {
            val intent = Intent(similaresActivity, FilmeActivity::class.java)
            val color = UtilsApp.loadPalette(holder.imageView)
            intent.putExtra(Constantes.COLOR_TOP, color)
            intent.putExtra(Constantes.FILME_ID, item?.id)
            intent.putExtra(Constantes.NOME_FILME, item?.title)
            similaresActivity.startActivity(intent)

        }
    }

    override fun getItemCount(): Int {
        return lista?.size ?: 0
    }

    inner class SimilareViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val imageView: ImageView = itemView.findViewById<View>(R.id.img_similares) as ImageView
        val similares_nome: TextView = itemView.findViewById<View>(R.id.similares_nome) as TextView
        val similares_data_lancamento: TextView = itemView.findViewById<View>(R.id.similares_data_lancamento) as TextView
        val similares_voto_media: TextView = itemView.findViewById<View>(R.id.similares_voto_media) as TextView
        val similares_title_original: TextView = itemView.findViewById<View>(R.id.similares_title_original) as TextView


    }

}
