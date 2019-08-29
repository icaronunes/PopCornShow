package temporada

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import com.ramotion.foldingcell.FoldingCell
import com.squareup.picasso.Picasso
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import kotlinx.android.synthetic.main.epsodio_detalhes.view.*
import kotlinx.android.synthetic.main.foldin_main.view.*
import kotlinx.android.synthetic.main.item_epsodio.view.*
import kotlinx.android.synthetic.main.layout_diretor.view.*
import utils.UtilsApp

/**
 * Created by root on 27/02/18.
 */

class TemporadaFoldinAdapter(val temporadaActivity: TemporadaActivity, val tvSeason: TvSeasons,
                             val seasons: UserSeasons?, val seguindo: Boolean,
                             val temporadaOnClickListener: TemporadaAdapter.TemporadaOnClickListener)
    : RecyclerView.Adapter<TemporadaFoldinAdapter.HoldeTemporada>() {

    private var unfoldedIndexes = HashSet<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HoldeTemporada {
        val view = LayoutInflater.from(temporadaActivity).inflate(R.layout.foldin_main, parent, false)
        return HoldeTemporada(view)
    }

    override fun onBindViewHolder(holder: HoldeTemporada, position: Int) {

        val ep = tvSeason.episodes?.get(position)
        val epUser = seasons?.userEps?.get(position)

        holder.progressDetalhe.visibility = if (seguindo) {
            View.VISIBLE
        } else {
            View.GONE
        }

        holder.titulo.text = ep?.name

        holder.numero.text = ep?.episodeNumber.toString()
        if (seguindo) {
            holder.visto.setBackgroundColor(if (epUser?.isAssistido!!) temporadaActivity.resources.getColor(R.color.green) else {
                this.temporadaActivity.resources.getColor(R.color.gray_reviews)
            })

            holder.visto.setOnClickListener {
                this.temporadaOnClickListener.onClickVerTemporada(it, position)
            }

            holder.vistoDetelhe.setBackgroundColor(if (epUser.isAssistido) temporadaActivity.resources.getColor(R.color.green) else {
                this.temporadaActivity.resources.getColor(R.color.gray_reviews)
            })

            holder.vistoDetelhe.setOnClickListener {
                this.temporadaOnClickListener.onClickVerTemporada(it, position)
            }

        } else {
            holder.vistoDetelhe.visibility = View.GONE
            holder.verMais.layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT
            holder.visto.visibility = View.GONE
        }

        holder.resumo.text = ep?.overview
        holder.votos.text = ep?.voteCount.toString()
        holder.detalhesVotos.text = ep?.voteCount.toString()

        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp
                        .getTamanhoDaImagem(temporadaActivity, 4)) + ep?.stillPath)
                .error(R.drawable.empty_popcorn)
                .into(holder.img)
        holder.resumoDetalhe.text = ep?.overview

        if (ep?.voteAverage?.toString()?.length!! >= 2) {
            holder.detalhesNota.text = ep.voteAverage.toString().slice(0..2)
            holder.nota.text = ep.voteAverage.toString().slice(0..2)
        }

        ep.voteAverage.let {
            holder.detalhesNota.text = it.toString()
        }

        if (epUser != null && seguindo) {
            holder.notaUser.text = epUser.nota.toString()
            holder.progressDetalhe.rating = epUser.nota
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

        ep.crew?.firstOrNull { it?.job == "Director" }?.let {
            holder.grupWriter.visibility = View.VISIBLE
            holder.nameDiretor.visibility = View.VISIBLE
            holder.nameDiretor.text = it.name
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp
                            .getTamanhoDaImagem(temporadaActivity, 2)) + it.profilePath)
                    .error(R.drawable.person)
                    .into(holder.diretorImg)

        }

        ep.crew?.firstOrNull { it?.job == "Writer" }?.let {
            holder.grupDirector.visibility = View.VISIBLE
            holder.nomeEscritor.visibility = View.VISIBLE
            holder.nomeEscritor.text = it.name
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp
                            .getTamanhoDaImagem(temporadaActivity, 2)) + it.profilePath)
                    .error(R.drawable.person)
                    .into(holder.escritorImg)
        }

        holder.cell.setOnClickListener {
            temporadaOnClickListener.onClickScrool(position)
            holder.cell.toggle(false)
            registerToggle(position)
        }

        holder.verMais.setOnClickListener {
            temporadaOnClickListener.onClickTemporada(it, position)
        }

        holder.wrapperRating.setOnClickListener {
            temporadaOnClickListener.onClickTemporadaNota(holder.progressDetalhe, ep, position, epUser)
        }

    }

    override fun getItemCount(): Int {
        if (seguindo) {
            if (seasons?.userEps?.isNotEmpty()!!) {
                return seasons.userEps!!.size
            }
        } else {
            if (tvSeason.episodes?.isNotEmpty()!!) {
                return tvSeason.episodes.size
            }
        }
        return 0
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

    fun notificarMudanca(ep: UserEp?, position: Int) {
        seasons?.userEps?.set(position, ep!!)
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
        val resumoDetalhe: TextView = itemView.epsodio_detalhes_resumo
        val detalhesNota = itemView.epsodio_detalhes_nota
        val detalhesVotos = itemView.epsodio_detalhes_votos
        val notaUser = itemView.epsodio_detalhes_nota_user
        val progressDetalhe = itemView.epsodio_detalhes_progress
        val verMais = itemView.epsodio_detalhes_ler_mais
        //layout_diretor

        val vistoDetelhe = itemView.layout_diretor_nome_visto
        val escritorImg = itemView.writer_img
        val diretorImg = itemView.img_director
        val nameDiretor = itemView.director_name
        val nomeEscritor = itemView.writer_name
        val wrapperRating = itemView.wrapper_rating
        val grupDirector = itemView.grup_director
        val grupWriter = itemView.grup_writer
    }
}
