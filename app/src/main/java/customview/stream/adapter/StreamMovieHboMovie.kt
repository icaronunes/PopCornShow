package customview.stream.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import customview.stream.BaseStreamAb
import customview.stream.TypeEnumStream
import customview.stream.TypeEnumStream.*
import domain.ViewType
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream.hboPackage

class StreamAbMovieHboAdapter(val subscription: Boolean = false,
    val purchase: Boolean = false,
    val type: TypeEnumStream) :
    BaseStreamAb(), ViewTypeDelegateAdapter {
    override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as StreamMovieHolder).bind(item as? Availability)
    }

    inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)) {
        fun bind(availability: Availability?) = with(itemView.source_item) {
            iconSource = ContextCompat.getDrawable(context, R.drawable.hbo)
            setOnClickListener {
                try {
                    callAppOrWeb(availability, typeStream) {
                        val intent = Intent(Intent.ACTION_VIEW)
                        intent.data = Uri.parse(getLink(availability))
                        context.startActivity(intent)
                    }
                } catch (ex: Exception) {
                    FirebaseCrashlytics.getInstance().log("Erro no Stream - ${availability.toString()}")
                    FirebaseCrashlytics.getInstance().recordException(ex)
                    tryWebLink(availability,  context)
                }
            }
        }
    }

    private fun tryWebLink(availability: Availability?, context: Context) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse( availability?.sourceData?.links?.web ?: "https://play.hbogo.com/")
        context.startActivity(intent)
    }

    private fun getLink(availability: Availability?): String {
        if (availability == null) return "https://play.hbogo.com/"
        if (availability.sourceData?.references == null) return "https://play.hbogo.com/"

        return when (type) {
            EP, TV -> "android-app://com.hbo.hbonow/https/play.hbonow.com/episode/urn:hbo:episode:${getSomeReference(availability, type)}"
            MOVIE -> "hbogo://deeplink/MO.MO/${getSomeReference(availability, type)}"
        }
    }

    override val typeStream: String = hboPackage
}
