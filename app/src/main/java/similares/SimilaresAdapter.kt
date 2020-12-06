package similares

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import kotlinx.android.synthetic.main.adapter_similares.view.similares_date_avaliable
import kotlinx.android.synthetic.main.adapter_similares.view.similares_img
import kotlinx.android.synthetic.main.adapter_similares.view.similares_name
import kotlinx.android.synthetic.main.adapter_similares.view.similares_rated
import kotlinx.android.synthetic.main.adapter_similares.view.similares_title_original
import utils.setPicassoWithCache

/**
 * Created by icaro on 12/08/16.
 */

class SimilaresAdapter(
    private val activity: Activity,
    private val listSimilares: List<SimilaresInfo?>?,
    val callBack: (SimilaresInfo, ImageView) -> Unit
) : RecyclerView.Adapter<SimilaresAdapter.SimilareViewHolde>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SimilareViewHolde {
        val view = LayoutInflater.from(activity).inflate(R.layout.adapter_similares, parent, false)
        return SimilareViewHolde(view)
    }

    override fun onBindViewHolder(holder: SimilareViewHolde, position: Int) {
        holder.bind(listSimilares?.get(position)!!)
    }

    override fun getItemCount(): Int {
        return listSimilares?.size ?: 0
    }

    inner class SimilareViewHolde(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(item: SimilaresInfo) = with(itemView) {
            similares_name.text = item.title()
            similares_date_avaliable.text = item.firstDate()
            similares_title_original.text = item.originalTitle()
            similares_rated.text = item.votes().toString()
            similares_img.setPicassoWithCache(item.poster(), 2)

            itemView.setOnClickListener {
                callBack(item,similares_img)

            }
        }
    }
}
