package producao

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.CrewItem
import pessoa.activity.PersonActivity
import utils.Constantes
import utils.setPicassoWithCache

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
        holder.imgCrew.setPicassoWithCache(crew?.profilePath, 2, img_erro = R.drawable.person)

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context, PersonActivity::class.java).apply {
                putExtra(Constantes.PERSON_ID, crew?.id)
                putExtra(Constantes.NOME_PERSON, crew?.name)
            })
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
