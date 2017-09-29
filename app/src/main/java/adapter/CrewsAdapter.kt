package adapter

import activity.CrewsActivity
import activity.PersonActivity
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
import domain.CrewItem
import utils.Constantes
import utils.UtilsApp

/**
 * Created by icaro on 24/07/16.
 */
class CrewsAdapter(private val context: CrewsActivity, private val crews: List<CrewItem?>?) : RecyclerView.Adapter<CrewsAdapter.CrewsViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CrewsViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.crews_list_adapter, parent, false)
        return CrewsViewHolder(view)
    }


    override fun onBindViewHolder(holder: CrewsViewHolder, position: Int) {
        val crew = crews?.get(position)
        holder?.crew_character.text = crew?.department + " " + crew?.job

        holder.crew_nome.text = crew?.name
        Picasso.with(context)
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + crew?.profilePath)
                .error(R.drawable.person)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.img_crew)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, PersonActivity::class.java)
            intent.putExtra(Constantes.PERSON_ID, crew?.id)
            intent.putExtra(Constantes.NOME_PERSON, crew?.name)
            context.startActivity(intent)

        }

    }

    override fun getItemCount(): Int {
        return crews?.size ?: 0
    }

    inner class CrewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val crew_nome: TextView
         val crew_character: TextView
         val img_crew: ImageView

        init {
            crew_nome = itemView.findViewById<View>(R.id.crew_nome) as TextView
            crew_character = itemView.findViewById<View>(R.id.crew_character) as TextView
            img_crew = itemView.findViewById<View>(R.id.img_crew) as ImageView

        }
    }
}

