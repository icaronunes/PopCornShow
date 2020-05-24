package temporada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.EpisodesItem
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_img
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_ler_mais
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_nota
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_nota_user
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_progress
import kotlinx.android.synthetic.main.epsodio_detalhes.view.epsodio_detalhes_votos
import kotlinx.android.synthetic.main.epsodio_detalhes.view.layout_diretor_nome_visto
import kotlinx.android.synthetic.main.epsodio_detalhes.view.wrapper_rating
import kotlinx.android.synthetic.main.foldin_main.view.folding_cell
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_nota
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_number
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_titulo
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_titulo_resumo
import kotlinx.android.synthetic.main.item_epsodio.view.item_epsodio_visto
import kotlinx.android.synthetic.main.item_epsodio.view.label_ep
import kotlinx.android.synthetic.main.layout_diretor.view.director_name
import kotlinx.android.synthetic.main.layout_diretor.view.grup_director
import kotlinx.android.synthetic.main.layout_diretor.view.grup_writer
import kotlinx.android.synthetic.main.layout_diretor.view.img_director
import kotlinx.android.synthetic.main.layout_diretor.view.writer_img
import kotlinx.android.synthetic.main.layout_diretor.view.writer_name
import utils.Constant
import utils.gone
import utils.invisible
import utils.makeToast
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by root on 27/02/18.
 */

class TemporadaFoldinAdapter(
    val temporadaActivity: TemporadaActivity,
    private val tvSeason: TvSeasons,
    val seasons: UserSeasons?,
    val seguindo: Boolean,
    private val temporadaOnClickListener: TemporadaAdapter.TemporadaOnClickListener
) :
    RecyclerView.Adapter<TemporadaFoldinAdapter.HoldeTemporada>() {

    private var unfoldedIndexes = HashSet<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HoldeTemporada(parent)
    override fun onBindViewHolder(holder: HoldeTemporada, position: Int) {
        val ep = tvSeason.episodes?.get(position)!!
        val epUser = seasons?.userEps?.get(position)
        val watchSeason = seasons?.isVisto ?: false
        holder.bind(ep, epUser, watchSeason)
    }

    override fun getItemCount() = tvSeason.episodes?.size ?: 0

    private fun registerToggle(position: Int) = if (unfoldedIndexes.contains(position)) registerFold(position)
    else registerUnfold(position)

    private fun registerFold(position: Int) = unfoldedIndexes.remove(position)

    private fun registerUnfold(position: Int) = unfoldedIndexes.add(position)

    fun notificarMudanca(ep: UserEp?, position: Int) {
        seasons?.userEps?.set(position, ep!!)
        notifyItemChanged(position)
    }

    inner class HoldeTemporada(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(temporadaActivity).inflate(R.layout.foldin_main, parent, false)) {

        fun bind(ep: EpisodesItem, epUser: UserEp?, watchSeason: Boolean): Unit = with(itemView) {
            epsodio_detalhes_progress.visibility = if (seguindo) View.VISIBLE else View.GONE
            item_epsodio_titulo.text = ep.name
            item_epsodio_titulo_resumo.text = ep.overview
            ep.episodeNumber.let {
                if(it != null) item_epsodio_number.text = it.toString() else label_ep.invisible()
            }
            epsodio_detalhes_votos.text = ep.voteCount.toString()
            epsodio_detalhes_img.setPicassoWithCache(ep.stillPath, 5, img_erro = R.drawable.empty_popcorn)
            epsodio_detalhes_nota.text = ep.overview

            ep.voteAverage?.let {
                epsodio_detalhes_nota.text = it.toString().slice(0..2)
                item_epsodio_nota.text = it.toString().slice(0..2)
            }

            if (folding_cell.isUnfolded) {
                if (unfoldedIndexes.contains(layoutPosition)) {
                    folding_cell.folding_cell.unfold(true)
                } else {
                    folding_cell.fold(true)
                }
                registerToggle(layoutPosition)
            }

            ep.crew?.firstOrNull { it?.job == Constant.DIRECTOR }?.let {
                grup_director.visible()
                director_name.text = it.name
                img_director.setPicassoWithCache(it.profilePath, 3, img_erro = R.drawable.person)
            }

            ep.crew?.firstOrNull { it?.job == Constant.WRITER }?.let {
                grup_writer.visible()
                writer_name.text = it.name
                writer_img.setPicassoWithCache(it.profilePath, 3, img_erro = R.drawable.person)
            }

            epUser?.nota?.let {
                epsodio_detalhes_nota_user.text = it.toString()
                epsodio_detalhes_progress.rating = it
            }

            layout_diretor_nome_visto.setOnClickListener { temporadaOnClickListener.onClickVerTemporada(layoutPosition) }
            epsodio_detalhes_ler_mais.setOnClickListener { temporadaOnClickListener.onClickTemporada(layoutPosition) }
            wrapper_rating.setOnClickListener { temporadaOnClickListener.onClickTemporadaNota(epsodio_detalhes_progress, ep, layoutPosition, epUser) }

            folding_cell.setOnClickListener {
                temporadaOnClickListener.onClickScrool(position)
                try {
                    folding_cell.toggle(false)
                    registerToggle(layoutPosition)
                } catch (ex: IllegalStateException) {
                    temporadaActivity.makeToast(R.string.ops)
                }
            }

            if (seguindo) {
                when (epUser?.isAssistido == true || watchSeason) {
                    true -> {
                        item_epsodio_visto.apply {
                            setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        }
                        layout_diretor_nome_visto.apply {
                            setBackgroundColor(ContextCompat.getColor(context, R.color.green))
                        }
                    }
                    false -> {
                        item_epsodio_visto.apply {
                            setBackgroundColor(ContextCompat.getColor(context, R.color.gray_reviews))
                        }
                        layout_diretor_nome_visto.apply {
                            setBackgroundColor(ContextCompat.getColor(context, R.color.gray_reviews))
                        }
                    }
                }
            } else {
                layout_diretor_nome_visto.gone()
                epsodio_detalhes_ler_mais.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            }
        }
    }
}
