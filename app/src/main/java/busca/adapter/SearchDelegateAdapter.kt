package busca.adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import domain.ViewType
import domain.search.Result
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import java.util.ArrayList

/**
 * Created by icaro on 18/09/16.
 */
class SearchDelegateAdapter(val context: Activity) : RecyclerView.Adapter<ViewHolder>() {
	private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
	private val results: ArrayList<ViewType> = arrayListOf()

	init {
		delegateAdapters.put(0, LoadingDelegateAdapter())
		delegateAdapters.put(1, SearchMovieAdapter())
		delegateAdapters.put(2, SearchTvAdapter())
		delegateAdapters.put(3, SearchPersonAdapter())
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		return delegateAdapters.get(getItemViewType(position))!!
			.onBindViewHolder(holder = holder, item = results[position], context = context)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return delegateAdapters.get(viewType)?.onCreateViewHolder(parent)!!
	}

	override fun getItemCount() = results.size
	override fun getItemViewType(position: Int) = results[position].getViewType()

	fun addItens(newList: List<Result>) {
		for (item in newList) {
			results.add(item)
		}
		notifyDataSetChanged()
	}
}
