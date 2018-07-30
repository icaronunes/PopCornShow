package activity

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import br.com.icaro.filme.R
import com.squareup.picasso.Picasso
import domain.busca.MultiSearch
import filme.activity.FilmeActivity
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp
import utils.enums.EnumTypeMedia

 class MultiAdapter(val application: Context, val multiRetorno: MultiSearch, val icon: Drawable?) : RecyclerView.Adapter<MultiAdapter.HolderView>() {



	 override fun onBindViewHolder(holder: HolderView, position: Int) {
		val item = multiRetorno.results?.get(position)!!

		when (item.mediaType) {
			EnumTypeMedia.MOVIE.type -> {
				Picasso.get()
						.load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(application, 1))!! + item.posterPath)
						.into(holder.poster)

				holder.itemView?.setOnClickListener { view: View ->
					val intent = Intent(application, FilmeActivity::class.java)
					intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
					intent.putExtra(Constantes.FILME_ID, item.id)
					intent.putExtra(Constantes.NOME_FILME, item.title)
					application.startActivity(intent)
					icon?.alpha = 255
				}

				holder.search_title_original.text = item.originalTitle

				holder.search_nome.text = item.title

				holder.search_data_lancamento.text = if (item.releaseDate != null && item.releaseDate.length >= 4) item.releaseDate.substring(0, 4) else application.getString(R.string.empty_data)
				return
			}

			EnumTypeMedia.TV.type -> {
				Picasso.get()
						.load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(application, 1))!! + item.posterPath)
						.into(holder.poster)

				holder.itemView?.setOnClickListener { view: View ->
					val intent = Intent(application, TvShowActivity::class.java)
					intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
					intent.putExtra(Constantes.TVSHOW_ID, item.id)
					intent.putExtra(Constantes.NOME_TVSHOW, item.name)
					application.startActivity(intent)
					icon?.alpha = 255
				}

				if (item.originalTitle.isNullOrEmpty()) holder.search_title_original.visibility = View.GONE else holder.search_title_original.text = item.originalName

				holder.search_nome.text = item.name

				holder.search_data_lancamento.text = if (item.firstAirDate != null && item.firstAirDate.length >= 4) item.firstAirDate.substring(0, 4) else application.getString(R.string.empty_data)
				return
			}

			EnumTypeMedia.PERSON.type -> {

				Picasso.get()
						.load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(application, 1))!! + item.profile_path)
						.into(holder.poster)

				holder.itemView.setOnClickListener { view: View ->
					val intent = Intent(application, PersonActivity::class.java)
					intent.putExtra(Constantes.COLOR_TOP, UtilsApp.loadPalette(holder.poster))
					intent.putExtra(Constantes.PERSON_ID, item.id)
					intent.putExtra(Constantes.NOME_PERSON, item.name)
					application.startActivity(intent)
					icon?.alpha = 255
				}
				holder.search_title_original.visibility = View.GONE

				holder.search_nome.text = item.name

				holder.search_data_lancamento.visibility = View.GONE
				return
			}
		}

	}


	override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiAdapter.HolderView {
		val view = LayoutInflater.from(application).inflate(R.layout.search_list_multi_adapter, parent, false)
		return HolderView(view)
	}

	override fun getItemCount(): Int = multiRetorno.results?.size!!


	inner class HolderView(itemView: View?) : RecyclerView.ViewHolder(itemView) {

		var poster: ImageView
		var search_nome: TextView
		var search_data_lancamento: TextView
		var search_title_original: TextView

		init {

			poster = itemView?.findViewById<View>(R.id.img_muitl_search) as ImageView
			search_nome = itemView.findViewById<View>(R.id.search_muitl_nome) as TextView
			search_title_original = itemView.findViewById<View>(R.id.search_muitl_title_original) as TextView
			search_data_lancamento = itemView.findViewById<View>(R.id.search_muitl_data_lancamento) as TextView

		}

	}
}
