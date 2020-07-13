package tvshow.activity

import activity.BaseActivityAb
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.BaseRequest.Failure
import applicaton.BaseViewModel.BaseRequest.Loading
import applicaton.BaseViewModel.BaseRequest.Success
import br.com.icaro.filme.R
import br.com.icaro.filme.R.string
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import domain.TvshowDB
import domain.tvshow.Tvshow
import hasValue
import kotlinx.android.synthetic.main.fab_float.fab_menu
import kotlinx.android.synthetic.main.fab_float.menu_item_favorite
import kotlinx.android.synthetic.main.fab_float.menu_item_rated
import kotlinx.android.synthetic.main.fab_float.menu_item_watchlist
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal
import kotlinx.android.synthetic.main.tvserie_activity.collapsing_toolbar
import kotlinx.android.synthetic.main.tvserie_activity.img_top_tvshow
import kotlinx.android.synthetic.main.tvserie_activity.streamview_tv
import kotlinx.android.synthetic.main.tvserie_activity.tabLayout
import kotlinx.android.synthetic.main.tvserie_activity.toolbar
import kotlinx.android.synthetic.main.tvserie_activity.viewPager_tvshow
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tvshow.TvShowAdapter
import tvshow.viewmodel.TvShowViewModel
import utils.Constant
import utils.UtilsApp
import utils.animeRotation
import utils.createIdReal
import utils.getNameTypeReel
import utils.gone
import utils.makeToast
import utils.released
import utils.setAnimation
import utils.setPicassoWithCache
import utils.visible
import java.io.File

class TvShowActivity(override var layout: Int = R.layout.tvserie_activity) : BaseActivityAb() {

