package listaserie.adapter

import android.content.Context
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import domain.ListaItemSerie
import domain.ViewType
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
        listaResult.add(loading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegateAdapters.get(viewType).onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, listaResult[position], context)
    }

    override fun getItemViewType(position: Int): Int = listaResult[position].getViewType()


    fun addSeries(listaMedia: List<ListaItemSerie?>?, totalPagina: Int) {

        val initPosition = listaResult.size - 1
        this.listaResult.removeAt(initPosition)
        notifyItemRemoved(initPosition)
        for (result in listaMedia!!) {
            this.listaResult.add(result!!)
        }
        notifyItemRangeChanged(initPosition, this.listaResult.size + 1 /* plus loading item */)
        if (listaResult.size < totalPagina)
            this.listaResult.add(loading)

    }

    override fun getItemCount(): Int = listaResult.size

    companion object {
        private val loading = object : ViewType {
            override fun getViewType(): Int = Constantes.BuscaConstants.LOADING
        }
    }

}

