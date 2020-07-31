package seguindo

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.IMPORTANT_FOR_ACCESSIBILITY_YES
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import br.com.icaro.filme.R
import domain.UserTvshow
import domain.ViewType
import kotlinx.android.synthetic.main.seguindo_tvshow.view.follow_poster
import kotlinx.android.synthetic.main.seguindo_tvshow.view.follow_title
import pessoaspopulares.adapter.ViewTypeDelegateAdapter
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 02/12/16.
 */
class FallowAllRecycleAdapter : ViewTypeDelegateAdapter {

    override fun onBindViewHolder(holder: ViewHolder, item: ViewType?, context: Context?) {
        (holder as SeguindoViewHolder).bind(item as UserTvshow)
    }

    override fun onCreateViewHolder(parent: ViewGroup) = SeguindoViewHolder(parent)

    inner class SeguindoViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.seguindo_tvshow, parent, false)
        ) {

        fun bind(item: UserTvshow) = with(itemView) {
            follow_poster.setPicassoWithCache(item.poster, 3, {
            }, {
                follow_title.apply {
                    text = item.nome
                    visible()
                }
            })

            itemView.contentDescription = item.nome
            itemView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES

            itemView.setOnClickListener {
                context?.startActivity(Intent(context, TvShowActivity::class.java).apply {
                    putExtra(Constant.TVSHOW_ID, item.id)
                    putExtra(Constant.NOME_TVSHOW, item.nome)
                    putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(follow_poster))
                })
            }
        }
    }
}
