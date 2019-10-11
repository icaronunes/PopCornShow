package tvshow.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.tvshow.ResultsItem
import kotlinx.android.synthetic.main.scroll_similares.view.*
import tvshow.activity.TvShowActivity
import utils.*

class SimilaresSerieAdapter(val activity: FragmentActivity?, private val similarItems: List<ResultsItem?>?)
    : RecyclerView.Adapter<SimilaresSerieAdapter.SimilaresSerieHolde>() {

    override fun onBindViewHolder(holder: SimilaresSerieHolde, position: Int) = holder.bind(similarItems?.get(position)!!)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilaresSerieHolde = SimilaresSerieHolde(parent)

    override fun getItemCount() = similarItems?.size!!

    inner class SimilaresSerieHolde(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(activity).inflate(R.layout.scroll_similares, parent, false)) {
        private var colorTop: Int = 0

        fun bind(tvshow: ResultsItem) = with(itemView) {
            progressBarSimilares.visible()
            textSimilaresName.gone()

            imgPagerSimilares.setPicassoWithCache(tvshow.posterPath, 2, sucesso = {
                progressBarSimilares.gone()
                colorTop = UtilsApp.loadPalette(imgPagerSimilares)
            }, error = {
                progressBarSimilares.gone()
            }, img_erro = R.drawable.poster_empty)

            imgPagerSimilares.setOnClickListener {
                activity?.startActivity(Intent(activity, TvShowActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, colorTop)
                    putExtra(Constantes.NOME_TVSHOW, tvshow.name)
                    putExtra(Constantes.TVSHOW_ID, tvshow.id)
                })
            }
        }
    }
}