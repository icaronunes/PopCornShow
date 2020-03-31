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
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 18/08/16.
 */
class PersonTvAdapter(private val context: Context, private val personCredits: List<CastItem?>) :
    RecyclerView.Adapter<PersonTvAdapter.PersonTvViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PersonTvViewHolder(parent)
    override fun getItemCount() = personCredits.size

    override fun onBindViewHolder(holder: PersonTvViewHolder, position: Int) {
        holder.bind(personCredits[position]!!)
    }

    inner class PersonTvViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.person_midias_filmes_layout, parent, false)) {

        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_poster_grid)
        val poster: ImageView = itemView.findViewById(R.id.img_poster_grid)
        val title: TextView = itemView.findViewById(R.id.text_title_crew)

        fun bind(credit: CastItem) = with(itemView) {
            poster.setPicassoWithCache(credit.posterPath, 5,
                sucesso = {
                    progressBar.gone()
                    title.gone()
                },
                error = {
                    title.visible()
                    progressBar.gone()
                    val date = getYear(credit)
                    title.text = "${credit.name ?: ""} - $date"
                },
                img_erro = R.drawable.poster_empty)

            setOnClickListener {
                context.startActivity(Intent(context, TvShowActivity::class.java).apply {
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(poster))
                    putExtra(Constant.TVSHOW_ID, credit.id)
                    putExtra(Constant.NOME_TVSHOW, credit.title)
                })
            }
        }

        private fun getYear(credit: CastItem?): String {
            return if (credit?.firstAir.isNullOrEmpty() ) {
                ""
            } else {
                credit?.firstAir?.substring(0, 4) ?: ""
            }
        }
    }
}
