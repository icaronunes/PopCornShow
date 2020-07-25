package seguindo

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import customview.LoadingShimmer.PaymentLoadingsType
import domain.ViewType
import kotlinx.android.synthetic.main.fallow_loading.view.shimmer_fallow
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

class LoadingFallowDelegateAdapter(vararg val list: PaymentLoadingsType) : ViewTypeDelegateAdapter {

	override fun onCreateViewHolder(parent: ViewGroup) = LoadingViewHolder(parent)

	override fun onBindViewHolder(
		holder: RecyclerView.ViewHolder,
		item: ViewType?,
		context: Context?
	) {
		(holder as LoadingViewHolder).bind(*list)
	}

	class LoadingViewHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
		LayoutInflater.from(parent.context)
			.inflate(R.layout.fallow_loading, parent, false)
	) {

		fun bind(vararg list: PaymentLoadingsType) = with(itemView) {
			shimmer_fallow.createCustomLoading(*list)
		}
	}
}