	private val EMPTYRATED: Float = 0.0f
	private lateinit var model: TvShowViewModel
	private var idTvshow: Int = 0
	private var idReel: String = ""
	private var colorTop: Int = 0
	private lateinit var series: Tvshow
	private var seguindo: Boolean = false
	private var numberRated: Float = 0.0f

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		setupNavDrawer()
		getExtras()
		setTitleAndDisableTalk()
		model = createViewModel(TvShowViewModel::class.java, this)
		observers()
		if (UtilsApp.isNetWorkAvailable(baseContext)) {
			getDataTvshow()
		} else {
			snack()
		}
	}

	private fun observers() {
		model.favorit.observe(this, Observer {
			setEventListenerFavorite(it.child("$idTvshow").exists())
		})

		model.rated.observe(this, Observer {
			setEventListenerRated(it.child("$idTvshow").exists())
			setRatedValue(it)
		})

		model.auth.observe(this, Observer {
			setFabVisible(it)
		})

		model.watch.observe(this, Observer {
			setEventListenerWatch(it.child("$idTvshow").exists())
		})

		model.tvshow.observe(this, Observer {
			when (it) {
				is Success -> {
					series = it.result
					setupViewPagerTabs(series)
					setImageTop(series.backdropPath ?: "")
					setFab()
					model.loadingMedia(false)
					if (idReel.isEmpty()) model.getRealGoodData(series.createIdReal())
				}
				is Failure -> {
					ops()
				}
				is Loading -> {
					setLoading(it.loading)
				}
			}
		})

		model.real.observe(this, Observer {
			when (it) {
				is Success -> {
					streamview_tv.fillStream(
						series.originalName?.getNameTypeReel()
							?: "", it.result.sources
					)
				}
				is Failure -> {
					streamview_tv.error = true
				}
			}
			setAnimated()
		})
	}

	private fun setLoading(loading: Boolean) {
		if(loading) progress_horizontal.visible() else progress_horizontal.gone()
	}

	private fun setFabVisible(visible: Boolean) {
		if (visible) {
			setColorFab(colorTop)
			fab_menu.visible()
		} else {
			fab_menu.gone()
		}
	}

	private fun setAnimated() {
		val sheet = BottomSheetBehavior.from(streamview_tv)
		(sheet as BottomSheetBehavior<View>).setAnimation(
			viewPager_tvshow,
			streamview_tv.findViewById(R.id.title_streaming)
		)
		streamview_tv.setClose(sheet)
	}

	private fun setTitleAndDisableTalk() {
		collapsing_toolbar?.setBackgroundColor(colorTop)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		collapsing_toolbar.title = " "
		collapsing_toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
		collapsing_toolbar.isFocusable = false
		toolbar.title = " "
		toolbar.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
	}

	private fun getDataTvshow() {
		if (idReel.isNotEmpty()) model.getRealGoodData(idReel)
		GlobalScope.launch {
			model.getTvshow(idTvshow)
		}
	}

	private fun setEventListenerWatch(boolean: Boolean) {
		if (boolean) {
			menu_item_watchlist?.labelText = resources.getString(R.string.remover_watch)
		} else {
			menu_item_watchlist?.labelText = resources.getString(R.string.adicionar_watch)
		}
	}

	private fun setEventListenerRated(boolean: Boolean) {
		if (boolean) {
			menu_item_rated?.labelText = resources.getString(R.string.remover_rated)
		} else {
			menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
		}
	}

	private fun setEventListenerFavorite(boolean: Boolean) {
		if (boolean) {
			menu_item_favorite?.labelText = resources.getString(R.string.remover_favorite)
		} else {
			menu_item_favorite?.labelText = resources.getString(R.string.adicionar_favorite)
		}
	}

	private fun setRatedValue(it: DataSnapshot) {
		numberRated = it.child(idTvshow.toString()).child("nota").value?.toString()?.toFloat()
			?: EMPTYRATED
	}

	override fun onDestroy() {
		model.destroy()
		super.onDestroy()
	}

	private fun getExtras() {
		if (intent.action == null) {
			colorTop = intent.getIntExtra(Constant.COLOR_TOP, R.color.colorFAB)
			idTvshow = intent.getIntExtra(Constant.TVSHOW_ID, 0)
			if (intent.hasExtra(Constant.ID_REEL)) {
				idReel = intent.getStringExtra(Constant.ID_REEL)
			}
		} else {
			colorTop = Integer.parseInt(intent.getStringExtra(Constant.COLOR_TOP))
			idTvshow = Integer.parseInt(intent.getStringExtra(Constant.TVSHOW_ID))
			if (intent.hasExtra(Constant.ID_REEL)) {
				idReel = intent.getStringExtra(Constant.ID_REEL)
			}
		}
	}

	private fun snack() {
		Snackbar.make(viewPager_tvshow, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(baseContext)) {
					getDataTvshow()
				} else {
					snack()
				}
			}.show()
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (series.hasValue) {
			if (item.itemId == R.id.share) {
				salvaImagemMemoriaCache(this@TvShowActivity, series.posterPath,
					object : SalvarImageShare {
						override fun retornaFile(file: File) {
							val intent = Intent(Intent.ACTION_SEND).apply {
								type = "message/rfc822"
								type = "image/*"
								putExtra(
									Intent.EXTRA_TEXT,
									"${series.name} ${buildDeepLink(series.id)} by: ${Constant.TWITTER_URL}"
								)
								putExtra(
									Intent.EXTRA_STREAM,
									UtilsApp.getUriDownloadImage(this@TvShowActivity, file)
								)
							}
							startActivity(
								Intent.createChooser(
									intent,
									resources.getString(R.string.compartilhar, series.name)
								)
							)
						}

						override fun RetornoFalha() {
							makeToast(R.string.erro_na_gravacao_imagem)
						}
					})
			}
		} else {
			makeToast(R.string.erro_ainda_sem_imagem)
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onCreateOptionsMenu(menu: Menu?): Boolean {
		menuInflater.inflate(R.menu.menu_share, menu) //
		return true
	}

	fun buildDeepLink(id: Int): String {
		// Get the unique appcode for this app.
		return "https://q2p5q.app.goo.gl/?link=https://br.com.icaro.filme/?action%3Dtvshow%26id%3D$id&apn=br.com.icaro.filme"
	}

	private fun makeTvshiwDb() = TvshowDB().apply {
		title = series.name
		id = series.id
		poster = series.posterPath
	}

	private fun addOrRemoveWatch() {
		menu_item_watchlist.animeRotation()
		model.chanceWatch(add = {
			it.child("$idTvshow").setValue(makeTvshiwDb())
				.addOnCompleteListener {
					makeToast(R.string.filme_add_watchlist)
				}
		}, remove = {
			it.child("$idTvshow").setValue(null)
				.addOnCompleteListener {
					makeToast(R.string.tvshow_watch_remove)
				}
		}, idMedia = idTvshow)
		this@TvShowActivity.fab_menu.close(true)
	}

	private fun addOrRemoveFavorite() {
		menu_item_favorite.animeRotation()
		if (series.firstAirDate?.released() == true) {
			model.putFavority(
				add = {
					it.child(idTvshow.toString()).setValue(null)
						.addOnCompleteListener {
							Toast.makeText(
								this@TvShowActivity,
								getString(R.string.tvshow_remove_favorite),
								Toast.LENGTH_SHORT
							).show()
						}
				},
				remove = {
					it.child(idTvshow.toString()).setValue(makeTvshiwDb())
						.addOnCompleteListener {
							Toast.makeText(
								this@TvShowActivity,
								getString(R.string.tvshow_add_favorite),
								Toast.LENGTH_SHORT
							)
								.show()
						}
				},
				id = idTvshow
			)

			this@TvShowActivity.fab_menu.close(true)
		} else {
			makeToast(R.string.tvshow_nao_lancado)
		}
	}

	private fun ratedMovie() = View.OnClickListener {
		if (series.firstAirDate?.released() == true) {
			Dialog(this@TvShowActivity).apply {
				requestWindowFeature(Window.FEATURE_NO_TITLE)
				setContentView(R.layout.dialog_custom_rated)
				findViewById<TextView>(R.id.rating_title).text = series.name ?: ""
				window?.setLayout(
					WindowManager.LayoutParams.MATCH_PARENT,
					WindowManager.LayoutParams.WRAP_CONTENT
				)

				val ratingBar = findViewById<RatingBar>(R.id.ratingBar_rated)
				ratingBar.rating = numberRated / 2

				findViewById<Button>(R.id.cancel_rated)
					.setOnClickListener {
						removeRated()
						dismiss()
					}

				findViewById<Button>(R.id.ok_rated).setOnClickListener {
					if (ratingBar.rating == 0.0f) {
						removeRated()
					} else {
						addRated(ratingBar)
					}
					dismiss()
				}
				show()
			}
		} else {
			makeToast(R.string.tvshow_nao_lancado)
		}
	}

	private fun addRated(ratingBar: RatingBar) {
		val tvshowDB = makeTvshiwDb().apply { nota = ratingBar.rating * 2 }
		model.setRated(idTvshow) { database ->
			database.child(idTvshow.toString()).setValue(tvshowDB)
				.addOnCompleteListener {
					makeToast("${getString(R.string.tvshow_rated)} - ${tvshowDB.nota}")
				}
			model.setRatedOnTheMovieDB(tvshowDB)

		}
		this@TvShowActivity.fab_menu.close(true)
	}

	private fun removeRated() {
		model.setRated(idTvshow) { database ->
			database.child(idTvshow.toString()).setValue(null)
				.addOnCompleteListener {
					makeToast(string.tvshow_remove_rated)
				}
			this@TvShowActivity.fab_menu.close(true)
		}
	}

	private fun setupViewPagerTabs(tvshow: Tvshow) {
		viewPager_tvshow.apply {
			offscreenPageLimit = 2
			adapter = TvShowAdapter(context, supportFragmentManager, tvshow, colorTop)
			currentItem = 0
			tabLayout.setupWithViewPager(this)
			tabLayout.setSelectedTabIndicatorColor(colorTop)
		}
	}

	private fun setImageTop(path: String) {
		img_top_tvshow.setPicassoWithCache(path, 5, img_erro = R.drawable.top_empty)
		AnimatorSet().apply {
			playTogether(
				ObjectAnimator.ofFloat(img_top_tvshow, View.X, -100f, 0.0f)
					.setDuration(1000)
			)
			start()
		}
	}

	private fun setColorFab(color: Int) {
		fab_menu?.menuButtonColorNormal = color
		menu_item_favorite?.colorNormal = color
		menu_item_watchlist?.colorNormal = color
		menu_item_rated?.colorNormal = color
	}

	private fun setFab() {
		menu_item_watchlist.setOnClickListener { addOrRemoveWatch() }
		menu_item_favorite.setOnClickListener { addOrRemoveFavorite() }
		menu_item_rated.setOnClickListener { ratedMovie() }
	}

	fun getModelView() = model
}