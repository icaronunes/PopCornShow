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
import filme.activity.FilmeActivity
import utils.Constantes
import utils.UtilsApp
import java.lang.Exception

/**
 * Created by icaro on 18/08/16.
 */
class PersonMovieAdapter(private val context: Context, private val personCredits: List<CastItem?>?) : RecyclerView.Adapter<PersonMovieAdapter.PersonMovieViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonMovieAdapter.PersonMovieViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.person_movie_filmes_layout, parent, false)
        return PersonMovieViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonMovieAdapter.PersonMovieViewHolder, position: Int) {

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
            val intent = Intent(context, FilmeActivity::class.java)
            val imageView = view as ImageView
            intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(imageView))
            intent.putExtra(Constantes.FILME_ID, credit?.id)
            intent.putExtra(Constantes.NOME_FILME, credit?.title)
            context.startActivity(intent)
        }

    }

    override fun getItemCount(): Int {
        return personCredits?.size ?: 0
    }

    inner class PersonMovieViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val progressBar: ProgressBar
        val poster: ImageView
        val title: TextView

        init {
            poster = itemView.findViewById<View>(R.id.img_poster_grid) as ImageView
            title = itemView.findViewById<View>(R.id.text_title_crew) as TextView
            progressBar = itemView.findViewById<View>(R.id.progress_poster_grid) as ProgressBar
        }
    }
}
