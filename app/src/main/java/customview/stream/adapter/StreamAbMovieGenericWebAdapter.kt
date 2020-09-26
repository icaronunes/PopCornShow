package customview.stream.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import com.google.firebase.crashlytics.FirebaseCrashlytics
import customview.stream.BaseStreamAb
import domain.ViewType
import domain.reelgood.movie.Availability
import kotlinx.android.synthetic.main.sources_item_view.view.source_item
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import utils.getNameTypeReel

class StreamAbMovieGenericWebAdapter(
	val subscription: Boolean,
	val purchase: Boolean,
	private val title: String = ""
) : BaseStreamAb(), ViewTypeDelegateAdapter {
	override fun onCreateViewHolder(parent: ViewGroup) = StreamMovieHolder(parent)
	override val typeStream: String = ""
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as StreamMovieHolder).bind(item as? Availability)
	}

	inner class StreamMovieHolder(val parent: ViewGroup) :
		ViewHolder(
			LayoutInflater.from(parent.context).inflate(R.layout.sources_item_view, parent, false)
		) {
		fun bind(availability: Availability?) = with(itemView.source_item) {
			try {
				getImgStreamService(
					availability,
					onResource = {
						iconSource = AppCompatResources.getDrawable(context, it)
					}
				) {
					iconSource = it?.toDrawable(resources)?.current ?: AppCompatResources.getDrawable(context, R.drawable.question)
				}
			} catch (ex: Exception) {
				iconSource = ContextCompat.getDrawable(context, R.drawable.question)
			}
			if (!subscription) {
				sourceSd = if (purchase) "SD: ${availability?.purchaseCostSd
					?: "--"}" else "SD: ${availability?.rentalCostSd ?: "---"}"
				sourceHd = if (purchase) "HD: ${availability?.purchaseCostHd
					?: "--"}" else "HD: ${availability?.rentalCostHd ?: "--"}"
			}
			setOnClickListener { callWeb(availability, context) }
		}
	}

	fun callWeb(availability: Availability?, context: Context) {
		try {
			context.startActivity(
				Intent(Intent.ACTION_VIEW, Uri.parse(getLink(availability))).apply {
					addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
				}
			)
		} catch (e: Exception) {
			FirebaseCrashlytics.getInstance().log("Error no Stream - ${title.getNameTypeReel()} ${availability.toString()}")
			FirebaseCrashlytics.getInstance().recordException(e)
			context.startActivity(Intent(
				Intent.ACTION_VIEW,
				Uri.parse("https://www.google.com/search?q=$title ${availability?.sourceName ?: ""}")
			).apply {
				addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
			})
		}
	}

	private fun getLink(availability: Availability?) = getSomeLink(availability, null)
		?: getStreamWebLink(availability, title = title.getNameTypeReel())
}
