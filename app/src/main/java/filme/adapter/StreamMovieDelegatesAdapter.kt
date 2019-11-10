package filme.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import domain.ViewType
import domain.reelgood.Availability
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constantes
import java.util.ArrayList

class StreamMovieDelegatesAdapter(val subscription: Boolean, val purchase: Boolean = false) : Adapter<ViewHolder>() {

    private var streamList = ArrayList<ViewType>()
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    companion object {
        const val huluPackage = "com.hulu.plus"
        const val netflixPackage = "com.netflix.mediaclient"
        const val googleVideosPackage = "com.google.android.videos"
        const val hboPackage = "com.hbo.broadband.br"
        const val starzPackage = "com.starz.starzplay.android"
        const val amazonPackage = "com.amazon.avod.thirdpartyclient"
    }

    init {
        delegateAdapters.put(Constantes.ReelGood.LOADING, LoadingDelegateAdapter())
        delegateAdapters.put(Constantes.ReelGood.NETFLIX, StreamMovieNetflixAdapter(subscription, purchase))
        delegateAdapters.put(Constantes.ReelGood.HBO, StreamMovieHboAdapterAdapter(subscription, purchase))
        delegateAdapters.put(Constantes.ReelGood.HULU, StreamMovieHuluAdapter(subscription, purchase))
        delegateAdapters.put(Constantes.ReelGood.STARZ, StreamMovieStarzAdapterAdapter(subscription, purchase))
        delegateAdapters.put(Constantes.ReelGood.GOOGLEPLAY, StreamMovieGoogleAdapterAdapter(subscription, purchase))
        delegateAdapters.put(Constantes.ReelGood.AMAZON, StreamMovieAmazonAdapterAdapter(subscription, purchase))
        delegateAdapters.put(Constantes.ReelGood.WEB, StreamMovieGenericWebAdapterAdapter(subscription, purchase))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun getItemViewType(position: Int) = streamList[position].getViewType()

    override fun getItemCount(): Int = streamList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder = holder, item = streamList[position], context = null)
    }

    fun addStream(stream: List<Availability>) {
        streamList.addAll(stream)
    }
}
