package episodio

import Color
import Layout
import Txt
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import applicaton.BaseFragment
import br.com.icaro.filme.R
import domain.CrewItem
import domain.EpisodesItem
import domain.FilmeService
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import domain.createUserEp
import domain.fillEpUserTvshow
import kotlinx.android.synthetic.main.epsodio_fragment.air_date
import kotlinx.android.synthetic.main.epsodio_fragment.ep_director
import kotlinx.android.synthetic.main.epsodio_fragment.ep_image
import kotlinx.android.synthetic.main.epsodio_fragment.ep_rating
import kotlinx.android.synthetic.main.epsodio_fragment.ep_rating_button
import kotlinx.android.synthetic.main.epsodio_fragment.ep_sinopse
import kotlinx.android.synthetic.main.epsodio_fragment.ep_title
import kotlinx.android.synthetic.main.epsodio_fragment.ep_votos
import kotlinx.android.synthetic.main.epsodio_fragment.ep_write
import kotlinx.android.synthetic.main.epsodio_fragment.wrapper_rating_ep
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pessoa.activity.PersonActivity
import utils.Constant
import utils.kotterknife.bindArgument
import utils.notNullOrEmpty
import utils.parseDateShot
import utils.released
import utils.replaceItemList
import utils.setPicasso

/**
 * Created by icaro on 27/08/16.
 */
