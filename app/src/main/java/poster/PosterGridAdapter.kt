package poster

import android.R.anim
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import br.com.icaro.filme.R.id
import br.com.icaro.filme.R.layout
import domain.PostersItem
import poster.PosterGridAdapter.*
import utils.Constant
import utils.gone
import utils.setPicassoWithCache
import java.io.Serializable

/**
 * Created by icaro on 28/07/16.
 */
class PosterGridAdapter(
    private val context: Context,
    private val artworks: List<PostersItem>,
    private val nome: String,
) : Adapter<PosterViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PosterViewHolder(parent)
    override fun onBindViewHolder(holder: PosterViewHolder, position: Int) =
        holder.bind(artworks[position])

    override fun getItemCount() = artworks.size
    inner class PosterViewHolder(parent: ViewGroup) :
        ViewHolder(LayoutInflater.from(context).inflate(layout.poster_grid_image, parent, false)) {
        private val img: ImageView = itemView.findViewById(id.img_poster_grid)
        private val progressBar: ProgressBar = itemView.findViewById(id.progress_poster_grid)

        fun bind(postersItem: PostersItem) = with(itemView) {
            img.setPicassoWithCache(postersItem.filePath, 6,
                img_erro = R.drawable.poster_empty,
                error = { progressBar.gone() },
                sucesso = { progressBar.gone() }
            ).setOnClickListener {
                val intent = Intent(context, PosterActivity::class.java).apply {
                    putExtra(Constant.ARTWORKS, artworks as Serializable)
                    putExtra(Constant.POSICAO, position)
                    putExtra(Constant.NAME, nome)
                }
                val opts = ActivityOptionsCompat.makeCustomAnimation(context,
                    anim.fade_in, anim.fade_out)
                ActivityCompat.startActivity(context, intent, opts.toBundle())
            }
        }
    }
}
