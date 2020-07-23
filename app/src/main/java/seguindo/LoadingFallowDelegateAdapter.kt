package seguindo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import customview.LoadingShimmer.PaymentLoadingsType.CalendarTv
import domain.ViewType
import kotlinx.android.synthetic.main.fallow_loading.view.shimmer_fallow
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

class LoadingFallowDelegateAdapter : ViewTypeDelegateAdapter {

	override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(parent)

	override fun onBindViewHolder(
		holder: RecyclerView.ViewHolder,
		item: ViewType?,
		context: Context?
	) {
		(holder as LoadingViewHolder).bind()
	}

	class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
		LayoutInflater.from(parent.context)
			.inflate(R.layout.fallow_loading, parent, false)
	) {

		fun bind() = with(itemView) {
			shimmer_fallow.createCustomLoading(CalendarTv)
		}
	}
}

