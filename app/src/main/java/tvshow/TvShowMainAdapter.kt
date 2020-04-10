package tvshow

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.Keep
import androidx.core.app.ActivityCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.ListaItemSerie
import domain.ListaSeries
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

/**
 * Created by icaro on 17/02/17.
 */
@Keep
class TvShowMainAdapter(activity: FragmentActivity, private val popularTvshow: ListaSeries) : RecyclerView.Adapter<TvShowMainAdapter.TvShowPopularesViewHolder>() {
    private val context: Context
    private var color: Int = 0

    init {
        context = activity
        this.color = ActivityCompat.getColor(context, R.color.white)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TvShowPopularesViewHolder(parent)

    override fun onBindViewHolder(holder: TvShowPopularesViewHolder, position: Int) {
        holder.bind(popularTvshow.results[position])
    }

    override fun getItemCount() = if (popularTvshow.results.size < 15) popularTvshow.results.size else 15

    inner class TvShowPopularesViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.poster_main, parent, false)) {
        fun bind(item: ListaItemSerie) = with(itemView) {
            img_poster_grid.setBackgroundColor(ActivityCompat.getColor(context, R.color.accent))
            img_poster_grid.setPicassoWithCache(item.posterPath, 6,
                {
                    progress_poster_grid.gone()
                    color = UtilsApp.loadPalette(img_poster_grid)
                    layout_poster_main.setCardBackgroundColor(color)
                },
                {
                    progress_poster_grid.gone()
                    title_main.apply {
                        text = item.name
                        title_main.visible()
                    }
                }, img_erro = R.drawable.poster_empty)

            img_poster_grid.setOnClickListener {
                context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                    putExtra(Constant.NOME_TVSHOW, item.name)
                    putExtra(Constant.TVSHOW_ID, item.id)
                    putExtra(Constant.ID_REEL, "${item.originalName?.getNameTypeReel()}-${item.firstAirDate?.yearDate()}")
                    putExtra(Constant.COLOR_TOP, color)
                })
            }
        }
    }
}
