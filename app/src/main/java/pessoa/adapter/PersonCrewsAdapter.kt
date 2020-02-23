package pessoa.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.person.CrewItem
import filme.activity.MovieDetailsActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 18/08/16.
 */
class PersonCrewsAdapter(private val context: Context, private val personCredits: List<CrewItem?>?) :
    RecyclerView.Adapter<PersonCrewsAdapter.PersonCrewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PersonCrewsViewHolder(parent)
    override fun onBindViewHolder(holder: PersonCrewsViewHolder, position: Int) {
        holder.bind(personCredits?.get(position))
    }

    override fun getItemCount() = personCredits?.size ?: 0

    inner class PersonCrewsViewHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.crews_filmes_layout, parent, false)) {

        val progressBar: ProgressBar = itemView.findViewById<View>(R.id.progress_poster_grid) as ProgressBar
        val poster: ImageView = itemView.findViewById<View>(R.id.img_poster_grid) as ImageView
        val title: TextView = itemView.findViewById<View>(R.id.text_title_crew) as TextView

        fun bind(item: CrewItem?) = with(itemView) {

            fun getYear(date: String?): String {
                return if (date.isNullOrEmpty() ) {
                    "-"
                } else {
                    " - ${date.substring(0, 4)}"
                }
            }

            poster.setPicassoWithCache(item?.posterPath, 5,
                error = {
                    progressBar.visibility = View.INVISIBLE
                    if (item?.mediaType == "tv") {
                        val date = getYear(item.firstAir)
                        title.text = "${item?.name} $date"
                    } else {
                        val date = getYear(item?.releaseDate)
                        title.text = "${item?.title} $date"
                    }
                    title.visible()
                },
                sucesso = {
                title.gone()
                progressBar.gone()
            })



            setOnClickListener {
                when (item?.mediaType?.toLowerCase()) {
                    "movie" -> {
                        context.startActivity(Intent(context, MovieDetailsActivity::class.java).apply {
                            putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                            putExtra(Constantes.FILME_ID, item.id)
                            putExtra(Constantes.NOME_FILME, item.title)
                        })
                    }
                    "tv" -> {
                        context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                            putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(poster))
                            putExtra(Constantes.TVSHOW_ID, item.id)
                            putExtra(Constantes.NOME_TVSHOW, item.title)
                        })
                    }
                    else -> {
                        Toast.makeText(context, context.getString(R.string.ops), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}
