package adapter

import activity.CrewsActivity
import pessoa.activity.PersonActivity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
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
        holder.crewCharacter.text = "${crew?.department}  ${crew?.job}"

        holder.crewNome.text = crew?.name
        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + crew?.profilePath)
                .error(R.drawable.person)
                .into(holder.imgCrew)

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

         val crewNome: TextView = itemView.findViewById<View>(R.id.crew_nome) as TextView
        val crewCharacter: TextView = itemView.findViewById<View>(R.id.crew_character) as TextView
        val imgCrew: ImageView = itemView.findViewById<View>(R.id.img_crew) as ImageView

    }
}

