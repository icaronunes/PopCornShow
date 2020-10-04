package elenco.adapter

import adapter.CrewAdapter
import android.content.Context
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView.*
import domain.ViewType
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

class WorksAdapter(val context: Context, val lista: List<ViewType>, horizontalLayout: Boolean = false ) : Adapter<ViewHolder>() {

	private var delegateAdapters: SparseArrayCompat<ViewTypeDelegateAdapter> =
		SparseArrayCompat<ViewTypeDelegateAdapter>().apply {
			put(Constant.ViewTypesIds.CAST, ElencoAdapter(context, horizontalLayout))
			put(Constant.ViewTypesIds.CREWS, CrewAdapter(context, horizontalLayout))
		}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		delegateAdapters.get(getItemViewType(position))
			?.onBindViewHolder(holder, item = lista[position], context = context)
	}
	override fun getItemCount() = lista.size
	override fun getItemViewType(position: Int) = lista[position].getViewType()
}