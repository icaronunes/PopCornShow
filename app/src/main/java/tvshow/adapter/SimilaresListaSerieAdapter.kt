package tvshow.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.tvshow.ResultsItem
import kotlinx.android.synthetic.main.adapter_similares.view.similares_date_avaliable
import kotlinx.android.synthetic.main.adapter_similares.view.similares_img
import kotlinx.android.synthetic.main.adapter_similares.view.similares_name
import kotlinx.android.synthetic.main.adapter_similares.view.similares_rated
import kotlinx.android.synthetic.main.adapter_similares.view.similares_title_original
import similares.SimilaresActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.parseDateShot
import utils.setPicassoWithCache

/**
 * Created by root on 30/09/17.
 */
class SimilaresListaSerieAdapter(
    private val activity: SimilaresActivity,
    private val listaTvshow: List<ResultsItem?>?
) :
    RecyclerView.Adapter<SimilaresListaSerieAdapter.SimilareViewHolde>() {

    override fun onBindViewHolder(holder: SimilareViewHolde, position: Int) {
        holder.bind(listaTvshow?.get(position)!!)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilareViewHolde {
        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_similares, parent, false)
        return SimilareViewHolde(view)
    }

    override fun getItemCount(): Int {
        return listaTvshow?.size!!
    }

    inner class SimilareViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: ResultsItem) = with(itemView) {
            item.name?.let { similares_name.text = item.name }
            item.firstAirDate?.let { similares_date_avaliable.text = it.parseDateShot() }
            item.originalName?.let { similares_title_original.text = it }
            item.voteAverage?.let { similares_rated.text = it.toString() }
            similares_img.setPicassoWithCache(item.posterPath, 3, img_erro = R.drawable.poster_empty)

            setOnClickListener {
                activity.startActivity(Intent(activity, TvShowActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(similares_img))
                    putExtra(Constant.TVSHOW_ID, item.id)

                })
            }
        }
    }
}
