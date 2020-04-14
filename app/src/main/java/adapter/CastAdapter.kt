package adapter

import adapter.CastAdapter.CastViewHolder
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import br.com.icaro.filme.R.drawable
import br.com.icaro.filme.R.id
import br.com.icaro.filme.R.layout
import domain.CastItem
import pessoa.activity.PersonActivity
import utils.Constant
import utils.gone
import utils.setPicassoWithCacheAndHolder
import utils.visible

/**
 * Created by icaro on 22/02/17.
 */
class CastAdapter(val activity: FragmentActivity, val casts: List<CastItem>) : Adapter<CastViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CastViewHolder = CastViewHolder(parent)

    override fun onBindViewHolder(holder: CastViewHolder, position: Int) {
        holder.bind(casts[position])
    }

    override fun getItemCount(): Int {
        return casts.filter {
            (!it.name.isNullOrBlank() || it.character.isNullOrEmpty())
        }.size
    }

    inner class CastViewHolder(parent: ViewGroup) :
        ViewHolder(LayoutInflater.from(activity).inflate(layout.scroll_elenco, parent, false)) {

        private val progressBar: ProgressBar = itemView.findViewById(id.progressBarCast)
        private val img: ImageView = itemView.findViewById(id.imgPager)
        private val textCastPersonagem: TextView = itemView.findViewById(id.textCastPersonagem)
        private val textCastNome: TextView = itemView.findViewById(id.textCastNomes)

        fun bind(person: CastItem) = with(itemView) {
            progressBar.visible()
            val (_, character, _, _, name, profilePath, id) = person
            textCastPersonagem.text = character
            textCastNome.text = name
            img.setPicassoWithCacheAndHolder(stillPath = profilePath,
                patten = 4,
                img_erro = drawable.person,
                error = { progressBar.gone() },
                sucesso = { progressBar.gone() },
                holder = R.drawable.person
            )
            setOnClickListener {
                context.startActivity(Intent(context, PersonActivity::class.java).apply {
                    putExtra(Constant.NOME_PERSON, name)
                    putExtra(Constant.PERSON_ID, id)
                })
            }
        }
    }
}