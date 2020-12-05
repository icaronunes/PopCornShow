package adapter

import activity.SearchHolderView
import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import domain.ViewType
import pessoaspopulares.adapter.ViewTypeDelegateAdapter

class TvItemAdapter : ViewTypeDelegateAdapter {
	override fun onCreateViewHolder(parent: ViewGroup) = SearchHolderView(parent)
	override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
		(holder as SearchHolderView).bindTv(item)
	}
}
