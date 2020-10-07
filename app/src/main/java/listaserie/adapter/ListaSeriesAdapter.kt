package listaserie.adapter

import adapter.AdDelegateAdapter
import android.content.Context
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ListaItemSerie
import domain.ViewType
import domain.movie.ListAd
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant
import java.util.ArrayList

class ListaSeriesAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listaResult = ArrayList<ViewType>()
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
	    delegateAdapters.put(Constant.ViewTypesIds.LOADING, LoadingDelegateAdapter())
	    delegateAdapters.put(Constant.ViewTypesIds.NEWS, ListasSeriesDelegateAdapter())
	    delegateAdapters.put(Constant.ViewTypesIds.AD, AdDelegateAdapter())
	    listaResult.add(loading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder, listaResult[position], context)
    }

    override fun getItemViewType(position: Int): Int = listaResult[position].getViewType()

    fun addItems(listaMedia: List<ViewType?>?) {
        // TODO fix metodo
	    if (itemCount != 0 && listaResult[itemCount - 1].getViewType() == Constant.ViewTypesIds.LOADING) {
		    this.listaResult.removeAt(itemCount - 1)
		    notifyItemRemoved(itemCount)
	    }
        val before = listaResult.size
        this.listaResult.addAll(listaMedia as List<ViewType>)
        notifyItemRangeInserted(before, this.listaResult.size - 1 /* plus loading item */)
    }

    fun addAd(ad: UnifiedNativeAd) {
	    if (itemCount != 0 && listaResult[itemCount - 1].getViewType() == Constant.ViewTypesIds.LOADING) {
		    this.listaResult.removeAt(itemCount - 1)
		    notifyItemRemoved(itemCount)
	    }

        val before = itemCount
        listaResult.add(ListAd(ad))
        notifyItemRangeInserted(before, itemCount)
    }

    fun addLoading(totalResults: Int?) {
        if (itemCount < totalResults!!) {
            listaResult.add(loading)
            notifyItemInserted(itemCount)
        }
    }

    override fun getItemCount(): Int = listaResult.size

    companion object {
        private val loading = object : ViewType {
	        override fun getViewType(): Int = Constant.ViewTypesIds.LOADING
        }
    }
}
