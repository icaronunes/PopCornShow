package adapter

import pessoa.activity.PersonActivity
import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import domain.CastItem
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 24/07/16.
 */
class ElencoAdapter(private val context: Context, private val casts: List<CastItem?>?) : RecyclerView.Adapter<ElencoAdapter.ElencoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ElencoAdapter.ElencoViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.elenco_list_adapter, parent, false)
        return ElencoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ElencoAdapter.ElencoViewHolder, position: Int) {
        val (_, character, _, _, name, profilePath, id) = casts?.get(position)!!
        holder.elenco_character.text = character

        holder.elenco_nome.text = name
        Picasso.with(context).load(UtilsApp
                .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + profilePath)
                .placeholder(R.drawable.person)
                .into(holder.img_elenco)


        holder.itemView.setOnClickListener {
            val intent = Intent(context, PersonActivity::class.java)
            intent.putExtra(Constantes.PERSON_ID, id)
            intent.putExtra(Constantes.NOME_PERSON, name)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return casts?.size ?: 0
    }

    inner class ElencoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val elenco_nome: TextView
        val elenco_character: TextView
        val img_elenco: ImageView

        init {
            elenco_nome = itemView.findViewById<View>(R.id.elenco_nome) as TextView
            elenco_character = itemView.findViewById<View>(R.id.elenco_character) as TextView
            img_elenco = itemView.findViewById<View>(R.id.img_elenco) as ImageView
        }
    }
}
