package pessoa.adapter

import pessoa.activity.FotoPersonActivity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ProgressBar
import br.com.icaro.filme.R
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.person.ProfilesItem
import utils.Constantes
import utils.UtilsApp
import java.io.Serializable

/**
 * Created by icaro on 18/08/16.
 */
class PersonImagemAdapter(private val context: Context, private val artworks: List<ProfilesItem?>?, private val nome: String?) : RecyclerView.Adapter<PersonImagemAdapter.PersonImageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PersonImagemAdapter.PersonImageViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false)

        return PersonImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: PersonImagemAdapter.PersonImageViewHolder, position: Int) {
        val item = artworks!![position]

        Picasso.with(context).load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3)) + item?.filePath)
                .placeholder(R.drawable.person)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageButton, object : Callback {
                    override fun onSuccess() {
                        holder.progressBar.visibility = View.GONE
                    }

                    override fun onError() {
                        holder.progressBar.visibility = View.GONE
                    }
                })

        holder.imageButton.setOnClickListener {
            val intent = Intent(context, FotoPersonActivity::class.java)
            intent.putExtra(Constantes.PERSON, artworks as Serializable)
            intent.putExtra(Constantes.NOME_PERSON, nome)
            intent.putExtra(Constantes.POSICAO, position)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return if (artworks != null && artworks.isNotEmpty()) {
            artworks.size
        } else 0
    }

    inner class PersonImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
         val progressBar: ProgressBar
         val imageButton: ImageButton

        init {
            progressBar = itemView.findViewById<View>(R.id.progress_poster_grid) as ProgressBar
            imageButton = itemView.findViewById<View>(R.id.img_poster_grid) as ImageButton
        }
    }
}
