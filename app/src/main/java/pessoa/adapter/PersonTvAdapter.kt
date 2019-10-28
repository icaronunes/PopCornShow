package pessoa.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.person.CastItem
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.setPicasso

/**
 * Created by icaro on 18/08/16.
 */
class PersonTvAdapter(private val context: Context, private val personCredits: List<CastItem?>) :
        RecyclerView.Adapter<PersonTvAdapter.PersonTvViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonTvViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_movie_filmes_layout, parent, false)
        return PersonTvViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonTvViewHolder, position: Int) {

        val credit = personCredits[position]!!
        holder.poster.setPicasso(credit.posterPath, 5,
                error = {
                    holder.progressBar.visibility = View.INVISIBLE
                    if (credit.releaseDate != null) {
                        val data = if (credit.releaseDate.length >= 4) " - " + credit.releaseDate.substring(0, 4) else ""
                        holder.title.text = "${credit.name} $data"
                    } else {
                        holder.title.text = "${credit.name}"
                    }
                    holder.title.visibility = View.VISIBLE
                }, sucesso = {
            holder.progressBar.visibility = View.INVISIBLE
            holder.title.visibility = View.GONE
        })

        holder.poster.setOnClickListener {
            val intent = Intent(context, TvShowActivity::class.java).apply {
                putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                putExtra(Constantes.TVSHOW_ID, credit.id)
                putExtra(Constantes.NOME_TVSHOW, credit.title)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return if (personCredits.isNotEmpty()) {
            personCredits.size
        } else 0
    }

    inner class PersonTvViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_poster_grid)
        val poster: ImageView = itemView.findViewById(R.id.img_poster_grid)
        val title: TextView = itemView.findViewById(R.id.text_title_crew)
    }
}
