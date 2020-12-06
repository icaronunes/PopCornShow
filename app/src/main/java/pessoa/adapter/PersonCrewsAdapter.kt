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
import utils.Constant
import utils.UtilsApp
import utils.getNameTypeReel
import utils.gone
import utils.setPicassoWithCache
import utils.visible
import utils.yearDate

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
                    return if(date.length >= 4) " - ${date.substring(0, 4)}" else "-"
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
                            putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(poster))
                            putExtra(Constant.ID, item.id)
                            putExtra(Constant.NOME_FILME, item.title)
                            putExtra(Constant.ID_REEL, "${item.originalTitle?.getNameTypeReel()}-${item.releaseDate?.yearDate()}")
                        })
                    }
                    "tv" -> {
                        context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                            putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(poster))
                            putExtra(Constant.ID, item.id)
                            putExtra(Constant.NOME_TVSHOW, item.title)
                            putExtra(Constant.ID_REEL, "${item.originalName?.getNameTypeReel()}-${item.firstAir?.yearDate()}")
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
