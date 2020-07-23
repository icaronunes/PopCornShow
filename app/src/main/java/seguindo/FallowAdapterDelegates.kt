package seguindo

import android.content.Context
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import domain.ViewType
import domain.tvshow.Fallow
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

/**
 * Created by icaro on 14/08/16.
 */
class FallowAdapterDelegates(private val context: Context) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val listaResult = ArrayList<ViewType>()
	private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

	init {
		delegateAdapters.put(Constant.BuscaConstants.LOADING, LoadingFallowDelegateAdapter())
		delegateAdapters.put(Constant.BuscaConstants.NEWS, ProximosAdapter())
		listaResult.addAll(arrayListOf(loading, loading, loading, loading, loading))
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
		delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		delegateAdapters.get(getItemViewType(position))
			?.onBindViewHolder(holder, listaResult[position], context)
	}

	override fun getItemViewType(position: Int) = listaResult[position].getViewType()
	override fun getItemId(position: Int) = position.toLong()
	override fun getItemCount(): Int = listaResult.size

	fun clearList() {
		listaResult.clear()
	}

	fun add(updater: Fallow) {
		this.listaResult.add(updater)
		notifyDataSetChanged()
	}

	companion object {
		private val loading = object : ViewType {
			override fun getViewType(): Int {
				return Constant.BuscaConstants.LOADING
			}
		}
	}
}
