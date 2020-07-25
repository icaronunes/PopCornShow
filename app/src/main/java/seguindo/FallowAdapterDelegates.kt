package seguindo

import adapter.delegateAdapters
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import customview.LoadingShimmer.PaymentLoadingsType
import domain.ViewType
import utils.Constant
import utils.listSize

/**
 * Created by icaro on 14/08/16.
 */
class FallowAdapterDelegates(private val context: Context, vararg val list: PaymentLoadingsType) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {

	private val listaResult: ArrayList<ViewType> = listSize(loading, 25)

	init {
		delegateAdapters.put(Constant.ViewTypesIds.LOADING, LoadingFallowDelegateAdapter(*list))
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

	fun add(updater: ViewType) {
		this.listaResult.add(updater)
		notifyDataSetChanged()
	}

	companion object {
		private val loading = object : ViewType {
			override fun getViewType(): Int {
				return Constant.ViewTypesIds.LOADING
			}
		}
	}
}
