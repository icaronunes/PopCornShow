package adapter

import Layout
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R.*
import domain.CrewItem
import domain.ViewType
import pessoa.activity.PersonActivity
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant
import utils.gone
import utils.setPicassoWithCacheAndHolder
import utils.visible

/**
 * Created by icaro on 22/02/17.
 */
class CrewAdapter(val context: Context, horizontal: Boolean) : ViewTypeDelegateAdapter {
	val layout: Int =
		if (horizontal) Layout.works_layout_horizontal else Layout.works_layout_vertical

	override fun onCreateViewHolder(parent: ViewGroup) = CrewViewHolder(parent)
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as CrewViewHolder).bind(item!!)
	}

	inner class CrewViewHolder(parent: ViewGroup) :
		ViewHolder(LayoutInflater.from(context).inflate(layout, parent, false)) {
		private val textCrewJob: TextView = itemView.findViewById(id.person_work)
		private val textCrewNome: TextView = itemView.findViewById(id.name_work)
		private val img: ImageView = itemView.findViewById(id.img_work)
		private val progressBarCrew: ProgressBar = itemView.findViewById(id.progress_work)
		fun bind(crewItem: ViewType) = with(itemView) {
			progressBarCrew.visible()
			val (_, _, name, profilePath, id, _, job) = crewItem as CrewItem
			textCrewJob.text = job
			textCrewNome.text = name
			img.setPicassoWithCacheAndHolder(stillPath = profilePath,
				patten = 2,
				img_erro = drawable.person,
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