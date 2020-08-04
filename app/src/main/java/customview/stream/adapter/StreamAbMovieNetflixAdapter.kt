package customview.stream.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import customview.stream.BaseStreamAb
import customview.stream.TypeEnumStream
import customview.stream.TypeEnumStream.EP
import customview.stream.TypeEnumStream.MOVIE
import customview.stream.TypeEnumStream.TV
import domain.ViewType
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream

class StreamAbMovieNetflixAdapter(val subscription: Boolean = false,
    val purchase: Boolean = false,
    private val titleMedia: String,
    val type: TypeEnumStream) : BaseStreamAb(), ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = ContextCompat.getDrawable(context, R.drawable.netflix_stream)
            setOnClickListener {
                try {
                    callAppOrWeb(availability = availability, packagerCall = typeStream) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(getSomeLink(availability, getLinkGeneric(availability)))
                        context.startActivity(intent)
                    }
                } catch (ex: Exception) {
                    Crashlytics.log("Erro no Stream - ${availability.toString()}")
                }
            }
        }
    }

    private fun getLinkGeneric(availability: Availability?): String {
        if (availability == null || availability.sourceData?.references == null) return "https://www.netflix.com/search?q=${titleMedia}"

        return when (type) {
            MOVIE -> "https://www.netflix.com/title/${getSomeReference(availability, MOVIE)}"
            TV -> "https://www.netflix.com/title/${getSomeReference(availability, TV)}"
            EP -> "https://www.netflix.com/title/${getSomeReference(availability, TV)}?trackId=${getSomeReference(availability, EP)}"
        }
    }

    override val typeStream: String = TypeStream.netflixPackage
}


