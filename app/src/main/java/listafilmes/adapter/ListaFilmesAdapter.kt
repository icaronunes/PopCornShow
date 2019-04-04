package listafilmes.adapter

import android.content.Context
import android.support.v4.util.SparseArrayCompat
import android.support.v7.widget.RecyclerView
import android.view.ViewGroup
import com.google.android.gms.ads.formats.UnifiedNativeAd
import domain.ViewType
import domain.movie.ListAd
import domain.movie.ListaItemFilme
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constantes
import java.util.*

class ListaFilmesAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
	private val listaResult = ArrayList<ViewType>()
	private val delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()
	
	init {
		delegateAdapters.put(Constantes.BuscaConstants.LOADING, LoadingDelegateAdapter())
		delegateAdapters.put(Constantes.BuscaConstants.NEWS, ListasFilmesDelegateAdapter())
		delegateAdapters.put(Constantes.BuscaConstants.AD, AdDelegateAdapter())
		//listaResult.add(loading)
		
	}
	
	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
			delegateAdapters.get(viewType).onCreateViewHolder(parent)
	
	override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
		delegateAdapters.get(getItemViewType(position)).onBindViewHolder(holder, listaResult[position], context)
	}
	
	override fun getItemViewType(position: Int): Int = listaResult[position].getViewType()
	
	
	fun addFilmes(listaMedia: List<ListaItemFilme?>?, totalPagina: Int) {
		if (listaMedia?.isNotEmpty()!!) {
			val initPosition = listaResult.size - 1
			if(!listaResult.isEmpty() && listaResult[listaResult.size - 1].getViewType() == Constantes.BuscaConstants.LOADING) {
				this.listaResult.removeAt(listaResult.size - 1)
			}
			for (result in listaMedia) {
				this.listaResult.add(result!!)
			}
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
	
	
	fun addAd(ad: UnifiedNativeAd, totalPagina: Int) {
		if(listaResult[listaResult.size - 1].getViewType() == Constantes.BuscaConstants.LOADING) {
			this.listaResult.removeAt(listaResult.size - 1)
		}
		listaResult.add(ListAd(ad))
		notifyItemInserted(listaResult.size - 1)
		if (listaResult.size < totalPagina) {
			listaResult.add(loading)
			notifyItemInserted(listaResult.size - 1)
		}
	}
	
	companion object {
		
		private val loading = object : ViewType {
			override fun getViewType(): Int = Constantes.BuscaConstants.LOADING
		}
	}
	
}

