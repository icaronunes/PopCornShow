package filme.adapter

import android.view.ViewGroup
import androidx.collection.SparseArrayCompat
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import domain.reelgood.Availability
import pessoaspopulares.adapter.LoadingDelegateAdapter
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant

class StreamTvDelegatesAdapter(val subscription: Boolean, val purchase: Boolean = false, val titleMedia: String? = "") : Adapter<ViewHolder>() {

    private var streamList: List<Availability> = mutableListOf()
    private var delegateAdapters = SparseArrayCompat<ViewTypeDelegateAdapter>()

    init {
        delegateAdapters.run {
            put(Constant.ReelGood.LOADING, LoadingDelegateAdapter())
            put(Constant.ReelGood.NETFLIX, StreamMovieNetflixAdapter(subscription, purchase))
            put(Constant.ReelGood.HBO, StreamMovieHboAdapterAdapter(subscription, purchase))
            put(Constant.ReelGood.HULU, StreamMovieHuluAdapter(subscription, purchase))
            put(Constant.ReelGood.STARZ, StreamMovieStarzAdapterAdapter(subscription, purchase))
            put(Constant.ReelGood.GOOGLEPLAY, StreamMovieGoogleAdapterAdapter(subscription, purchase, titleMedia))
            put(Constant.ReelGood.AMAZON, StreamMovieAmazonAdapterAdapter(subscription, purchase))
            put(Constant.ReelGood.WEB, StreamMovieGenericWebAdapterAdapter(subscription, purchase, titleMedia))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return delegateAdapters.get(viewType)!!.onCreateViewHolder(parent)
    }

    override fun getItemViewType(position: Int) = streamList[position].getViewType()
    override fun getItemCount(): Int = streamList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return delegateAdapters.get(getItemViewType(position))!!.onBindViewHolder(holder = holder,
            item = streamList[position], context = null)
    }

    fun addStream(stream: List<Availability>) { streamList = stream }
}
