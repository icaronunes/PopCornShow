package adapter

import android.content.Context
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import domain.ViewType
import domain.movie.ListaItemFilme
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

/**
 * Created by icaro on 14/08/16.
 */
class ListUserAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val listaResult = ArrayList<ViewType>()
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
        delegateAdapters.put(Constant.ViewTypesIds.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constant.ViewTypesIds.NEWS, ListasDelegateAdapter())
        listaResult.add(loading)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegateAdapters.get(getItemViewType(position))?.onBindViewHolder(holder, listaResult[position], context)
    }

    override fun getItemViewType(position: Int): Int {
        return listaResult[position].getViewType()
    }

    fun addItens(listaMedia: List<ListaItemFilme?>?, totalResults: Int) {
        if (listaMedia?.isNotEmpty()!!) {
            val initPosition = listaResult.size - 1
            this.listaResult.removeAt(initPosition)
            notifyItemRemoved(initPosition)
            for (result in listaMedia) {
                this.listaResult.add(result!!)
            }
            this.listaResult.sortedBy { (it as ListaItemFilme).releaseDate }
                .reversed()
            notifyItemRangeChanged(initPosition, this.listaResult.size + 1 /* plus loading item */)
            if (listaResult.size < totalResults)
                this.listaResult.add(loading)
        }
    }

    override fun getItemCount(): Int = listaResult.size

    companion object {

        private val loading = object : ViewType {
            override fun getViewType(): Int {
                return Constant.ViewTypesIds.LOADING
            }
        }
    }
}
