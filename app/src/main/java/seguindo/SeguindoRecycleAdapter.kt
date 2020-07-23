package seguindo

import android.content.Intent
import android.view.LayoutInflater
import android.view.View.IMPORTANT_FOR_ACCESSIBILITY_YES
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import br.com.icaro.filme.R
import domain.UserTvshow
import kotlinx.android.synthetic.main.seguindo_tvshow.view.follow_poster
import kotlinx.android.synthetic.main.seguindo_tvshow.view.follow_title
import kotlinx.android.synthetic.main.seguindo_tvshow.view.follow_update
import seguindo.SeguindoRecycleAdapter.SeguindoViewHolder
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.gone
import utils.setPicassoWithCache
import utils.visible

/**
 * Created by icaro on 02/12/16.
 */
class SeguindoRecycleAdapter(private val context: FragmentActivity?) :
	RecyclerView.Adapter<SeguindoViewHolder>() {

	private var userTvshows: MutableList<UserTvshow> = mutableListOf()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = SeguindoViewHolder(parent)
	override fun onBindViewHolder(holder: SeguindoViewHolder, position: Int) =
		holder.bind(userTvshows[position])

	override fun getItemCount() = userTvshows.size
	override fun getItemViewType(position: Int) = position

	fun add(tvFire: UserTvshow) {
		userTvshows.add(tvFire)
		notifyItemInserted(userTvshows.size - 1)
	}

    inner class SeguindoViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(LayoutInflater.from(context).inflate(R.layout.seguindo_tvshow, parent, false)) {

        fun bind(item: UserTvshow) = with(itemView) {

            follow_poster.setPicassoWithCache(item.poster, 3, {

            }, {
                follow_title.apply {
                    text = item.nome
                    visible()
                }
            }, img_erro = R.drawable.poster_empty)

            itemView.contentDescription = item.nome
            itemView.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES

            if (item.desatualizada) follow_update.visible() else follow_update.gone()

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
