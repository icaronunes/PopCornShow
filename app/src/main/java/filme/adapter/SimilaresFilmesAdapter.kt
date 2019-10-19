package filme.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.ResultsSimilarItem
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.scroll_similares.view.imgPagerSimilares
import kotlinx.android.synthetic.main.scroll_similares.view.progressBarSimilares
import kotlinx.android.synthetic.main.scroll_similares.view.textSimilaresName
import utils.Constantes
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 22/02/17.
 */
class SimilaresFilmesAdapter(val context: FragmentActivity, private val similarItems: List<ResultsSimilarItem?>?)
    : RecyclerView.Adapter<SimilaresFilmesAdapter.SimilaresViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SimilaresViewHolder(parent)

    override fun onBindViewHolder(holder: SimilaresViewHolder, position: Int) {
        holder.bind(similarItems?.get(position)!!)
    }

    override fun getItemCount(): Int {
        return similarItems?.size!!
    }

    inner class SimilaresViewHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.scroll_similares, parent, false)) {
        fun bind(item: ResultsSimilarItem) = with(itemView) {
            progressBarSimilares.visible()
            item.title.let {
                textSimilaresName.text = it
            }

            item.posterPath.let {
                imgPagerSimilares.setPicassoWithCache(it, 2,
                    {
                        textSimilaresName.gone()
                        progressBarSimilares.gone()
                    },
                    {
                        textSimilaresName.visible()
                        progressBarSimilares.gone()
                    },
                    img_erro = R.drawable.poster_empty)
            }

            setOnClickListener {
                context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(imgPagerSimilares))
                    putExtra(Constantes.NOME_FILME, item.title)
                    putExtra(Constantes.FILME_ID, item.id)
                })
            }
        }
    }
}
