package pessoa.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.person.ProfilesItem
import pessoa.activity.FotoPersonActivity
import utils.Constantes
import utils.gone
import utils.setPicassoWithCache
import java.io.Serializable

/**
 * Created by icaro on 18/08/16.
 */
class PersonImagemAdapter(private val context: Context, private val artworks: List<ProfilesItem?>?, private val nome: String?)
    : RecyclerView.Adapter<PersonImagemAdapter.PersonImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PersonImageViewHolder(parent)
    override fun onBindViewHolder(holder: PersonImageViewHolder, position: Int) {
        holder.bind(artworks!![position])
    }

    override fun getItemCount() = artworks?.size ?: 0

    inner class PersonImageViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false)) {
        val progressBar: ProgressBar = itemView.findViewById(R.id.progress_poster_grid)
        val image: ImageView = itemView.findViewById(R.id.img_poster_grid)

        fun bind(item: ProfilesItem?) = with(itemView) {
            image.setPicassoWithCache(item?.filePath ?: "", 5,
                error = {
                    progressBar.gone()
                }, sucesso = {
                progressBar.gone()
            })

            setOnClickListener {
                context.startActivity(Intent(context, FotoPersonActivity::class.java).apply {
                    putExtra(Constantes.PERSON, artworks as Serializable)
                    putExtra(Constantes.NOME_PERSON, nome)
                    putExtra(Constantes.POSICAO, position)
                })
            }
        }
    }
}
