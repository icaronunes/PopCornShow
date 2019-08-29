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
import domain.person.CrewItem
import filme.activity.FilmeActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.setPicasso

/**
 * Created by icaro on 18/08/16.
 */
class PersonCrewsAdapter(private val context: Context, private val personCredits: List<CrewItem?>?) :
        RecyclerView.Adapter<PersonCrewsAdapter.PersonCrewsViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonCrewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.crews_filmes_layout, parent, false)
        return PersonCrewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonCrewsViewHolder, position: Int) {

        val item = personCredits?.get(position)

//        Picasso.get().load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3))!! + item?.posterPath)
//                .placeholder(R.drawable.poster_empty)
//                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
//                .into(holder.poster, object : Callback {
//                    override fun onError(e: Exception?) {
//                        holder.progressBar.visibility = View.INVISIBLE
//                        val data = StringBuilder()
//                        if (item?.releaseDate != null) {
//                            if (item.releaseDate.length >= 4) {
//                                data.append(if (item.releaseDate.length >= 4) " - " + item.releaseDate.substring(0, 4) else "")
//                            }
//                        }
//                        if (item?.mediaType == "tv") {
//                            holder.title.text = item.name + data
//                        } else {
//                            holder.title.text = item?.title + data
//                        }
//                        holder.title.visibility = View.VISIBLE
//                    }
//
//
//                    override fun onSuccess() {
//                        holder.title.visibility = View.INVISIBLE
//                        holder.progressBar.visibility = View.INVISIBLE
//                    }
//                })
        holder.poster.setPicasso(item?.posterPath, 3, error = {
            holder.progressBar.visibility = View.INVISIBLE
            val data = StringBuilder()
            if (item?.releaseDate != null) {
                if (item.releaseDate.length >= 4) {
                    data.append(if (item.releaseDate.length >= 4) " - " + item.releaseDate.substring(0, 4) else "")
                }
            }
            if (item?.mediaType == "tv") {
                holder.title.text = item.name + data
            } else {
                holder.title.text = item?.title + data
            }
            holder.title.visibility = View.VISIBLE
        }, sucesso = {
            holder.title.visibility = View.INVISIBLE
            holder.progressBar.visibility = View.INVISIBLE
        })


        holder.poster.setOnClickListener {

            if (item?.mediaType == "movie") {
                val intent = Intent(context, FilmeActivity::class.java)
                intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                intent.putExtra(Constantes.FILME_ID, item.id)
                intent.putExtra(Constantes.NOME_FILME, item.title)
                context.startActivity(intent)

            } else if (item?.mediaType == "tv") {
                val intent = Intent(context, TvShowActivity::class.java)
                intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
                intent.putExtra(Constantes.TVSHOW_ID, item.id)
                intent.putExtra(Constantes.NOME_TVSHOW, item.title)
                context.startActivity(intent)
            }
        }

    }

    override fun getItemCount(): Int {
        return personCredits?.size ?: 0
    }

    inner class PersonCrewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val progressBar: ProgressBar = itemView.findViewById<View>(R.id.progress_poster_grid) as ProgressBar
        val poster: ImageView = itemView.findViewById<View>(R.id.img_poster_grid) as ImageView
        val title: TextView = itemView.findViewById<View>(R.id.text_title_crew) as TextView

    }

}
