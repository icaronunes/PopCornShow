package temporada

import Color
import Layout
import activity.BaseActivity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import domain.EpisodesItem
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import domain.createUserEp
import domain.fillEpUserTvshow
import episodio.EpsodioActivity
import ifValid
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import kotlinx.android.synthetic.main.temporada_layout.recycleView_temporada
import temporada.adapter.SeasonAdapter
import tvshow.viewmodel.TvShowViewModel
import utils.Constant
import utils.UtilsApp
import utils.gone
import utils.kotterknife.bindBundle
import utils.makeToast
import utils.patternRecyler
import utils.released
import utils.replaceItemList
import utils.visible
import java.util.HashMap

/**
 * Created by icaro on 26/08/16.
 */
class SeasonActivity(override var layout: Int = Layout.temporada_layout) : BaseActivity() {

	private val seasonId: Int by bindBundle(Constant.TEMPORADA_ID)
	private val seasonPosition: Int by bindBundle(Constant.TEMPORADA_POSITION)
	private val tvShowId: Int by bindBundle(Constant.ID)
	private val titleSeason: String by bindBundle(Constant.NAME)
	private val color: Int by bindBundle(Constant.COLOR_TOP, Color.primary)
	private var fallow: Boolean = false
	private var seasons: UserSeasons? = null
	private lateinit var tvSeason: TvSeasons
	private val model: TvShowViewModel by lazy {
		createViewModel(TvShowViewModel::class.java, this)
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		handleTitle()
		initAdapter()

		if (UtilsApp.isNetWorkAvailable(this)) {
			model.hasfallow(tvShowId)
			model.getSeason(tvShowId, seasonId)
		} else {
			snack(recycleView_temporada) {
				if (UtilsApp.isNetWorkAvailable(baseContext)) {
					model.getSeason(tvShowId, seasonId)
				} else {
					snack(recycleView_temporada)
				}
			}
		}
	}

	private fun initAdapter() {
		recycleView_temporada.patternRecyler(false).apply {
			setHasFixedSize(true)
			adapter = SeasonAdapter(fallow, { position: Int ->
				callIntent(position)
			},
				{ status: Boolean, id: Int ->
					watchSeason(status, id)
				})
			{ ep: EpisodesItem, position: Int, userEp: UserEp? ->
				onClickSeasonReated(ep, position, userEp)
			}
		}
	}

	private fun callIntent(position: Int) {
		val intent = Intent(this@SeasonActivity, EpsodioActivity::class.java).apply {
			putExtras(
				bundleOf(
					Constant.ID to tvShowId,
					Constant.POSICAO to position,
					Constant.TEMPORADA_POSITION to seasonPosition,
					Constant.TVSEASONS to tvSeason,
					Constant.COLOR_TOP to color,
					Constant.SEGUINDO to fallow
				)
			)
		}
		startActivity(intent)
	}

	private fun watchSeason(status: Boolean, id: Int) {
		fun fillWatchEp(watch: Boolean): HashMap<String, Any> {
			return HashMap<String, Any>().also {
				val eps = tvSeason.fillEpUserTvshow(seasons, watch, id).userEps
				it["/$tvShowId/seasons/$seasonPosition/visto"] =
					if (status) isWatchAll(eps) else false
				it["/$tvShowId/seasons/$seasonPosition/userEps"] = eps
			}
		}

		model.watchEp(fillWatchEp(status))
	}

	override fun onStart() {
		super.onStart()
		observerSeasonFire()
		observerSeason()
	}

	private fun handleTitle() {
		setUpToolBar()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = " "
	}

	private fun observerSeasonFire() {
		model.isFallow.observe(this, Observer {
			it.ifValid {
				(recycleView_temporada.adapter as SeasonAdapter).changeFallow(it)
				fallow = it
				model.getSeasonFire(idTvshow = tvShowId, seasonNumber = seasonPosition)
			}
		})

		model.seasons.observe(this, androidx.lifecycle.Observer {
			if (it.exists()) {
				seasons = it.getValue(UserSeasons::class.java)
				(recycleView_temporada.adapter as SeasonAdapter).addSeasonFire(seasons)
			} else {
				(recycleView_temporada.adapter as SeasonAdapter).addSeasonFire(null)
			}
		})
	}

	private fun observerSeason() {
		model.tvSeason.observe(this, Observer {
			when (it) {
				is Success -> {
					tvSeason = it.result
					supportActionBar?.title =
						if (tvSeason.name?.isNotEmpty() == true) tvSeason.name else titleSeason
					(recycleView_temporada.adapter as SeasonAdapter).addTvSeason(tvSeason.episodes)
					model.loadingView(false)
				}
				is Failure -> {
					model.loadingView(false)
					ops()
				}
				is Loading -> {
				}
			}
		})

		model.loadingView.observe(this, Observer { setLoading(it) })
	}

	private fun setLoading(loading: Boolean) {
		if (loading) progress_horizontal.visible() else progress_horizontal.gone()
	}

	private fun isWatchAll(eps: List<UserEp>): Boolean {
		fun containsEpNotWatch() = eps.find { !it.isAssistido }
		return containsEpNotWatch() == null
	}

	private fun onClickSeasonReated(
		ep: EpisodesItem,
		position: Int,
		userEp: UserEp?,
	) {
		if (ep.airDate?.released() == true) {
			Dialog(this@SeasonActivity).apply {
				requestWindowFeature(Window.FEATURE_NO_TITLE)
				setContentView(Layout.dialog_custom_rated)
				val width = resources.getDimensionPixelSize(R.dimen.popup_width)
				val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)
				window!!.setLayout(width, height)

				findViewById<TextView>(R.id.rating_title).text = ep.name
				val ratingBar = findViewById<RatingBar>(R.id.ratingBar_rated).apply {
					rating = userEp?.nota ?: Constant.ZERO
				}

				findViewById<Button>(R.id.cancel_rated).apply {
					visibility =
						if (userEp != null && userEp.isAssistido) View.VISIBLE else View.INVISIBLE
					setOnClickListener {
						val episode = ep.createUserEp().apply {
							isAssistido = false
							nota = Constant.ZERO
						}
						val childUpdates = HashMap<String, Any>()
						childUpdates["$tvShowId/seasons/$seasonPosition/visto/"] = false
						childUpdates["$tvShowId/seasons/$seasonPosition/userEps/$position/"] =
							episode
						model.watchEp(childUpdates)
						dismiss()
					}
				}

				findViewById<Button>(R.id.ok_rated).apply {
					setOnClickListener {
						val epRated = ep.createUserEp().apply {
							nota = ratingBar.rating
							this.isAssistido = true
						}
						val eps = tvSeason
							.fillEpUserTvshow(seasons, true, id).userEps
							.replaceItemList(epRated) { it.id == epRated.id }
						val childUpdates = HashMap<String, Any>().apply {
							this["$tvShowId/seasons/$seasonPosition/visto"] = isWatchAll(eps)
							this["$tvShowId/seasons/$seasonPosition/userEps/$position/"] = epRated
						}
						model.watchEp(childUpdates)
						setRateIMDB(epRated)
						dismiss()
					}
				}
			}.show()
		} else {
			makeToast(getString(R.string.atualizar))
		}
	}

	private fun setRateIMDB(epRated: UserEp) {
		model.setRatedTvShowOnTheMovieDB(tvShowId, epRated)
	}

	override fun onCreateOptionsMenu(menu: Menu) = true
	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) { finish() }
		return true
	}
}


