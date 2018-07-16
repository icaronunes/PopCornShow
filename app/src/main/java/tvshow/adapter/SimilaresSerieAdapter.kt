package tvshow.adapter

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
import domain.tvshow.ResultsItem
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import java.lang.Exception

class SimilaresSerieAdapter(val activity: FragmentActivity?, val similarItems: List<ResultsItem?>?) : RecyclerView.Adapter<SimilaresSerieAdapter.SimilaresSerieHolde>() {

    private var color_top: Int = 0

    override fun onBindViewHolder(holder: SimilaresSerieHolde, position: Int) {
        val tvshow = similarItems?.get(position)
        holder.progressBarSimilares.visibility = View.VISIBLE
        if (tvshow?.posterPath != null) {
            holder.textSimilares.visibility = View.GONE
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(activity, 2)) + tvshow.posterPath)
                    .placeholder(R.drawable.poster_empty)
                    .into(holder.imgPagerSimilares, object : Callback {
                        override fun onError(e: Exception?) {
                            holder.progressBarSimilares.visibility = View.GONE
                        }

                        override fun onSuccess() {
                            color_top = UtilsApp.loadPalette(holder.imgPagerSimilares)
                            holder.progressBarSimilares.visibility = View.GONE
                        }
                    })

            holder.imgPagerSimilares.setOnClickListener { view ->
                val intent = Intent(activity, TvShowActivity::class.java)
                intent.putExtra(Constantes.COLOR_TOP, color_top)
                intent.putExtra(Constantes.NOME_TVSHOW, tvshow.name)
                intent.putExtra(Constantes.TVSHOW_ID, tvshow.id)
                activity?.startActivity(intent)

            }

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SimilaresSerieHolde {
        val view = LayoutInflater.from(activity).inflate(R.layout.scroll_similares, parent, false)
        return SimilaresSerieHolde(view)
    }

    override fun getItemCount(): Int {
        return similarItems?.size!!
    }


    inner class SimilaresSerieHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

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