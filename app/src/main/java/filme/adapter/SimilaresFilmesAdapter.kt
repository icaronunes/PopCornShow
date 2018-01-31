package filme.adapter

import android.content.Context
import android.content.Intent
import android.support.v4.app.FragmentActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.ResultsSimilarItem
import filme.activity.FilmeActivity
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 22/02/17.
 */
class SimilaresFilmesAdapter(activity: FragmentActivity, val similarItems: List<ResultsSimilarItem?>?) : RecyclerView.Adapter<SimilaresFilmesAdapter.SimilaresViewHolder>() {

    private val context: Context
    private var color_top: Int = 0

    init {
        context = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilaresViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.scroll_similares, parent, false)
        return SimilaresViewHolder(view)
    }

    override fun onBindViewHolder(holder: SimilaresViewHolder, position: Int) {
        val it = similarItems?.get(position)!!
        holder.progressBarSimilares.visibility = View.VISIBLE
        if (it.title != null && it.posterPath != null) {
            holder.textSimilares.visibility = View.GONE
            Picasso.with(context)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2))!! + it.posterPath)
                    .placeholder(R.drawable.poster_empty)
                    .into(holder.imgPagerSimilares, object : Callback {
                        override fun onSuccess() {
                            color_top = UtilsApp.loadPalette(holder.imgPagerSimilares)
                            holder.progressBarSimilares.visibility = View.GONE
                        }

                        override fun onError() {
                            holder.progressBarSimilares.visibility = View.GONE
                        }
                    })

            holder.imgPagerSimilares.setOnClickListener { view ->
                val intent = Intent(context, FilmeActivity::class.java)
                intent.putExtra(Constantes.COLOR_TOP, color_top)
                intent.putExtra(Constantes.NOME_FILME, it.title)
                intent.putExtra(Constantes.FILME_ID, it.id)
                context.startActivity(intent)

            }

        }
    }

    override fun getItemCount(): Int {
        return similarItems?.size!!
    }


    inner class SimilaresViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal val progressBarSimilares: ProgressBar
        internal val textSimilares: TextView
        internal val imgPagerSimilares: ImageView

        init {
            progressBarSimilares = itemView.findViewById<View>(R.id.progressBarSimilares) as ProgressBar
            textSimilares = itemView.findViewById<View>(R.id.textSimilaresNome) as TextView
            imgPagerSimilares = itemView.findViewById<View>(R.id.imgPagerSimilares) as ImageView
        }
    }
}
