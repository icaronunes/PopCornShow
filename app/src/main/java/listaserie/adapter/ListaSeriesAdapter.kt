package listaserie.adapter

import android.content.Context
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ListaItemSerie
import domain.ViewType
import domain.movie.ListAd
import listafilmes.adapter.AdDelegateAdapter
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constantes
import java.util.*

class ListaSeriesAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listaResult = ArrayList<ViewType>()
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
        delegateAdapters.put(Constantes.BuscaConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constantes.BuscaConstants.NEWS, ListasSeriesDelegateAdapter())
        delegateAdapters.put(Constantes.BuscaConstants.AD, AdDelegateAdapter())
        listaResult.add(loading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder, listaResult[position], context)
    }

    override fun getItemViewType(position: Int): Int = listaResult[position].getViewType()

    fun addSeries(listaMedia: List<ListaItemSerie?>?) {
        //TODO fix metodo
        if (itemCount != 0 && listaResult[itemCount - 1].getViewType() == Constantes.BuscaConstants.LOADING) {
            this.listaResult.removeAt(itemCount - 1)
            notifyItemRemoved(itemCount)
        }

        this.listaResult.addAll(listaMedia as List<ViewType>)
    }

    fun addAd(ad: UnifiedNativeAd) {
        if (itemCount != 0 && listaResult[itemCount - 1].getViewType() == Constantes.BuscaConstants.LOADING) {
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
            override fun getViewType(): Int = Constantes.BuscaConstants.LOADING
        }
    }

}

