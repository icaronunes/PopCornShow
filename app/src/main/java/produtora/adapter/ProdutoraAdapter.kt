package produtora.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import domain.ViewType
import domain.movie.ListaItemFilme
import java.util.ArrayList
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constantes

class ProdutoraAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var produtoraResultsPage = ArrayList<ViewType>()
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    private val loadingItem = object : ViewType {
        override fun getViewType() = Constantes.BuscaConstants.LOADING
    }

    init {
        delegateAdapters.put(Constantes.BuscaConstants.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constantes.BuscaConstants.NEWS, ProdutoraMovieAdapter())
        produtoraResultsPage = ArrayList()
        produtoraResultsPage.add(loadingItem)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        return delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder, produtoraResultsPage[position], context = null)
    }

    fun addprodutoraMovie(list: List<ListaItemFilme?>?, totalResult: Int) {
        if (list?.isNotEmpty()!!) {
            val initPosition = produtoraResultsPage.size - 1
            this.produtoraResultsPage.removeAt(initPosition)
            notifyItemRemoved(initPosition)

            for (item in list) {
                this.produtoraResultsPage.add(item!!)
            }

            notifyItemRangeChanged(initPosition, this.produtoraResultsPage.size + 1 /* plus loading item */)
            if (totalResult < produtoraResultsPage.size) produtoraResultsPage.add(loadingItem)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun getItemViewType(position: Int): Int = produtoraResultsPage[position].getViewType()

    override fun getItemCount(): Int = produtoraResultsPage.size
}
