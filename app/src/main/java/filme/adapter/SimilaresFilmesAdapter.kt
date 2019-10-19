package filme.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.ResultsSimilarItem
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.poster_main.view.img_poster_grid
import kotlinx.android.synthetic.main.poster_main.view.layout_poster_main
import kotlinx.android.synthetic.main.poster_main.view.progress_poster_grid
import kotlinx.android.synthetic.main.poster_main.view.title_main
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
            progress_poster_grid.visible()
            layout_poster_main.setCardBackgroundColor(ContextCompat.getColor(context, R.color.primary))
            item.title.let {
                title_main.text = it
            }

            item.posterPath.let {
                img_poster_grid.setPicassoWithCache(it, 2, sucesso = {
                    title_main.gone()
                    progress_poster_grid.gone()
                    layout_poster_main.setCardBackgroundColor(UtilsApp.loadPalette(img_poster_grid))
                }, error =
                {
                    title_main.visible()
                    progress_poster_grid.gone()
                },
                    img_erro = R.drawable.poster_empty)
            }

            setOnClickListener {
                context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(img_poster_grid))
                    putExtra(Constantes.NOME_FILME, item.title)
                    putExtra(Constantes.FILME_ID, item.id)
                })
            }
        }
    }
}
