package activity

import android.R.id
import android.R.style
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.WindowManager.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Keep
import androidx.appcompat.app.AlertDialog.Builder
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.SearchView.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.PopCornViewModelFactory
import br.com.icaro.filme.BuildConfig
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import busca.SearchMultiActivity
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import configuracao.SettingsActivity
import domain.busca.MultiSearch
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lista.ListGenericActivity
import loading.firebase.TypeDataRef.*
import login.LoginActivity
import main.MainActivity
import pessoaspopulares.activity.PersonPopularActivity
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import seguindo.FallowingActivity
import utils.Api
import utils.Constant
import utils.Constant.ListOnTheMovie
import utils.UtilsApp.getBaseUrlImagem
import utils.UtilsApp.writeBitmap
import utils.UtilsKt
import yourLists.YourListActivity
import java.io.File
import java.io.Serializable
import java.util.HashMap
import java.util.Random
import java.util.concurrent.TimeUnit.*

/**
 * Created by icaro on 24/06/16.
 */
@Keep
abstract class BaseActivity : AppCompatActivity(), LifecycleOwner {
	//todo  Refatorar para criar o metodo snack() para enviar uma funcao com o getDados no hight ordem

	protected abstract var layout: Int

	protected var drawerLayout: DrawerLayout? = null
	protected var navigationView: NavigationView? = null
	private var imgUserPhoto: ImageView? = null
	private var tUserName: TextView? = null
	private var tLogin: TextView? = null
	private var textLogin: TextView? = null
	private var mFirebaseRemoteConfig: FirebaseRemoteConfig? = null
	private var dialog: Dialog? = null
	private val subscriptions = CompositeSubscription()

	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		setContentView(layout)
	}

	@ExperimentalCoroutinesApi
	protected fun <T : AndroidViewModel?> createViewModel(
		classViewModel: Class<T>,
		activity: Activity
	): T {
		val model = PopCornViewModelFactory(this.application, Api(baseContext), activity)
		return ViewModelProviders.of(this, model).get(classViewModel)
	}

	protected fun setUpToolBar() {
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		if (toolbar != null) {
			toolbar.setTitleTextColor(resources.getColor(color.white))
			setSupportActionBar(toolbar)
		}
	}

	protected fun setAdMob(adView: AdView?) {
		UtilsKt.setAdMob(adView!!)
	}

	fun hideSoftKeyboard() {
		window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
	}

	protected fun setupNavDrawer() {
		val actionBar = supportActionBar
		actionBar!!.setHomeAsUpIndicator(drawable.ic_menu)
		actionBar.setDisplayHomeAsUpEnabled(true)
		drawerLayout = findViewById(R.id.drawer_layoyt)
		navigationView = findViewById(R.id.nav_view)
		navigationView?.itemIconTintList = null
		if (navigationView != null && drawerLayout != null) {
			val view = layoutInflater.inflate(R.layout.nav_drawer_header, navigationView)
			view.visibility = VISIBLE
			view.findViewById<View>(R.id.textLogin)
			imgUserPhoto = view.findViewById(R.id.imgUserPhoto)
			tUserName = view.findViewById(R.id.tUserName)
			tLogin = view.findViewById(R.id.tLogin)
			textLogin = view.findViewById(R.id.textLogin)
			navigationView!!.setNavigationItemSelectedListener { item -> //Seleciona a Linha
				item.isChecked = true
				//Fecha o menu
				drawerLayout!!.closeDrawers()
				//trata o evento do menu
				onNavDrawerItemSelected(item)
				true
			}
		}
		validarNavDrawerComLogin()
	}

	private fun validarNavDrawerComLogin() {
		val auth = FirebaseAuth.getInstance()
		var grupo_login = navigationView!!.menu
		if (auth.currentUser == null) {
			textLogin!!.setText(string.fazer_login)
			textLogin!!.textSize = 20f
			textLogin!!.visibility = VISIBLE
			imgUserPhoto!!.setImageResource(drawable.add_user)
			grupo_login = navigationView!!.menu
			grupo_login.removeGroup(R.id.menu_drav_logado)
			imgUserPhoto!!.setOnClickListener(onClickListenerLogar())
		} else {
			val user = auth.currentUser
			if (user!!.isAnonymous) {
				textLogin!!.visibility = VISIBLE
				grupo_login.setGroupVisible(R.id.menu_drav_logado, true)
				tLogin!!.setText(string.anonymous)
				tUserName!!.setText(string.criar_login_popcorn)
				imgUserPhoto!!.setImageResource(drawable.add_user)
			} else {
				if (!user.providerData.isEmpty()) {
					textLogin!!.visibility = VISIBLE
					grupo_login.setGroupVisible(R.id.menu_drav_logado, true)
					tUserName!!.text = if (user.displayName != null) user.displayName else ""
					tLogin!!.text = if (user.email != null) user.email else ""
					Picasso.get().load(user.photoUrl)
						.placeholder(drawable.person)
						.into(imgUserPhoto)
				}
			}
		}
	}

	protected fun setCheckable(id: Int) {
		when (id) {
			R.id.menu_drav_home -> {
				run { this.navigationView!!.setCheckedItem(id) }
				run { this.navigationView!!.setCheckedItem(id) }
				run { this.navigationView!!.setCheckedItem(id) }
				run { this.navigationView!!.setCheckedItem(id) }
			}
			R.id.menu_drav_person -> {
				run { this.navigationView!!.setCheckedItem(id) }
				run { this.navigationView!!.setCheckedItem(id) }
				run { this.navigationView!!.setCheckedItem(id) }
			}
			R.id.menu_drav_oscar -> {
				run { this.navigationView!!.setCheckedItem(id) }
				run { this.navigationView!!.setCheckedItem(id) }
			}
			R.id.seguindo -> {
				navigationView!!.setCheckedItem(id)
			}
		}
	}

	private fun onNavDrawerItemSelected(menuItem: MenuItem) {
		val intent: Intent
		when (menuItem.itemId) {
			R.id.menu_drav_home -> {
				intent = Intent(this, MainActivity::class.java)
				intent.putExtra(Constant.ABA, R.id.menu_drav_home)
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
				startActivity(intent)
			}
			R.id.nav_item_settings -> {
				intent = Intent(this, SettingsActivity::class.java)
				startActivity(intent)
			}
			R.id.favorite -> {
				intent = Intent(this, YourListActivity::class.java)
				intent.putExtra(Constant.ABA, getString(string.favorite))
				intent.putExtra(Constant.MEDIATYPE, FAVORITY)
				startActivity(intent)
			}
			R.id.rated -> {
				intent = Intent(this, YourListActivity::class.java)
				intent.putExtra(Constant.ABA, getString(string.avaliados))
				intent.putExtra(Constant.MEDIATYPE, RATED)
				startActivity(intent)
			}
			R.id.watchlist -> {
				intent = Intent(this, YourListActivity::class.java)
				intent.putExtra(Constant.ABA, getString(string.quero_assistir))
				intent.putExtra(Constant.MEDIATYPE, WATCH)
				startActivity(intent)
			}
			R.id.menu_drav_person -> {
				intent = Intent(this, PersonPopularActivity::class.java)
				startActivity(intent)
			}
			R.id.menu_drav_oscar -> {
				intent = Intent(this, ListGenericActivity::class.java)
				intent.putExtra(Constant.LISTA_ID, ListOnTheMovie.OSCAR)
				intent.putExtra(Constant.LISTA_NOME, getString(string.oscar))
				startActivity(intent)
			}
			R.id.menu_drav_surpresa -> parametrosDoRemoteConfig
			R.id.seguindo -> {
				intent = Intent(this, FallowingActivity::class.java)
				intent.putExtra(Constant.LISTA_ID, BuildConfig.VERSION_CODE - 1)
				intent.putExtra(Constant.LISTA_NOME, string.oscar)
				startActivity(intent)
			}
		}
	}

	fun ops() {
		Toast.makeText(baseContext, getString(R.string.ops), Toast.LENGTH_LONG).show()
	}

	fun snack(anchor: View, txt: String = getString(R.string.no_internet), block: () -> Unit = {}) {
		Snackbar.make(anchor, txt, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) { block() }.show()
	}

	private val parametrosDoRemoteConfig: Unit
		private get() { // TOOD remover essa configuração
			val intent = Intent(this, ListGenericActivity::class.java)
			mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
			val configSettings = Builder()
				.build()
			mFirebaseRemoteConfig!!.setConfigSettingsAsync(configSettings)
			mFirebaseRemoteConfig!!.setDefaultsAsync(xml.xml_defaults)
			var cacheExpiration: Long = 3600 // 1 hour in seconds.
			// If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
			// the server.
			if (BuildConfig.DEBUG) {
				cacheExpiration = 0
			}
			mFirebaseRemoteConfig!!.fetch(cacheExpiration)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						mFirebaseRemoteConfig!!.fetchAndActivate()
					}
					var map: Map<String?, String?> = HashMap()
					map = listaRemoteConfig
					val numero = Random().nextInt(10).toString()
					//Log.d(TAG, "numero : " + numero);
					//TODO mandar somente o Map e fazer a troca na outra activity
					intent.putExtra(Constant.LISTA_ID, map["id$numero"])
					intent.putExtra(Constant.LISTA_NOME, map["title$numero"])
					intent.putExtra(Constant.BUNDLE, map as Serializable)
					startActivity(intent)
				}
		}
	private val listaRemoteConfig: Map<String?, String?>
		private get() {
			val map: MutableMap<String?, String?> = HashMap()
			for (i in 0..9) {
				map["id$i"] = mFirebaseRemoteConfig!!.getString("id$i")
				map["title$i"] = mFirebaseRemoteConfig!!.getString("title$i")
			}
			return map
		}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			id.home -> {
				if (drawerLayout != null) {
					openDrawer()
					return true
				}
			}
			R.id.search -> {
				if (dialog == null) {
					val view = layoutInflater.inflate(R.layout.layout_search_multi, null)
					dialog =
						Builder(this@BaseActivity, style.Theme_Translucent_NoTitleBar_Fullscreen)
							.setView(view)
							.setCancelable(true)
							.create()
				}
				if (item.icon != null) item.icon.alpha = 10
				dialog!!.show()
				val searchViewDialog: SearchView =
					dialog!!.findViewById(R.id.layout_search_multi_search)
				searchViewDialog.setIconifiedByDefault(false)
				searchViewDialog.isFocusable = true
				searchViewDialog.isIconified = false
				searchViewDialog.requestFocusFromTouch()
				searchViewDialog.setOnCloseListener {
					if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
					false
				}
				dialog!!.setOnDismissListener { dialog: DialogInterface? ->
					window.setSoftInputMode(
						LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
					)
					if (item.icon != null) item.icon.alpha = 255
				}
				searchViewDialog.setOnQueryTextListener(object : OnQueryTextListener {
					override fun onQueryTextSubmit(query: String): Boolean {
						if (item.icon != null) item.icon.alpha = 255
						val intent = Intent(this@BaseActivity, SearchMultiActivity::class.java)
						intent.putExtra(SearchManager.QUERY, query)
						startActivity(intent)
						return false
					}

					override fun onQueryTextChange(newText: String): Boolean {
						if (newText.isEmpty() || newText.length < 2) return false
						val inscricao = Api(this@BaseActivity)
							.procuraMulti(newText)
							.distinctUntilChanged()
							.debounce(1200, MILLISECONDS)
							.subscribeOn(Schedulers.io())
							.observeOn(AndroidSchedulers.mainThread())
							.subscribe(object : Observer<MultiSearch?> {
								override fun onCompleted() {}
								override fun onError(e: Throwable) {
									Toast.makeText(this@BaseActivity,
										getString(string.ops),
										Toast.LENGTH_SHORT).show()
								}

								override fun onNext(multiRetorno: MultiSearch?) {
									val recyclerView: RecyclerView =
										dialog!!.findViewById(R.id.layout_search_multi_recycler)
									recyclerView.setHasFixedSize(true)
									recyclerView.layoutManager = LinearLayoutManager(application)
									recyclerView.adapter = MultiSearchAdapter(this@BaseActivity,
										multiRetorno!!,
										item.icon)
								}
							})
						subscriptions.add(inscricao)
						return false
					}
				})
			}
		}
		return super.onOptionsItemSelected(item)
	}

	override fun onCreateOptionsMenu(menu: Menu): Boolean {
		menuInflater.inflate(R.menu.menu, menu) //
		return true
	}

	override fun onDestroy() {
		subscriptions.unsubscribe()
		super.onDestroy()
	}

	protected fun salvaImagemMemoriaCache(
		context: Context,
		endereco: String,
		callback: SalvarImageShare
	) {
		val imageView = ImageView(context)
		Picasso.get().load(getBaseUrlImagem(4) + endereco).into(imageView, object : Callback {
			override fun onSuccess() {
				val file = context.externalCacheDir
				if (file != null) if (!file.exists()) {
					file.mkdir()
				}
				val dir = File(file, endereco)
				val drawable = imageView.drawable as BitmapDrawable
				if (drawable != null) {
					val bitmap = drawable.bitmap
					writeBitmap(dir, bitmap)
				}
				callback.retornaFile(dir)
			}

			override fun onError(e: Exception) {
				callback.RetornoFalha()
			}
		})
	}

	//Abre Menu Lateral
	private fun openDrawer() {
		if (drawerLayout != null) {
			drawerLayout!!.openDrawer(GravityCompat.START)
		}
	}

	protected fun onClickListenerLogar(): OnClickListener {
		return OnClickListener { view: View? ->
			startActivity(Intent(this@BaseActivity,
				LoginActivity::class.java))
		}
	}

	//Fecha Menu Lateral
	protected fun closeDrawer() {
		if (drawerLayout != null) {
			drawerLayout!!.closeDrawer(GravityCompat.START)
		}
	}

	fun validatePassword(password: String): Boolean {
		return password.length > 5
	}

	interface SalvarImageShare {
		fun retornaFile(file: File)
		fun RetornoFalha()
	}
}