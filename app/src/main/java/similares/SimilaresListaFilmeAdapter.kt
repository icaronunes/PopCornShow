package similares

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.ResultsSimilarItem
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.adapter_similares.view.similares_date_avaliable
import kotlinx.android.synthetic.main.adapter_similares.view.similares_img
import kotlinx.android.synthetic.main.adapter_similares.view.similares_name
import kotlinx.android.synthetic.main.adapter_similares.view.similares_rated
import kotlinx.android.synthetic.main.adapter_similares.view.similares_title_original
import utils.Constant
import utils.UtilsApp
import utils.parseDateShot
import utils.setPicassoWithCache

/**
 * Created by icaro on 12/08/16.
 */

class SimilaresListaFilmeAdapter(
    private val activity: SimilaresActivity,
    private val listSimilares: List<ResultsSimilarItem?>?
) : RecyclerView.Adapter<SimilaresListaFilmeAdapter.SimilareViewHolde>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilareViewHolde {
        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_similares, parent, false)
        return SimilareViewHolde(view)
    }

    override fun onBindViewHolder(holder: SimilareViewHolde, position: Int) {
        holder.bind(listSimilares?.get(position)!!)
    }

    override fun getItemCount(): Int {
        return listSimilares?.size ?: 0
    }

    inner class SimilareViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ResultsSimilarItem) = with(itemView) {
            item.title?.let { similares_name.text = it }
            item.releaseDate?.let { similares_date_avaliable.text = it.parseDateShot() }
            item.originalTitle?.let { similares_title_original.text = it }
            item.voteAverage?.let { similares_rated.text = it.toString() }
            similares_img.setPicassoWithCache(item.posterPath, 2)

            itemView.setOnClickListener {
                activity.startActivity(Intent(activity, MovieDetailsActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(similares_img))
                    putExtra(Constant.FILME_ID, item.id)
                    putExtra(Constant.NOME_FILME, item.title)
                })
            }
        }
    }
}
