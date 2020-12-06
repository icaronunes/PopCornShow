package tvshow.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.tvshow.ResultsItem
import kotlinx.android.synthetic.main.poster_main.view.img_poster_grid
import kotlinx.android.synthetic.main.poster_main.view.layout_poster_main
import kotlinx.android.synthetic.main.poster_main.view.progress_poster_grid
import kotlinx.android.synthetic.main.poster_main.view.title_main
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.getNameTypeReel
import utils.gone
import utils.setPicassoWithCache
import utils.visible
import utils.yearDate

class SimilaresSerieAdapter(val activity: FragmentActivity, private val similarItems: List<ResultsItem?>?) :
    RecyclerView.Adapter<SimilaresSerieAdapter.SimilaresSerieHolde>() {

    override fun onBindViewHolder(holder: SimilaresSerieHolde, position: Int) = holder.bind(similarItems?.get(position)!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilaresSerieHolde = SimilaresSerieHolde(parent)

    override fun getItemCount() = similarItems?.size!!

    inner class SimilaresSerieHolde(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(activity).inflate(R.layout.scroll_similares, parent, false)) {

        fun bind(tvshow: ResultsItem) = with(itemView) {
            layout_poster_main.setCardBackgroundColor(ContextCompat.getColor(context, R.color.accent))
            progress_poster_grid.visible()
            title_main.text = tvshow.name

            img_poster_grid.setPicassoWithCache(tvshow.posterPath, 3, sucesso = {
                progress_poster_grid.gone()
                title_main.gone()
                layout_poster_main.setCardBackgroundColor(UtilsApp.loadPalette(img_poster_grid))
            }, error = {
                progress_poster_grid.gone()
                title_main.visible()
            }, img_erro = R.drawable.poster_empty)

            setOnClickListener {
                activity.startActivity(Intent(activity, TvShowActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(img_poster_grid))
                    putExtra(Constant.NOME_TVSHOW, tvshow.name)
                    putExtra(Constant.ID, tvshow.id)
                    putExtra(Constant.ID_REEL, "${tvshow.originalName?.getNameTypeReel()}-${tvshow.firstAirDate?.yearDate()}")
                })
            }
        }
    }
}
