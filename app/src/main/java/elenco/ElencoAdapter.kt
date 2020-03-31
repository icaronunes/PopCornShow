package elenco

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.CastItem
import pessoa.activity.PersonActivity
import utils.Constant
import utils.setPicassoWithCache

/**
 * Created by icaro on 24/07/16.
 */
class ElencoAdapter(private val context: Context, private val casts: List<CastItem?>?) : RecyclerView.Adapter<ElencoAdapter.ElencoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElencoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.elenco_list_adapter, parent, false)
        return ElencoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ElencoViewHolder, position: Int) {
        val (_, character, _, _, name, profilePath, id) = casts?.get(position)!!
        holder.elencoCharacter.text = character

        holder.elencoNome.text = name

        holder.imgElenco.setPicassoWithCache(profilePath, 2, img_erro = R.drawable.person)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, PersonActivity::class.java).apply {
                putExtra(Constant.PERSON_ID, id)
                putExtra(Constant.NOME_PERSON, name)
            })
        }
    }

    override fun getItemCount(): Int {
        return casts?.size ?: 0
    }

    inner class ElencoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val elencoNome: TextView = itemView.findViewById(R.id.elenco_nome)
        val elencoCharacter: TextView = itemView.findViewById(R.id.elenco_character)
        val imgElenco: ImageView = itemView.findViewById(R.id.img_elenco)
    }
}
