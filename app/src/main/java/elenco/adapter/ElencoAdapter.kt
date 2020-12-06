package elenco.adapter

import Layout
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import domain.CastItem
import domain.ViewType
import pessoa.activity.PersonActivity
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 24/07/16.
 */
class ElencoAdapter(private val context: Context, val horizontal: Boolean) :
	ViewTypeDelegateAdapter {
	val layout: Int =
		if (horizontal) Layout.works_layout_horizontal else Layout.works_layout_vertical

	override fun onCreateViewHolder(parent: ViewGroup): ViewHolder = ElencoViewHolder(parent)
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as ElencoViewHolder).bind(item!!)
	}

	inner class ElencoViewHolder(parent: ViewGroup) :
		RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(layout, parent, false)) {
		private val nameWork: TextView = itemView.findViewById(R.id.name_work)
		private val personWork: TextView = itemView.findViewById(R.id.person_work)
		private val imgWork: ImageView = itemView.findViewById(R.id.img_work)
		private val progressBarCrew: ProgressBar = itemView.findViewById(id.progress_work)
		fun bind(item: ViewType) {
			progressBarCrew.visible()
			val (_, character, _, _, name, profilePath, id) = item as CastItem
			personWork.text = character

			nameWork.text = name
			imgWork.setPicassoWithCache(
				profilePath,
				2,
				img_erro = R.drawable.person,
				sucesso = { progressBarCrew.gone() },
				error = { progressBarCrew.gone() })

			itemView.setOnClickListener {
				context.startActivity(Intent(context, PersonActivity::class.java).apply {
					putExtras(
						bundleOf(
							Constant.ID to id,
							Constant.NOME_PERSON to name
						)
					)
				})
			}
		}
	}
}
