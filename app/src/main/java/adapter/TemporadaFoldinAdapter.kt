package adapter

import activity.TemporadaActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.ramotion.foldingcell.FoldingCell
import com.squareup.picasso.Picasso
import domain.UserSeasons
import info.movito.themoviedbapi.model.tv.TvSeason
import kotlinx.android.synthetic.main.epsodio_detalhes.view.*
import kotlinx.android.synthetic.main.foldin_main.view.*
import kotlinx.android.synthetic.main.item_epsodio.view.*
import utils.UtilsApp

/**
 * Created by root on 27/02/18.
 */

class TemporadaFoldinAdapter(val temporadaActivity: TemporadaActivity, val tvSeason: TvSeason,
                             val seasons: UserSeasons?, val seguindo: Boolean, valtemporadaOnClickListener: TemporadaAdapter.TemporadaOnClickListener) : RecyclerView.Adapter<TemporadaFoldinAdapter.HoldeTemporada>() {

    private var unfoldedIndexes = HashSet<Int>()


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemporadaFoldinAdapter.HoldeTemporada? {
        val view = LayoutInflater.from(temporadaActivity).inflate(R.layout.foldin_main, parent, false)
        return HoldeTemporada(view)
    }

    override fun onBindViewHolder(holder: TemporadaFoldinAdapter.HoldeTemporada, position: Int) {

        val ep = tvSeason.episodes[position]

        holder.titulo.text = ep.name
        //holder.nota.text = seasons.userEps[position].nota.toString()
        holder.numero.text = ep.episodeNumber.toString()
        val color = if (seasons?.isVisto!!) temporadaActivity.resources.getColor(R.color.green) else {
            temporadaActivity.resources.getColor(R.color.gray_reviews)
        }
        holder.visto.setBackgroundColor(color)
        holder.resumo.text = ep.overview
        holder.votos.text = ep.voteCount.toString()


        holder.cell.isUnfolded.let {
            Picasso.with(temporadaActivity)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(temporadaActivity, 3)) + ep.stillPath)
                    .error(R.drawable.empty_popcorn)
                    .into(holder.img)
            holder.resumo_detalhe.text = ep.overview
            holder.nota_user.text = seasons?.userEps[position].nota.toString()
            holder.detalhes_votos.text = ep.voteCount.toString()
            ep.voteAverage.let {
                holder.detalhes_nota.text = ep?.voteAverage?.toString()
            }

        }

        if (holder.cell.isUnfolded) {
            if (unfoldedIndexes.contains(position)) {
                holder.cell.unfold(true)
                //registerFold(position)
                // registerToggle(position)
            } else {
                holder.cell.fold(true)
                // registerFold(position)
                // registerToggle(position)
            }
        }

        holder.cell.setOnClickListener {

            holder.cell.toggle(false)
            // registerFold(position)
            registerToggle(position)
        }
    }

    override fun getItemCount(): Int {
        if (seguindo) {
            seasons?.userEps.let {
                return it?.size!!
            }
        } else {
            tvSeason.episodes.let {
                return it.size
            }
        }
    }

    private fun registerToggle(position: Int) {
        if (unfoldedIndexes.contains(position))
            registerFold(position)
        else
            registerUnfold(position)
    }

    private fun registerFold(position: Int) {
        unfoldedIndexes.remove(position)
    }

    private fun registerUnfold(position: Int) {
        unfoldedIndexes.add(position)
    }

    inner class HoldeTemporada(itemView: View) : RecyclerView.ViewHolder(itemView) {

        //Foldin
        val cell: FoldingCell = itemView.folding_cell
        //item_epsodio
        val titulo = itemView.item_epsodio_titulo
        val resumo = itemView.item_epsodio_titulo_resumo
        val numero = itemView.item_epsodio_numero
        val visto = itemView.item_epsodio_visto
        val votos = itemView.item_epsodio_votos
        val nota = itemView.item_epsodio_nota
        //epsodio_detalhes
        val img = itemView.epsodio_detalhes_img
        val resumo_detalhe = itemView.epsodio_detalhes_resumo
        val detalhes_nota = itemView.epsodio_detalhes_nota
        val detalhes_votos = itemView.epsodio_detalhes_votos
        val nota_user = itemView.epsodio_detalhes_nota_user
        val progress_detalhe = itemView.epsodio_detalhes_progress
        val ver_mais = itemView.epsodio_detalhes_ler_mais
        //layout_diretor

    }
}
