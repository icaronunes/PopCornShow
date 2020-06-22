package temporada

import activity.BaseActivityAb
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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import applicaton.BaseViewModel.BaseRequest.Failure
import applicaton.BaseViewModel.BaseRequest.Loading
import applicaton.BaseViewModel.BaseRequest.Success
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import domain.EpisodesItem
import domain.TvSeasons
import domain.UserEp
import domain.UserSeasons
import episodio.EpsodioActivity
import kotlinx.android.synthetic.main.temporada_layout.recycleView_temporada
import rx.subscriptions.CompositeSubscription
import tvshow.viewmodel.TvShowViewModel
import utils.Constant
import utils.UtilsApp
import utils.makeToast
import utils.patternRecyler
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.HashMap
import java.util.Locale

/**
 * Created by icaro on 26/08/16.
 */
class TemporadaActivity(override var layout: Int = R.layout.temporada_layout) : BaseActivityAb(),
	TemporadaOnClickListener {
	private var temporada_id: Int = 0
	private var temporada_position: Int = 0
	private var nome_temporada: String? = null
	private var serieId: Int = 0
	private var color: Int = 0
	private var tvSeason: TvSeasons? = null
	private var seguindo: Boolean = false
	private var seasons: UserSeasons? = null
	private var mAuth: FirebaseAuth? = null
	private var myRef: DatabaseReference? = null
	private var postListener: ValueEventListener? = null
	private var subscription: CompositeSubscription? = null
	private lateinit var model: TvShowViewModel

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		handleTitle()
		getExtras()
		model = createViewModel(TvShowViewModel::class.java, this)
		initAdapter()
		mAuth = FirebaseAuth.getInstance()
		myRef = FirebaseDatabase.getInstance().getReference("users")
		subscription = CompositeSubscription()

		if (UtilsApp.isNetWorkAvailable(this)) {
			model.getSeason(serieId, temporada_id)
			model.getSeasonFire(idTvshow = serieId, seasonNumber = temporada_id)
		} else {
			snack()
		}
	}

	private fun initAdapter() {
		recycleView_temporada.patternRecyler(false).apply {
			adapter = TemporadaFoldinAdapter(
				this@TemporadaActivity, this@TemporadaActivity
			)
		}
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

	private fun snack() {
		Snackbar.make(recycleView_temporada, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(baseContext)) {
					model.getSeason(serieId, temporada_id)
				} else {
					snack()
				}
			}.show()
	}

	private fun getExtras() {
		if (intent.action == null) {
			temporada_id = intent.getIntExtra(Constant.TEMPORADA_ID, 0)
			temporada_position = intent.getIntExtra(Constant.TEMPORADA_POSITION, 0)
			serieId = intent.getIntExtra(Constant.TVSHOW_ID, 0)
			nome_temporada = intent.getStringExtra(Constant.NAME)
			color = intent.getIntExtra(Constant.COLOR_TOP, resources.getColor(R.color.red))
		} else {
			temporada_id = Integer.parseInt(intent.getStringExtra(Constant.TEMPORADA_ID))
			temporada_position =
				Integer.parseInt(intent.getStringExtra(Constant.TEMPORADA_POSITION))
			serieId = Integer.parseInt(intent.getStringExtra(Constant.TVSHOW_ID))
			nome_temporada = intent.getStringExtra(Constant.NAME)
			color = Integer.parseInt(intent.getStringExtra(Constant.COLOR_TOP))
		}
	}

	override fun onClickVerTemporada(position: Int) {
		model.watchEp(fillWatchEp(position)) //precisa receber tvshow para criar no firebase
	}

	private fun fillWatchEp(position: Int, watch: Boolean = true): HashMap<String, Any> {
		return HashMap<String, Any>().also {

			it["/visto"] = temporadaTodaAssistida(position)
			it["/$serieId/seasons/$temporada_position/userEps/$position/assistido"] = watch

		}
	}

	override fun onClickTemporada(position: Int) {
		startActivity(Intent(this@TemporadaActivity, EpsodioActivity::class.java).apply {
			putExtra(Constant.TVSHOW_ID, serieId)
			putExtra(Constant.POSICAO, position)
			putExtra(Constant.TEMPORADA_POSITION, temporada_position)
			putExtra(Constant.TVSEASONS, tvSeason)
			putExtra(Constant.COLOR_TOP, color)
			putExtra(Constant.SEGUINDO, seguindo)
		})
	}

	override fun onClickTemporadaNota(
		view: View?,
		ep: EpisodesItem,
		position: Int,
		userEp: UserEp?,
		notifyItemChanged: () -> Unit
	) {
		var date: Date? = null

		val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
		try {
			ep.airDate?.let { date = sdf.parse(it) }
		} catch (e: ParseException) {
			e.printStackTrace()
		}

		if (UtilsApp.verifyLaunch(date)) {
			Dialog(this@TemporadaActivity).apply {
				requestWindowFeature(Window.FEATURE_NO_TITLE)
				setContentView(R.layout.dialog_custom_rated)
				val width = resources.getDimensionPixelSize(R.dimen.popup_width)
				val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)
				window!!.setLayout(width, height)

				findViewById<TextView>(R.id.rating_title).text = ep.name
				val ratingBar = findViewById<RatingBar>(R.id.ratingBar_rated).apply {
					rating = userEp?.nota ?: 0.0f
				}

				findViewById<Button>(R.id.cancel_rated).apply {
					visibility = if(userEp != null && userEp.isAssistido) View.VISIBLE else View.INVISIBLE
					setOnClickListener {
						val episode = UserEp(ep.id!!, ep.seasonNumber, ep.episodeNumber!!, false, 0.0f, ep.name, ep.airDate)
						val childUpdates = HashMap<String, Any>()
						childUpdates["$serieId/seasons/$temporada_position/visto/"] = false
						childUpdates["$serieId/seasons/$temporada_position/userEps/$position/"] = episode
						model.watchEp(childUpdates)
						dismiss()
					}
				}

				findViewById<Button>(R.id.ok_rated).apply {
					setOnClickListener {
						val childUpdates = HashMap<String, Any>()
						val epRated = UserEp(ep.id!!, ep.seasonNumber, ep.episodeNumber!!, true, ratingBar.rating, ep.name, ep.airDate)
						childUpdates["$serieId/seasons/$temporada_position/visto"] = temporadaTodaAssistida(position)
						childUpdates["$serieId/seasons/$temporada_position/userEps/$position/"] = epRated
						model.watchEp(childUpdates)
						setnotaIMDB(epRated)
						dismiss()
					}
				}

			}.show()
		} else {
			makeToast(getString(R.string.atualizar))
		}
	}

	override fun onClickScrool(position: Int) {
		scrollToTop(position)
	}

	private fun watchBefore(position: Int) {
		val dialog = AlertDialog.Builder(this@TemporadaActivity)
			.setTitle(R.string.title_marcar_ep_anteriores)
			.setMessage(R.string.msg_marcar_ep_anteriores)
			.setPositiveButton(R.string.ok) { _, _ ->
				val user = mAuth!!.currentUser!!.uid

				val childUpdates = HashMap<String, Any>()
				for (i in 0..position) {
					childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/userEps/$i/assistido"] =
						true
				}

				if (position == seasons?.userEps?.size!! - 1) {
					childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/visto/"] =
						true
				} else {
					childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/visto/"] =
						temporadaTodaAssistida(position)
				}

				myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
					if (databaseError == null) {
						databaseReference.child(user).child("seguindo")
							.child(serieId.toString()).child("seasons")
							.child(temporada_position.toString())
							.child("userEps")
							.addListenerForSingleValueEvent(object : ValueEventListener {
								override fun onDataChange(dataSnapshot: DataSnapshot) {
									if (dataSnapshot.exists()) {
										for (i in 0..position) {
											val userEp = dataSnapshot.child(i.toString())
												.getValue(UserEp::class.java)
											(recycleView_temporada.adapter as TemporadaFoldinAdapter).notificarMudanca(
												userEp,
												i
											)
										}
									} else {
										Toast.makeText(
											this@TemporadaActivity,
											R.string.ops,
											Toast.LENGTH_SHORT
										).show()
									}
								}

								override fun onCancelled(databaseError: DatabaseError) {
								}
							})
					} else {
						Toast.makeText(
							this@TemporadaActivity,
							R.string.marcado_assistido,
							Toast.LENGTH_SHORT
						).show()
					}
				}

				Toast.makeText(
					this@TemporadaActivity,
					R.string.marcado_assistido,
					Toast.LENGTH_SHORT
				).show()
			}
			.setNegativeButton(R.string.no) { _, _ ->
				val user = mAuth!!.currentUser!!.uid
				val childUpdates = HashMap<String, Any>()

				childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/userEps/$position/assistido"] =
					true
				childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/visto/"] =
					temporadaTodaAssistida(position)

				myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
					if (databaseError == null) {
						databaseReference.child(user).child("seguindo")
							.child(serieId.toString()).child("seasons")
							.child(temporada_position.toString())
							.child("userEps").child(position.toString())
							.addListenerForSingleValueEvent(object : ValueEventListener {
								override fun onDataChange(dataSnapshot: DataSnapshot) {
									if (dataSnapshot.exists()) {
										val userEp = dataSnapshot.getValue(UserEp::class.java)
										(recycleView_temporada.adapter as TemporadaFoldinAdapter).notificarMudanca(
											userEp,
											position
										)
									} else {
										Toast.makeText(
											this@TemporadaActivity,
											R.string.ops,
											Toast.LENGTH_SHORT
										).show()
									}
								}

								override fun onCancelled(databaseError: DatabaseError) {
								}
							})
					} else {
						Toast.makeText(
							this@TemporadaActivity,
							R.string.marcado_assistido,
							Toast.LENGTH_SHORT
						).show()
					}
				}
				Toast.makeText(
					this@TemporadaActivity,
					R.string.marcado_assistido,
					Toast.LENGTH_SHORT
				).show()
			}
			.create()
		dialog.show()
	}

	private fun addEpWatch(position: Int) {
		val user = mAuth!!.currentUser!!.uid

		val childUpdates = HashMap<String, Any>()

		childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/userEps/$position/assistido"] =
			true
		childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/visto/"] =
			temporadaTodaAssistida(position)

		myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
			if (databaseError == null) {
				databaseReference.child(user).child("seguindo")
					.child(serieId.toString()).child("seasons")
					.child(temporada_position.toString())
					.child("userEps").child(position.toString())
					.addListenerForSingleValueEvent(object : ValueEventListener {
						override fun onDataChange(dataSnapshot: DataSnapshot) {
							if (dataSnapshot.exists()) {
								val userEp = dataSnapshot.getValue(UserEp::class.java)
								(recycleView_temporada.adapter as TemporadaFoldinAdapter)
									.notificarMudanca(userEp, position)
							} else {
								Toast.makeText(
									this@TemporadaActivity,
									R.string.ops,
									Toast.LENGTH_SHORT
								).show()
							}
						}

						override fun onCancelled(databaseError: DatabaseError) {
						}
					})
			} else {
				Toast.makeText(
					this@TemporadaActivity,
					R.string.marcado_assistido,
					Toast.LENGTH_SHORT
				).show()
			}
		}
		Toast.makeText(this@TemporadaActivity, R.string.marcado_assistido, Toast.LENGTH_SHORT)
			.show()
	}

	private fun removeWatch(position: Int) {
		val user = mAuth!!.currentUser!!.uid

		val childUpdates = HashMap<String, Any>()

		childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/userEps/$position/assistido"] =
			false
		childUpdates["/$user/seguindo/$serieId/seasons/$temporada_position/visto/"] = false
		myRef!!.updateChildren(childUpdates) { databaseError, databaseReference ->
			if (databaseError == null) {
				databaseReference
					.child(user).child("seguindo")
					.child(serieId.toString()).child("seasons")
					.child(temporada_position.toString())
					.child("userEps").child(position.toString())
					.addListenerForSingleValueEvent(object : ValueEventListener {
						override fun onDataChange(dataSnapshot: DataSnapshot) {
							if (dataSnapshot.exists()) {
								val userEp = dataSnapshot.getValue(UserEp::class.java)
								(recycleView_temporada.adapter as TemporadaFoldinAdapter)
									.notificarMudanca(userEp, position)
								Toast.makeText(
									this@TemporadaActivity,
									R.string.marcado_nao_assistido,
									Toast.LENGTH_SHORT
								).show()
							} else {
								Toast.makeText(
									this@TemporadaActivity,
									R.string.ops,
									Toast.LENGTH_SHORT
								).show()
							}
						}

						override fun onCancelled(databaseError: DatabaseError) {
						}
					})
			} else {
				Toast.makeText(this@TemporadaActivity, R.string.ops, Toast.LENGTH_SHORT).show()
			}
		}
	}

	private fun scrollToTop(currentCard: Int) {
		val layoutManager = recycleView_temporada.layoutManager as LinearLayoutManager?
		layoutManager?.scrollToPositionWithOffset(currentCard, 0)
	}

	private fun setnotaIMDB(epRated: UserEp) {
		model.setRatedTvShowOnTheMovieDB(epRated)
//		val job = GlobalScope.launch(Dispatchers.IO) {
//			try {
//				FilmeService.ratedTvshowEpsodioGuest(
//					serieId, seasons?.seasonNumber!!,
//					seasons!!.userEps!![position].episodeNumber, ratingBar, applicationContext
//				)
//			} catch (ex: Exception) {
//			}
//		}
//		job.cancel()
	}

	private fun temporadaTodaAssistida(position: Int): Boolean {

		for (userEp in seasons!!.userEps!!) {
			if (seasons!!.userEps!![position] != userEp) {
				if (!userEp.isAssistido) {
					return false
				}
			}
		}
		return true
	}

	private fun isAssistidoAnteriores(position: Int): Boolean {
		for (i in 0 until position) {
			if (!seasons!!.userEps!![i].isAssistido) {
				return true
			}
		}
		return false
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		return true
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
		}
		return true
	}

	private fun observerSeasonFire() {
		model.seasons.observe(this, androidx.lifecycle.Observer {
			if (it.exists()) {
				seasons = it.getValue(UserSeasons::class.java)
				(recycleView_temporada.adapter as TemporadaFoldinAdapter).addSeasonFire(seasons, true)
			} else {
				(recycleView_temporada.adapter as TemporadaFoldinAdapter).addSeasonFire(null, false)
			}
		})
	}

	private fun observerSeason() {
		model.tvSeason.observe(this, Observer {
			when (it) {
				is Success -> {
					tvSeason = it.result
					supportActionBar!!.title =
						if (tvSeason?.name?.isNotEmpty()!!) tvSeason?.name else nome_temporada
					(recycleView_temporada.adapter as TemporadaFoldinAdapter).addTvSeason(
						tvSeason?.episodes ?: listOf()
					)
				}
				is Failure -> {
					Crashlytics.logException(it.error)
					ops()
				}
				is Loading -> {

				}
			}
		})
	}
}
