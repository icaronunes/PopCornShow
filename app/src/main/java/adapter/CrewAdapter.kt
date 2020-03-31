package adapter

import adapter.CrewAdapter.CrewViewHolder
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R.drawable
import br.com.icaro.filme.R.id
import br.com.icaro.filme.R.layout
import domain.CrewItem
import pessoa.activity.PersonActivity
import utils.Constant
import utils.gone
import utils.setPicassoWithCacheAndHolder
import utils.visible

/**
 * Created by icaro on 22/02/17.
 */
class CrewAdapter(val activity: FragmentActivity, val crews: List<CrewItem>) : Adapter<CrewViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CrewViewHolder(parent)
    override fun onBindViewHolder(holder: CrewViewHolder, position: Int) {
        holder.bind(crews[position])
    }

    override fun getItemCount(): Int {
        return crews.filter {
            (!it.name.isNullOrBlank() || it.job.isNullOrEmpty())
        }.size
    }

    inner class CrewViewHolder(parent: ViewGroup) :
        ViewHolder(LayoutInflater.from(activity).inflate(layout.scroll_elenco, parent, false)) {
        private val textCrewJob: TextView = itemView.findViewById(id.textCastPersonagem)
        private val textCrewNome: TextView = itemView.findViewById(id.textCastNomes)
        private val img: ImageView = itemView.findViewById(id.imgPager)
        private val progressBarCrew: ProgressBar = itemView.findViewById(id.progressBarCast)

        fun bind(crewItem: CrewItem) = with(itemView) {
            progressBarCrew.visible()
            val (_, _, name, profilePath, id, _, job) = crewItem
            textCrewJob.text = job
            textCrewNome.text = name
            img.setPicassoWithCacheAndHolder(stillPath = profilePath,
                patten = 4,
                holder = drawable.person,
                sucesso = { progressBarCrew.gone() },
                error = { progressBarCrew.gone() })


            setOnClickListener {
                context.startActivity(Intent(context, PersonActivity::class.java).apply {
                    putExtra(Constant.PERSON_ID, id)
                    putExtra(Constant.NOME_PERSON, name)
                })
            }
        }
    }
}