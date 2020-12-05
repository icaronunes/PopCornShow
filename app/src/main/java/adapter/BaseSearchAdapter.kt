package adapter

import android.app.Activity
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import busca.adapter.SearchPersonAdapter
import busca.adapter.SearchTvAdapter
import domain.search.SearchMulti
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

class BaseSearchAdapter(val context: Activity, private val multis: SearchMulti) : RecyclerView.Adapter<ViewHolder>() {
	private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

	init {
		delegateAdapters.put(0, LoadingDelegateAdapter())
		delegateAdapters.put(1, MovieItemAdapter())
		delegateAdapters.put(2, TvItemAdapter())
		delegateAdapters.put(3, PersonItemAdapter())
	}

	override fun onBindViewHolder(holder: ViewHolder, position: Int) {
		return delegateAdapters.get(getItemViewType(position))!!
			.onBindViewHolder(holder = holder, item = multis.results[position], context = context)
	}

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
		return delegateAdapters.get(viewType)?.onCreateViewHolder(parent)!!
	}
	override fun getItemCount() = multis.results.size
	override fun getItemViewType(position: Int) = multis.results[position].getViewType()

}