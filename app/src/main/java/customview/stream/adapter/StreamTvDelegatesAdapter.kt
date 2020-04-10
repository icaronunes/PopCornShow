package customview.stream.adapter

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.Adapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import customview.stream.TypeEnumStream.TV
import domain.reelgood.movie.Availability

class StreamTvDelegatesAdapter(val subscription: Boolean, val purchase: Boolean = false, val titleMedia: String = ""): Adapter<ViewHolder>(){

    private var streamList: List<Availability> = mutableListOf()
    private var delegateAdapters = AdapterListStreams(subscription = subscription,
        purchase = purchase,
        titleMedia = titleMedia,
        type = TV)
        .getDeleteAdapters()

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
