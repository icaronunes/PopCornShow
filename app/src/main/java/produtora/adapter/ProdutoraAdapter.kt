package produtora.adapter

import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import domain.ListaItemSerie
import domain.ViewType
import pessoaspopulares.ViewTypeDelegateAdapter
import pessoaspopulares.adapter.LoadingDelegateAdapter
import utils.Constantes
import java.util.*

class ProdutoraAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {


    private var ProdutoraResultsPage = ArrayList<ViewType>()
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val loadingItem = object : ViewType {
        override fun getViewType() = Constantes.BuscaConstants.LOADING
    }

    init {
        delegateAdapters.put(Constantes.BuscaConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constantes.BuscaConstants.NEWS, ProdutoraMovieAdapter())
        ProdutoraResultsPage = ArrayList()
        ProdutoraResultsPage.add(loadingItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
       return delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder!!, ProdutoraResultsPage[position], context = null)
    }

    fun addprodutoraMovie(personResults: List<ListaItemSerie?>?) {

        val initPosition = ProdutoraResultsPage?.size!! - 1
        this.ProdutoraResultsPage?.removeAt(initPosition)
        notifyItemRemoved(initPosition)

        // insert news and the loading at the end of the list
        for (person in personResults!!) {
            this.ProdutoraResultsPage.add(person!!)
        }
        //games?.add(loadingItem)
        notifyItemRangeChanged(initPosition, this.ProdutoraResultsPage?.size!! + 1 /* plus loading item */)
        ProdutoraResultsPage.add(loadingItem)

    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType).onCreateViewHolder(parent)
    }

    override fun getItemViewType(position: Int): Int = ProdutoraResultsPage[position].getViewType()

    override fun getItemCount(): Int = ProdutoraResultsPage.size


}

