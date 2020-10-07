package lista.movie.adapter

import adapter.AdDelegateAdapter
import android.content.Context
import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ViewType
import domain.movie.ListAd
import domain.movie.ListaItemFilme
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

class ListaFilmesAdapter(private val context: Context) :
	RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val listaResult = arrayListOf<ViewType>()
    private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
	    delegateAdapters.put(Constant.ViewTypesIds.LOADING, LoadingDelegateAdapter())
	    delegateAdapters.put(Constant.ViewTypesIds.NEWS, ListasFilmesDelegateAdapter())
	    delegateAdapters.put(Constant.ViewTypesIds.AD, AdDelegateAdapter())
    }

	init {
		listaResult.add(loading)
	}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
            delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		delegateAdapters.get(getItemViewType(position))
			?.onBindViewHolder(holder, listaResult[position]!!, context)
    }

	override fun getItemViewType(position: Int): Int =
		listaResult[position].getViewType() ?: Constant.ViewTypesIds.LOADING

	fun addItems(listaMedia: List<ViewType?>?, totalPagina: Int) {
		if (listaMedia?.isNotEmpty() == true) {
            val initPosition = listaResult.size - 1
			if (listaResult.last().getViewType() == Constant.ViewTypesIds.LOADING) {
		        this.listaResult.removeAt(listaResult.size - 1)
	        }

			if (listaResult.isNotEmpty() && listaResult.last().getViewType() == Constant.ViewTypesIds.AD &&
				listaMedia.first()?.getViewType() == Constant.ViewTypesIds.AD
			) return

            this.listaResult.addAll(listaMedia as List<ViewType>)
            this.listaResult.sortedBy {
                if (it is ListaItemFilme) it.releaseDate
                true
            }.reversed()

            notifyItemRangeChanged(initPosition, this.listaResult.size - 1 /* plus loading item */)
            if (listaResult.size < totalPagina) {
                listaResult.add(loading)
                notifyItemInserted(listaResult.size - 1)
            }
        }
    }

    override fun getItemCount(): Int = listaResult.size

    companion object {
        private val loading = object : ViewType {
	        override fun getViewType(): Int = Constant.ViewTypesIds.LOADING
        }
    }
}
