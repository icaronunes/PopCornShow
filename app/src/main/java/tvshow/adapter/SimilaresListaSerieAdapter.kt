package tvshow.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.tvshow.ResultsItem
import kotlinx.android.synthetic.main.adapter_similares.view.*
import similares.SimilaresActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
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
            item.name?.let { similares_nome.text = item.name }
            item.firstAirDate?.let { similares_data_lancamento.text = it }
            item.originalName?.let { similares_title_original.text = it }
            item.voteAverage?.let { similares_voto_media.text = it.toString() }
            img_similares.setPicassoWithCache(item.posterPath, 2, img_erro = R.drawable.poster_empty)

            setOnClickListener {
                activity.startActivity(Intent(activity, TvShowActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(img_similares))
                    putExtra(Constantes.TVSHOW_ID, item.id)
                })
            }
        }
    }
}
