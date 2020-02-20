package pessoa.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.person.CastItem
import filme.activity.MovieDetailsActivity
import utils.Constantes
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 18/08/16.
 */
class PersonMovieAdapter(private val context: Context, private val personCredits: List<CastItem?>?) :
    RecyclerView.Adapter<PersonMovieAdapter.PersonMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PersonMovieViewHolder(parent)
    override fun getItemCount() = personCredits?.size ?: 0
    override fun onBindViewHolder(holder: PersonMovieViewHolder, position: Int) {
        holder.bind(personCredits?.get(position))
    }

    inner class PersonMovieViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.person_midias_filmes_layout, parent, false)) {
        fun bind(credit: CastItem?) = with(itemView) {
            poster.setPicassoWithCache(credit?.posterPath, 5,
                sucesso = {
                    title.gone()
                    progressBar.gone()
                },
                error = {
                    val date = credit?.releaseDate?.substring(0, 4) ?: ""
                    title.text = "${credit?.title ?: ""} - $date"
                    progressBar.gone()
                    title.visible()
                },
                img_erro = R.drawable.poster_empty)


            setOnClickListener {
                context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                    putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                    putExtra(Constantes.FILME_ID, credit?.id)
                    putExtra(Constantes.NOME_FILME, credit?.title)
                })
            }
        }

        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_poster_grid)
        val poster: ImageView = itemView.findViewById(R.id.img_poster_grid)
        val title: TextView = itemView.findViewById(R.id.text_title_crew)
    }
}
