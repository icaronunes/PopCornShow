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
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.person.CastItem
import filme.activity.FilmeActivity
import utils.Constantes
import utils.UtilsApp
import java.lang.Exception

/**
 * Created by icaro on 18/08/16.
 */
class PersonMovieAdapter(private val context: Context, private val personCredits: List<CastItem?>?)
    : RecyclerView.Adapter<PersonMovieAdapter.PersonMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonMovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_movie_filmes_layout, parent, false)
        return PersonMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonMovieViewHolder, position: Int) {

        val credit = personCredits?.get(position)

        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + credit?.posterPath)
                .error(R.drawable.poster_empty)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.poster, object : Callback {
                    override fun onError(e: Exception?) {
                        holder.progressBar.visibility = View.INVISIBLE
                        val data = StringBuilder()
                        if (!credit?.releaseDate.isNullOrBlank() ) {
                            data.append(if (credit?.releaseDate?.length!! >= 4) " - " + credit.releaseDate.substring(0, 4) else "")
                        }
                        holder.title.text = credit?.title + data
                        holder.title.visibility = View.VISIBLE
                    }

                    override fun onSuccess() {
                        holder.progressBar.visibility = View.INVISIBLE
                        holder.title.visibility = View.GONE
                    }

                })

        holder.poster.setOnClickListener { view ->
            context.startActivity(Intent(context, FilmeActivity::class.java).apply {
            putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
            putExtra(Constantes.FILME_ID, credit?.id)
            putExtra(Constantes.NOME_FILME, credit?.title)
            })
        }
    }

    override fun getItemCount(): Int {
        return personCredits?.size ?: 0
    }

    inner class PersonMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_poster_grid)
        val poster: ImageView = itemView.findViewById(R.id.img_poster_grid)
        val title: TextView = itemView.findViewById(R.id.text_title_crew)

    }
}
