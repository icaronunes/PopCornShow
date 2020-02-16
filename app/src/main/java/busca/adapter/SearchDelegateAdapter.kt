 package busca.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import busca.SearchMultiActivity
import domain.search.SearchMulti
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

 /**
 * Created by icaro on 18/09/16.
 */
class SearchDelegateAdapter(val context: SearchMultiActivity, private val multis: SearchMulti) : RecyclerView.Adapter<ViewHolder>()  {

    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
        delegateAdapters.put(0, LoadingDelegateAdapter())
        delegateAdapters.put(1, SearchMovieAdapter())
        delegateAdapters.put(2, SearchTvAdapter())
        delegateAdapters.put(3, SearchPersonAdapter())
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
