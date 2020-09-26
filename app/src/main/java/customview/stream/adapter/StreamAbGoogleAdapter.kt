package customview.stream.adapter

import Txt
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import customview.stream.BaseStreamAb
import domain.ViewType
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.Constant.TypeStream.googleVideosPackage

class StreamAbGoogleAdapter(
	val subscription: Boolean = false,
	val purchase: Boolean = false,
	private val titleMovie: String? = ""
) : BaseStreamAb(), ViewTypeDelegateAdapter {
	override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as StreamMovieHolder).bind(item as? Availability)
	}

	inner class StreamMovieHolder(parent: ViewGroup) : ViewHolder(
		LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)
	) {
		fun bind(availability: Availability?) = with(itemView.source_item) {
			iconSource = ContextCompat.getDrawable(context, R.drawable.google)
			if (!subscription) {
				sourceSd = if (purchase) "SD: ${availability?.purchaseCostSd
					?: "--"}" else "SD: ${availability?.rentalCostSd ?: "--"}"
				sourceHd = if (purchase) "HD: ${availability?.purchaseCostHd
					?: "--"}" else "HD: ${availability?.rentalCostHd ?: "--"}"
			}
			setOnClickListener {
				try {
					callAppOrWeb(availability, typeStream) {
						val intent = Intent(Intent.ACTION_VIEW)
						val link = getLink(availability)
						intent.data = Uri.parse(link)
						context.startActivity(intent)
					}
				} catch (ex: Exception) {
					Toast.makeText(context, context.getString(Txt.ops), Toast.LENGTH_SHORT).show()
					FirebaseCrashlytics.getInstance().recordException(ex)
				}
			}
		}
	}

	private fun getLink(availability: Availability?): String {
		if (availability == null) return "https://play.google.com/store/search?q=$titleMovie"
		val id = availability.sourceData?.references?.web?.movieId
			?: availability.sourceData?.references?.android?.movieId
			?: availability.sourceData?.references?.ios?.movieId
		return if (id != null) {
			"https://play.google.com/store/movies/details/?id=$id"
		} else "https://play.google.com/store/search?q=$titleMovie&c=movies"
	}

	override val typeStream: String = googleVideosPackage
}