class EpsodioFragment(override val layout: Int = Layout.epsodio_fragment) : BaseFragment() {
	private val model: EpsodioViewModel by lazy { createViewModel(EpsodioViewModel::class.java) }
	private val tvshowId: Int by bindArgument(Constant.TVSHOW_ID)
	private val temporadaPosition: Int by bindArgument(Constant.TEMPORADA_POSITION)
	private val episode: EpisodesItem by bindArgument(Constant.EPSODIO)
	private val color: Int by bindArgument(Constant.COLOR_TOP, Color.primary)
	private val seguindo: Boolean by bindArgument(Constant.SEGUINDO)
	private val tvSeason: TvSeasons by bindArgument(Constant.TVSEASONS)
	private var seasons: UserSeasons? = null
	private var userEp: UserEp? = null
	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	) = inflater.inflate(layout, container, false)

	override fun onActivityCreated(savedInstanceState: Bundle?) {
		super.onActivityCreated(savedInstanceState)
		setAirDate()
		setVote()
		setImage()
		setSinopse()
		setName()
		setDirect()
		setWriter()
		setColorButton()
		observers()
		model.getSeasonFire(tvshowId, temporadaPosition)
	}

	private fun observers() {
		model.seasons.observe(viewLifecycleOwner, Observer {
			seasons = it.getValue(UserSeasons::class.java)
			userEp = seasons?.userEps?.firstOrNull { userEp ->
				userEp.id == episode.id
			}
			try {
				if (userEp?.isAssistido == true) {
					ep_rating_button.text = resources.getText(R.string.classificar_visto)
					userEp?.nota?.let {
						ep_rating.rating = it * 2
					}
				} else {
					ep_rating_button.text = resources.getText(R.string.classificar)
					userEp?.nota?.let {
						ep_rating.rating = it * 2
					}
				}
			} catch (e: NoSuchMethodError) {
				Toast.makeText(context, R.string.ops, Toast.LENGTH_SHORT).show()
			}
		})
	}

	private fun setDirect() {
		episode.crew?.firstOrNull { it?.department?.equals("Directing")!! }?.let { director ->
			ep_director.text = director.name
			ep_director.setOnClickListener {
				nextPersonActivity(director)
			}
		}
	}

	private fun setWriter() {
		episode.crew?.firstOrNull { it?.department?.equals("Writing")!! }?.let { written ->
			ep_write.text = written.name
			ep_write.setOnClickListener {
				nextPersonActivity(written)
			}
		}
	}

	private fun nextPersonActivity(it: CrewItem) {
		startActivity(Intent(context, PersonActivity::class.java).apply {
			putExtra(Constant.NOME_PERSON, it.name)
			putExtra(Constant.PERSON_ID, it.id)
		})
	}

	private fun setButtonRating(available: String?) {
		if (available?.released() == true && seguindo) {
			ep_rating_button.setOnClickListener {
				dialogSetRating()
			}

			wrapper_rating_ep.setOnClickListener {
				dialogSetRating()
			}
		} else {
			ep_rating_button.visibility = View.GONE
		}
	}

	private fun dialogSetRating() {
		val alertDialog = Dialog(requireContext())
		alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
		alertDialog.setContentView(R.layout.dialog_custom_rated)
		val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
		val naoVisto = alertDialog.findViewById<View>(R.id.cancel_rated) as Button

		if (userEp != null) {
			if (!userEp!!.isAssistido) {
				naoVisto.visibility = View.INVISIBLE
			}
		} else {
			naoVisto.visibility = View.INVISIBLE
		}

		episode.name?.let { name ->
			alertDialog.findViewById<TextView>(R.id.rating_title).text = name
		}
		val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
		ratingBar.rating = userEp?.nota ?: 0.0f
		val width = resources.getDimensionPixelSize(R.dimen.popup_width)
		val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)

		alertDialog.window?.setLayout(width, height)
		alertDialog.show()

		naoVisto.setOnClickListener {
			val epRated = episode.createUserEp().apply {
				nota = 0.0f
				this.isAssistido = false
			}
			val eps = tvSeason
				.fillEpUserTvshow(seasons, false, id).userEps
				.replaceItemList(epRated) { it.id == epRated.id }
			val childUpdates = java.util.HashMap<String, Any>().apply {
				this["$tvshowId/seasons/$temporadaPosition/visto"] = false
				this["$tvshowId/seasons/$temporadaPosition/userEps"] = eps
			}

			model.watchEp(childUpdates)
			setRatedTvShowGuest(ratingBar)
			alertDialog.dismiss()
		}

		ok.setOnClickListener {
			if (seguindo) {
				val epRated = episode.createUserEp().apply {
					nota = ratingBar.rating
					this.isAssistido = true
				}
				val eps = tvSeason
					.fillEpUserTvshow(seasons, true, id).userEps
					.replaceItemList(epRated) { it.id == epRated.id }
				val childUpdates = java.util.HashMap<String, Any>().apply {
					this["$tvshowId/seasons/$temporadaPosition/visto"] = isWatchAll(eps)
					this["$tvshowId/seasons/$temporadaPosition/userEps"] = eps
				}
				model.watchEp(childUpdates)
				setRatedTvShowGuest(ratingBar)
				alertDialog.dismiss()
			}
		}
	}

	private fun isWatchAll(eps: List<UserEp>): Boolean {
		fun containsEpNotWatch() = eps.find { !it.isAssistido }
		return containsEpNotWatch() == null
	}

	private fun setRatedTvShowGuest(ratingBar: RatingBar) {
		val job = GlobalScope.launch(Dispatchers.IO) {
			try {
				FilmeService.ratedTvshowEpsodioGuest(
					tvshowId, seasons?.seasonNumber!!,
					episode.episodeNumber, ratingBar.rating.toInt(), context
				)
			} catch (ex: Exception) {
			}
		}
		job.cancel()
	}

	private fun setColorButton() {
		ep_rating_button.setTextColor(color)
	}

	private fun setSinopse() {
		episode.overview.let {
			ep_sinopse.text = it
		}
	}

	private fun setImage() {
		if (!episode.stillPath.isNullOrBlank()) {
			ep_image.setPicasso(episode.stillPath, 5, img_erro = R.drawable.top_empty)
		} else {
			ep_image.setPicasso("", img_erro = R.drawable.top_empty)
		}
	}

	private fun setVote() {
		episode.voteAverage.let { vote ->
			episode.voteCount.let { count ->
				ep_votos?.text = "$vote/$count"
				ep_votos.visibility = View.VISIBLE
			}
		}
	}

	private fun setAirDate() {
		episode.airDate?.let {
			air_date.text = it.parseDateShot()
			setButtonRating(it)
		}
	}

	private fun setName() {
		ep_title?.text =
			if (episode.name?.notNullOrEmpty() == true) episode.name else requireContext().getString(
				Txt.sem_nome
			)
	}

	companion object {
		fun newInstance(
			tvEpisode: EpisodesItem,
			tvshow_id: Int,
			color: Int,
			seguindo: Boolean,
			position: Int,
			temporada_position: Int,
			tvSeason: TvSeasons
		): Fragment {
			val fragment = EpsodioFragment()
			fragment.arguments = bundleOf(
				Constant.EPSODIO to tvEpisode,
				Constant.TVSHOW_ID to tvshow_id,
				Constant.COLOR_TOP to color,
				Constant.SEGUINDO to seguindo,
				Constant.POSICAO to position,
				Constant.TEMPORADA_POSITION to temporada_position,
				Constant.TVSEASONS to tvSeason
			)
			return fragment
		}
	}
}
