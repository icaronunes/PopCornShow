package adapter

import adapter.CrewAdapter.CrewViewHolder
import android.content.Intent
import android.os.Bundle
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
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.FirebaseAnalytics.Event
import com.google.firebase.analytics.FirebaseAnalytics.Param
import domain.CrewItem
import pessoa.activity.PersonActivity
import utils.Constantes
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
        ViewHolder(LayoutInflater.from(activity).inflate(layout.scroll_crews, parent, false)) {
        private val textCrewJob: TextView = itemView.findViewById(id.textCrewJob)
        private val textCrewNome: TextView = itemView.findViewById(id.textCrewNome)
        private val img: ImageView = itemView.findViewById(id.imgPagerCrews)
        private val progressBarCrew: ProgressBar = itemView.findViewById(id.progressBarCrews)

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
                    putExtra(Constantes.PERSON_ID, id)
                    putExtra(Constantes.NOME_PERSON, name)
                })
                FirebaseAnalytics.getInstance(context).logEvent(Event.SELECT_CONTENT, Bundle().apply {
                    putString(Event.SELECT_CONTENT, PersonActivity::class.java.name)
                    putInt(Param.ITEM_ID, id!!)
                    putString(Param.ITEM_NAME, name)
                })
            }
        }
    }
}