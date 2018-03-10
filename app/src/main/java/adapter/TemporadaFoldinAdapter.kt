package adapter

import activity.TemporadaActivity
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import br.com.icaro.filme.R
import com.ramotion.foldingcell.FoldingCell
import com.squareup.picasso.Picasso
import domain.UserEp
import domain.UserSeasons
import info.movito.themoviedbapi.model.tv.TvSeason
import kotlinx.android.synthetic.main.epsodio_detalhes.view.*
import kotlinx.android.synthetic.main.foldin_main.view.*
import kotlinx.android.synthetic.main.item_epsodio.view.*
import kotlinx.android.synthetic.main.layout_diretor.view.*
import utils.UtilsApp

/**
 * Created by root on 27/02/18.
 */

class TemporadaFoldinAdapter(val temporadaActivity: TemporadaActivity, val tvSeason: TvSeason,
                             val seasons: UserSeasons?, val seguindo: Boolean, val temporadaOnClickListener: TemporadaAdapter.TemporadaOnClickListener) : RecyclerView.Adapter<TemporadaFoldinAdapter.HoldeTemporada>() {

    private var unfoldedIndexes = HashSet<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemporadaFoldinAdapter.HoldeTemporada? {
        val view = LayoutInflater.from(temporadaActivity).inflate(R.layout.foldin_main, parent, false)
        return HoldeTemporada(view)
    }

    override fun onBindViewHolder(holder: TemporadaFoldinAdapter.HoldeTemporada, position: Int) {

        val ep = tvSeason.episodes[position]
        val epUser = seasons?.userEps?.get(position)

        holder.linear.visibility = if (seguindo) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.titulo.text = ep.name

        holder.numero.text = ep.episodeNumber.toString()
        if (seguindo) {
            holder.visto.setBackgroundColor(if (epUser?.isAssistido!!) temporadaActivity.resources.getColor(R.color.green) else {
                this.temporadaActivity.resources.getColor(R.color.gray_reviews)
            })

            holder.visto.setOnClickListener {
                this.temporadaOnClickListener.onClickVerTemporada(it, position)
            }

            holder.visto_detelhe.setBackgroundColor(if (epUser?.isAssistido) temporadaActivity.resources.getColor(R.color.green) else {
                this.temporadaActivity.resources.getColor(R.color.gray_reviews)
            })

            holder.visto_detelhe.setOnClickListener {
                this.temporadaOnClickListener.onClickVerTemporada(it, position)
            }

        } else {
            holder.visto_detelhe.visibility = View.GONE
            holder.ver_mais.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
        }

        holder.resumo.text = ep.overview
        holder.votos.text = ep.voteCount.toString()


        //  holder.cell.isUnfolded.let {
        Picasso.with(temporadaActivity)
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(temporadaActivity, 3)) + ep.stillPath)
                .error(R.drawable.empty_popcorn)
                .into(holder.img)
        holder.resumo_detalhe.text = ep.overview

        if (ep?.voteAverage?.toString()?.length!! >= 2) {
            holder.detalhes_nota.text = ep.voteAverage.toString().slice(0..2)
            holder.nota.text = ep.voteAverage.toString().slice(0..2)
        }
        holder.detalhes_votos.text = ep.voteCount.toString()
        ep.voteAverage.let {
            holder.detalhes_nota.text = ep?.voteAverage?.toString()
        }

        if (epUser != null && seguindo) {
            holder.nota_user.text = epUser.nota.toString()
            holder.progress_detalhe.rating = epUser.nota
        }

        if (holder.cell.isUnfolded) {
            if (unfoldedIndexes.contains(position)) {
                holder.cell.unfold(true)
                registerToggle(position)
            } else {
                holder.cell.fold(true)
                registerToggle(position)
            }
        }

        val diretor = ep?.credits?.crew?.first {
            it.job == "Director"
        }.toString()

        val escritor = ep?.credits?.crew?.first {
            it.job == "writer"
        }.toString()

        holder.name_diretor.text = if (diretor.equals("null", true)) {
            " - "
        } else {
            diretor
        }
        holder.nome_escritor.text = if (escritor.equals("null", true)) {
            " - "
        } else {
            escritor
        }

        holder.cell.setOnClickListener {
            holder.cell.toggle(false)
            registerToggle(position)
        }

        holder.ver_mais.setOnClickListener {
            this.temporadaOnClickListener.onClickTemporada(it, position)
        }

        holder.linear.setOnClickListener {
            this.temporadaOnClickListener.onClickTemporadaNota(holder.progress_detalhe, ep, position, epUser)
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

    fun notificarMudanca(ep: UserEp?, position: Int){

        seasons?.userEps?.set(position, ep)
        notifyItemChanged(position)

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

        val visto_detelhe = itemView.layout_diretor_nome_visto
        val escritor_img = itemView.layout_diretor_nome_escritor_img
        val diretor_img = itemView.layout_diretor_nome_diretor_img
        val name_diretor = itemView.layout_diretor_nome_diretor
        val nome_escritor = itemView.layout_diretor_nome_escritor
        val linear = itemView.epsodio_detalhes_linear

    }
}
