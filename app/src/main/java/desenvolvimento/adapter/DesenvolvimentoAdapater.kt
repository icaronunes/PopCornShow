package desenvolvimento.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R.*
import desenvolvimento.adapter.DesenvolvimentoAdapater.*
import kotlinx.android.synthetic.main.lib_adapter_layout.view.development

/**
 * Created by icaro on 18/12/16.
 */
class DesenvolvimentoAdapater(
	private val context: Context,
	private val itens: Array<String>
) : Adapter<HolderDesenvolvimento>() {

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = HolderDesenvolvimento(parent)
	override fun onBindViewHolder(holder: HolderDesenvolvimento, position: Int) {
		holder.bind(itens[position])
	}

	override fun getItemCount(): Int {
		return itens.size
	}

	inner class HolderDesenvolvimento(parent: ViewGroup) : ViewHolder(LayoutInflater.from(context)
		.inflate(layout.lib_adapter_layout, parent, false)) {
		fun bind(item: String) = with(itemView) {
			development.text = item
		}
	}
}