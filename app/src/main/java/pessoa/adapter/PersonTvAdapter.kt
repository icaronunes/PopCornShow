package pessoa.adapter

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.person.CastItem
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import java.lang.Exception

/**
 * Created by icaro on 18/08/16.
 */
class PersonTvAdapter(private val context: Context, private val personCredits: List<CastItem?>) : RecyclerView.Adapter<PersonTvAdapter.PersonTvViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonTvViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_movie_filmes_layout, parent, false)
        return PersonTvViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonTvAdapter.PersonTvViewHolder, position: Int) {

        val credit = personCredits[position]

            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3)) + credit?.posterPath)
                    .error(R.drawable.poster_empty)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.poster, object : Callback {
                        override fun onError(e: Exception?) {
                            holder.progressBar.visibility = View.INVISIBLE
                            val data = StringBuilder()
                            if (!credit?.releaseDate.isNullOrBlank()) {
                                data.append(if (credit?.releaseDate?.length!! >= 4) " - " + credit.releaseDate.substring(0, 4) else "")
                            }
                            holder.title.text = credit?.name + data
                            holder.title.visibility = View.VISIBLE
                        }

                        override fun onSuccess() {
                            holder.progressBar.visibility = View.INVISIBLE
                            holder.title.visibility = View.GONE
                        }
                    })

            holder.poster.setOnClickListener { view ->
                val intent = Intent(context, TvShowActivity::class.java)
                val imageView = view as ImageView
                val color = UtilsApp.loadPalette(imageView)
                intent.putExtra(Constantes.COLOR_TOP, color)
                intent.putExtra(Constantes.TVSHOW_ID, credit?.id)
                intent.putExtra(Constantes.NOME_TVSHOW, credit?.title)
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

