package favority

import Txt
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog.Builder
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Adapter
import br.com.icaro.filme.R
import br.com.icaro.filme.R.drawable
import br.com.icaro.filme.R.string
import domain.IMedia
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.include_progress.view.progress
import kotlinx.android.synthetic.main.list_your_adapter_layout.view.title
import kotlinx.android.synthetic.main.usuario_list_adapter.view.img_filme_usuario
import kotlinx.android.synthetic.main.usuario_list_adapter.view.text_rated_user
import loading.firebase.TypeDataRef
import loading.firebase.TypeMediaFireBase
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.gone
import utils.loadPallet
import utils.setPicassoWithCache
import utils.visible

class YourListAdapter(
	val fragment: Context,
	val typeDataRef: String,
	val type: String,
	val block: (Int) -> Unit
) : Adapter<YourListAdapter.FavoriteViewHolder>() {

	private var list: List<IMedia> = listOf()

	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
			YourListAdapter.FavoriteViewHolder = FavoriteViewHolder(parent)

	override fun getItemCount() = list.size
	override fun onBindViewHolder(holder: YourListAdapter.FavoriteViewHolder, position: Int) {
		holder.bind(list[position])
	}

	fun addList(list: List<IMedia>) {
		this.list = list
		notifyDataSetChanged()
	}

	inner class FavoriteViewHolder(parent: ViewGroup) :
		RecyclerView.ViewHolder(
			LayoutInflater.from(fragment)
				.inflate(R.layout.list_your_adapter_layout, parent, false)
		) {

		fun bind(media: IMedia): Unit = with(itemView) {
			img_filme_usuario.setPicassoWithCache(media.poster(), sucesso = {
				progress.gone()
				title.gone()
			}, error = {
				progress.gone()
				title.visible()
				title.text = media.name()
			})

			if (typeDataRef == TypeDataRef.RATED.type()) {
				media.rated()?.apply {
					if (this == 10f) {
						text_rated_user.text = this.toInt().toString()
					} else text_rated_user.text = this.toString()
				}
				text_rated_user.visible()
			}

			fun dialogType(txt: Int) {
				setOnLongClickListener {
					Builder(context)
						.setIcon(drawable.icon_agenda)
						.setTitle(media.name())
						.setMessage(resources.getString(txt))
						.setNegativeButton(
							string.no
						) { _, _ -> }
						.setPositiveButton(
							string.ok
						) { _, _ ->
							block(media.id())
						}.show()
					true
				}
			}

			when (type) {
				TypeMediaFireBase.TVSHOW.type() -> {
					setOnClickListener {
						fragment.startActivity(Intent(fragment, TvShowActivity::class.java).apply {
							putExtra(Constant.COLOR_TOP, img_filme_usuario.loadPallet())
							putExtra(Constant.TVSHOW_ID, media.id())
							putExtra(Constant.NOME_TVSHOW, media.name())
						})
					}
					dialogType(Txt.excluir_tvshow)
				}
				TypeMediaFireBase.MOVIE.type() -> {
					setOnClickListener {
						fragment.startActivity(
							Intent(
								fragment,
								MovieDetailsActivity::class.java
							).apply {
								putExtra(Constant.COLOR_TOP, img_filme_usuario.loadPallet())
								putExtra(Constant.FILME_ID, media.id())
								putExtra(Constant.NOME_FILME, media.name())
							})
					}
					dialogType(Txt.excluir_filme)
				}
			}
		}
	}
}