package activity

import Color
import Drawable
import Txt
import android.R.*
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.*
import android.view.WindowManager.LayoutParams
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat.*
import androidx.appcompat.widget.SearchView.*
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProviders
import applicaton.PopCornViewModelFactory
import br.com.icaro.filme.BuildConfig
import br.com.icaro.filme.R
import com.google.android.gms.ads.AdView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings.*
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import configuracao.SettingsActivity
import kotlinx.coroutines.ExperimentalCoroutinesApi
import lista.ListGenericActivity
import loading.firebase.TypeDataRef.*
import login.LoginActivity
import main.MainActivity
import pessoaspopulares.activity.PersonPopularActivity
import rx.subscriptions.CompositeSubscription
import seguindo.FallowingActivity
import utils.Api
import utils.Constant
import utils.Constant.ListOnTheMovie
import utils.Dialogs
import utils.UtilsApp.getBaseUrlImagem
import utils.UtilsApp.writeBitmap
import utils.UtilsKt
import utils.findViewOnHeader
import utils.kotterknife.findOptionalView
import utils.makeToast
import utils.visible
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
	protected abstract var layout: Int
	protected val drawerLayout: DrawerLayout? by findOptionalView(R.id.drawer_layoyt)
	protected val navigationView: NavigationView? by findOptionalView(R.id.nav_view)
	private val photoUser: ImageView? by lazy { getViewOnNavigater<ImageView>(R.id.photo_user) }
	private val login: TextView? by lazy { getViewOnNavigater<TextView>(R.id.login) }
	private val userName: TextView? by lazy { getViewOnNavigater<TextView>(R.id.userName) }
	private var dialog: Dialog? = null
	private val subscriptions = CompositeSubscription()

	@SuppressLint("SourceLockedOrientationActivity")
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
		setContentView(layout)
	}

	private fun <T> getViewOnNavigater(idView: Int): T? {
		return navigationView?.findViewOnHeader<T>(idView)
	}

	@ExperimentalCoroutinesApi
	protected fun <T : AndroidViewModel?> createViewModel(
		classViewModel: Class<T>,
		activity: Activity,
	): T {
		val model = PopCornViewModelFactory(this.application, Api(baseContext), activity)
		return ViewModelProviders.of(this, model).get(classViewModel)
	}

	protected fun setUpToolBar() {
		val toolbar = findViewById<Toolbar>(R.id.toolbar)
		if (toolbar != null) {
			toolbar.setTitleTextColor(resources.getColor(Color.white))
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
		supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		navigationView?.itemIconTintList = null
		if (navigationView != null && drawerLayout != null) {
			val view = layoutInflater.inflate(R.layout.nav_drawer_header, navigationView)
			view.visible()
			navigationView?.setNavigationItemSelectedListener { item -> //Seleciona a Linha
				item.isChecked = true
				drawerLayout?.closeDrawers()
				onNavDrawerItemSelected(item)
				true
			}
		}
		validarNavDrawerComLogin()
	}

	private fun validarNavDrawerComLogin() {
		val auth = FirebaseAuth.getInstance()
		val grupoLogin = navigationView?.menu
		val user = auth.currentUser
		if (user == null) {
			login?.apply { setText(Txt.fazer_login) }
			grupoLogin?.removeGroup(R.id.menu_drav_logado)
			photoUser?.setImageResource(Drawable.add_user)
			photoUser?.setOnClickListener(onClickListenerLogar())
		} else {
			if (user.isAnonymous) {
				login?.setText(Txt.anonymous)
				userName?.setText(Txt.criar_login_popcorn)
				photoUser?.setImageResource(Drawable.add_user)
				grupoLogin?.setGroupVisible(R.id.menu_drav_logado, true)
			} else {
				grupoLogin?.setGroupVisible(R.id.menu_drav_logado, true)
				userName?.text = "dadadasda" //user.providerData[0]?.displayName ?: ""
				login?.text = "dadasdas Login" //user.providerData[0]?.email ?: ""
				photoUser.let {
					Picasso.get().load(user.photoUrl).placeholder(Drawable.person).into(it)
				}
			}
		}
		navigationView?.invalidate()
		navigationView?.requestLayout()
	}

	protected fun setCheckable(id: Int) {
		navigationView?.setCheckedItem(id)
	}

	private fun onNavDrawerItemSelected(menuItem: MenuItem) {
		val intent: Intent
		when (menuItem.itemId) {
			R.id.menu_drav_home -> {
				intent = Intent(this, MainActivity::class.java).apply {
					putExtra(Constant.ABA, R.id.menu_drav_home)
					addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
				}
				startActivity(intent)
			}
			R.id.nav_item_settings -> startActivity(Intent(this, SettingsActivity::class.java))
			R.id.favorite -> {
				intent = Intent(this, YourListActivity::class.java).apply {
					putExtra(Constant.ABA, getString(Txt.favorite))
					putExtra(Constant.MEDIATYPE, FAVORITY)
				}
				startActivity(intent)
			}
			R.id.rated -> {
				intent = Intent(this, YourListActivity::class.java).apply {
					putExtra(Constant.ABA, getString(Txt.avaliados))
					putExtra(Constant.MEDIATYPE, RATED)
				}
				startActivity(intent)
			}
			R.id.watchlist -> {
				intent = Intent(this, YourListActivity::class.java).apply {
					putExtra(Constant.ABA, getString(Txt.quero_assistir))
					putExtra(Constant.MEDIATYPE, WATCH)
				}
				startActivity(intent)
			}
			R.id.menu_drav_person -> startActivity(Intent(this, PersonPopularActivity::class.java))
			R.id.menu_drav_oscar -> {
				intent = Intent(this, ListGenericActivity::class.java).apply {
					putExtra(Constant.LISTA_ID, ListOnTheMovie.OSCAR)
					putExtra(Constant.LISTA_NOME, getString(Txt.oscar))
				}
				startActivity(intent)
			}
			R.id.menu_drav_surpresa -> parametrosDoRemoteConfig
			R.id.seguindo -> {
				intent = Intent(this, FallowingActivity::class.java).apply {
					putExtra(Constant.LISTA_ID, BuildConfig.VERSION_CODE - 1)
					putExtra(Constant.LISTA_NOME, Txt.oscar)
				}
				startActivity(intent)
			}
		}
	}

	fun ops() { makeToast(R.string.ops) }

	fun snack(anchor: View, txt: String = getString(R.string.no_internet), block: () -> Unit = {}) {
		Snackbar.make(anchor, txt, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) { block() }.show()
	}

	private val parametrosDoRemoteConfig: Unit
		get() { // TOOD remover essa configuração
			val mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance()
			fun getListRemote(): Map<String, String> {
				val map: MutableMap<String, String> = HashMap()
				for (i in 0..9) {
					map["id$i"] = mFirebaseRemoteConfig.getString("id$i")
					map["title$i"] = mFirebaseRemoteConfig.getString("title$i")
				}
				return map
			}

			mFirebaseRemoteConfig.setConfigSettingsAsync(Builder().build())
			mFirebaseRemoteConfig.setDefaultsAsync(R.xml.xml_defaults)
			var cacheExpiration: Long = 3600 // 1 hour in seconds.
			// If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
			// the server.
			if (BuildConfig.DEBUG) {
				cacheExpiration = 0
			}
			mFirebaseRemoteConfig.fetch(cacheExpiration)
				.addOnCompleteListener(this) { task ->
					if (task.isSuccessful) {
						mFirebaseRemoteConfig.fetchAndActivate()
					}
					val map = getListRemote()
					val number = Random().nextInt(10).toString()
					//Log.d(TAG, "numero : " + numero);
					//TODO mandar somente o Map e fazer a troca na outra activity
					val intent = Intent(this, ListGenericActivity::class.java).apply {
						putExtra(Constant.LISTA_ID, map["id$number"])
						putExtra(Constant.LISTA_NOME, map["title$number"])
						putExtra(Constant.BUNDLE, map as Serializable)
					}
					startActivity(intent)
				}
		}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
			id.home -> {
				if (drawerLayout != null) {
					openDrawer()
					return true
				}
			}
			R.id.search -> Dialogs.dialogSearch(this, item)

				// 	if (dialog == null) {
				// 		val view = layoutInflater.inflate(R.layout.layout_search_multi, null)
				// 		dialog =
				// 			Builder(this@BaseActivity, style.Theme_Translucent_NoTitleBar_Fullscreen)
				// 				.setView(view)
				// 				.setCancelable(true)
				// 				.create()
				// 	}
				// 	if (item.icon != null) item.icon.alpha = 10
				// 	dialog!!.show()
				// 	val searchViewDialog: SearchView =
				// 		dialog!!.findViewById(R.id.layout_search_multi_search)
				// 	searchViewDialog.setIconifiedByDefault(false)
				// 	searchViewDialog.isFocusable = true
				// 	searchViewDialog.isIconified = false
				// 	searchViewDialog.requestFocusFromTouch()
				// 	searchViewDialog.setOnCloseListener {
				// 		if (dialog != null && dialog!!.isShowing) dialog!!.dismiss()
				// 		false
				// 	}
				// 	dialog!!.setOnDismissListener { dialog: DialogInterface? ->
				// 		window.setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)
				// 		if (item.icon != null) item.icon.alpha = 255
				// 	}
				// 	searchViewDialog.setOnQueryTextListener(object : OnQueryTextListener {
				// 		override fun onQueryTextSubmit(query: String): Boolean {
				// 			if (item.icon != null) item.icon.alpha = 255
				// 			val intent = Intent(this@BaseActivity, SearchMultiActivity::class.java)
				// 			intent.putExtra(SearchManager.QUERY, query)
				// 			startActivity(intent)
				// 			return false
				// 		}
				//
				// 		override fun onQueryTextChange(newText: String): Boolean {
				// 			if (newText.isEmpty() || newText.length < 2) return false
				// 			val inscricao = Api(this@BaseActivity)
				// 				.procuraMulti(newText)
				// 				.distinctUntilChanged()
				// 				.debounce(1200, MILLISECONDS)
				// 				.subscribeOn(Schedulers.io())
				// 				.observeOn(AndroidSchedulers.mainThread())
				// 				.subscribe(object : Observer<MultiSearch?> {
				// 					override fun onCompleted() {}
				// 					override fun onError(e: Throwable) {
				// 						Toast.makeText(this@BaseActivity,
				// 							getString(Txt.ops),
				// 							Toast.LENGTH_SHORT).show()
				// 					}
				//
				// 					override fun onNext(multiRetorno: MultiSearch?) {
				// 						val recyclerView: RecyclerView =
				// 							dialog!!.findViewById(R.id.layout_search_multi_recycler)
				// 						recyclerView.setHasFixedSize(true)
				// 						recyclerView.layoutManager = LinearLayoutManager(application)
				// 						recyclerView.adapter = MultiSearchAdapter(this@BaseActivity,
				// 							multiRetorno!!,
				// 							item.icon)
				// 					}
				// 				})
				// 			subscriptions.add(inscricao)
				// 			return false
				// 		}
				// 	})
				// }
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
		callback: SalvarImageShare,
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

	protected fun onClickListenerLogar() = OnClickListener { view: View? ->
			startActivity(Intent(this@BaseActivity,
				LoginActivity::class.java))
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