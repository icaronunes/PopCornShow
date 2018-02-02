package filme.activity

import activity.BaseActivity
import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.app.Dialog
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.design.widget.CollapsingToolbarLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.widget.Button
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import br.com.icaro.filme.R
import com.crashlytics.android.Crashlytics
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import domain.API
import domain.FilmeDB
import domain.FilmeService
import domain.Movie
import filme.fragment.FilmeInfoFragment
import fragment.ImagemTopFilmeScrollFragment
import kotlinx.android.synthetic.main.activity_filme.*
import kotlinx.android.synthetic.main.fab_float.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp
import java.io.File
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class FilmeActivity : BaseActivity() {
    private var color_fundo: Int = 0
    private var id_filme: Int = 0
    private var movieDb: Movie? = null
    private var addFavorite = true
    private var addWatch = true
    private var addRated = true

    private var mAuth: FirebaseAuth? = null

    private var myWatch: DatabaseReference? = null
    private var myFavorite: DatabaseReference? = null
    private var myRated: DatabaseReference? = null

    private var valueEventWatch: ValueEventListener? = null
    private var valueEventRated: ValueEventListener? = null
    private var valueEventFavorite: ValueEventListener? = null

    private var numero_rated: Float = 0.0f

    private var subscriptions = CompositeSubscription()
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filme)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        setupNavDrawer()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitle(" ")
        getExtras()


        top_img_viewpager.apply {
            setBackgroundColor(color_fundo)
            offscreenPageLimit = 3
        }


        iniciarFirebases()
        subscriptions = CompositeSubscription()

        if (UtilsApp.isNetWorkAvailable(this)) {
            getDados()
        } else {
            snack()
        }

    }

    private fun getDados() {

        val inscricaoMovie = API(context = this).loadMovieComVideo(id_filme)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    movieDb = it
                    title = movieDb?.title
                    top_img_viewpager?.adapter = ImagemTopFragment(supportFragmentManager)
                    progress_horizontal?.visibility = View.GONE
                    setFAB()
                    setFragmentInfo()
                }, { erro ->
                    Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                })

        subscriptions.add(inscricaoMovie)

    }

    private fun setEventListenerWatch() {

        valueEventWatch = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                if (dataSnapshot.child(id_filme.toString()).exists()) {
                    addWatch = true
                    menu_item_watchlist?.labelText = resources.getString(R.string.remover_watch)
                } else {
                    addWatch = false
                    menu_item_watchlist?.labelText = resources.getString(R.string.adicionar_watch)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        myWatch?.addValueEventListener(valueEventWatch)

    }

    private fun setEventListenerRated() {
        valueEventRated = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(id_filme.toString()).exists()) {
                    addRated = true

                    if (dataSnapshot.child(id_filme.toString()).child("nota").exists()) {
                        val nota = dataSnapshot.child(id_filme.toString()).child("nota").value.toString()
                        numero_rated = java.lang.Float.parseFloat(nota)
                        menu_item_rated?.labelText = resources.getString(R.string.remover_rated)
                        if (numero_rated == 0.0f) {
                            menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
                        }
                    }

                } else {
                    addRated = false
                    numero_rated = 0.0f
                    menu_item_rated?.labelText = resources.getString(R.string.adicionar_rated)
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        myRated?.addValueEventListener(valueEventRated)

    }

    private fun setEventListenerFavorite() {
        valueEventFavorite = object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                if (dataSnapshot.child(id_filme.toString()).exists()) {
                    addFavorite = true
                    menu_item_favorite?.labelText = resources.getString(R.string.remover_favorite)
                } else {
                    addFavorite = false
                    menu_item_favorite?.labelText = resources.getString(R.string.adicionar_favorite)

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {

            }
        }
        myFavorite?.addValueEventListener(valueEventFavorite)
    }

    private fun iniciarFirebases() {

        mAuth = FirebaseAuth.getInstance()
        val database = FirebaseDatabase.getInstance()

        if (mAuth?.currentUser != null) {

            myWatch = database.getReference("users").child(mAuth?.currentUser
                    ?.uid).child("watch")
                    ?.child("movie")

            myFavorite = database.getReference("users").child(mAuth?.currentUser
                    ?.uid).child("favorites")
                    ?.child("movie")

            myRated = database.getReference("users").child(mAuth?.currentUser
                    ?.uid).child("rated")
                    ?.child("movie")
        }
    }

    private fun getExtras() {
        if (intent.action == null) {
            id_filme = intent.getIntExtra(Constantes.FILME_ID, 0)
            color_fundo = intent.getIntExtra(Constantes.COLOR_TOP, R.color.transparent)
        } else {
            id_filme = Integer.parseInt(intent.getStringExtra(Constantes.FILME_ID))
            color_fundo = Integer.parseInt(intent.getStringExtra(Constantes.COLOR_TOP))
        }
    }

    private fun setFAB() {
        if (mAuth?.currentUser != null) {

            setEventListenerFavorite()
            setEventListenerRated()
            setEventListenerWatch()

            fab_menu_filme?.alpha = 1.0f
            setColorFab(color_fundo)
            menu_item_favorite?.setOnClickListener(addOrRemoveFavorite())
            menu_item_rated?.setOnClickListener(RatedFilme())
            menu_item_watchlist?.setOnClickListener(addOrRemoveWatch())

        } else {
            fab_menu_filme?.alpha = 0.0f
        }
    }

    private fun snack() {
        Snackbar.make(top_img_viewpager, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        getDados()
                    } else {
                        snack()
                    }
                }.show()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        try {
            menuInflater.inflate(R.menu.menu_share, menu)

            val searchView = menu.findItem(R.id.search).actionView as SearchView
            val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
            searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
            searchView.queryHint = resources.getString(R.string.procurar)

            searchView.isEnabled = false
        } catch (e: Exception) {
            Crashlytics.logException(e);
            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show()
        }

        return true
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == R.id.share) {
            if (movieDb != null){

            salvaImagemMemoriaCache(this@FilmeActivity, movieDb?.posterPath, object : SalvarImageShare {
                override fun retornaFile(file: File) {
                    val intent = Intent(Intent.ACTION_SEND)
                    intent.type = "message/rfc822"
                    intent.putExtra(Intent.EXTRA_TEXT, movieDb?.title + " " + buildDeepLink() + " by: " + Constantes.TWITTER_URL)
                    intent.type = "image/*"
                    intent.putExtra(Intent.EXTRA_STREAM, UtilsApp.getUriDownloadImage(this@FilmeActivity, file))
                    startActivity(Intent.createChooser(intent, resources.getString(R.string.compartilhar_filme)))
                }

                override fun RetornoFalha() {
                    Toast.makeText(this@FilmeActivity, resources.getString(R.string.erro_na_gravacao_imagem), Toast.LENGTH_SHORT).show()
                }
            })

        } else {
                Toast.makeText(this@FilmeActivity, resources.getString(R.string.erro_ainda_sem_imagem), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    fun buildDeepLink(): String {
        // Get the unique appcode for this app.

        val link = "https://q2p5q.app.goo.gl/?link=https://br.com.icaro.filme/?action%3DFA%26id%3D${(movieDb?.id)}&apn=br.com.icaro.filme"

        // If the deep link is used in an advertisement, this value must be set to 1.
        val isAd = false
        if (isAd) {
            // builder.appendQueryParameter("ad", "1");
        }

        // Minimum version is optional.
        //        int minVersion = ;
        //        if (minVersion > 16) {
        //            builder.appendQueryParameter("amv", Integer.toString(minVersion));
        //        }

        //        if (!TextUtils.isEmpty(androidLink)) {
        //            builder.appendQueryParameter("al", androidLink);
        //        }
        //
        //        if (!TextUtils.isEmpty(playStoreAppLink)) {
        //            builder.appendQueryParameter("afl", playStoreAppLink);
        //        }
        //
        //        if (!customParameters.isEmpty()) {
        //            for (Map.Entry<String, String> parameter : customParameters.entrySet()) {
        //                builder.appendQueryParameter(parameter.getKey(), parameter.getValue());
        //            }
        //        }

        // Return the completed deep link.
        //        Log.d(TAG, builder.build().toString());
        //        return builder.build().toString();
        return link
    }


    fun RatedFilme(): View.OnClickListener {
        return View.OnClickListener {
            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                date = sdf.parse(movieDb?.releaseDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@FilmeActivity, getString(R.string.filme_nao_lancado), Toast.LENGTH_SHORT).show()
            } else {
                val alertDialog = Dialog(this@FilmeActivity)
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
                alertDialog.setContentView(R.layout.adialog_custom_rated)

                val ok = alertDialog.findViewById<View>(R.id.ok_rated) as Button
                val no = alertDialog.findViewById<View>(R.id.cancel_rated) as Button
                val title = alertDialog.findViewById<View>(R.id.rating_title) as TextView
                title.text = movieDb?.title
                val ratingBar = alertDialog.findViewById<View>(R.id.ratingBar_rated) as RatingBar
                ratingBar.rating = numero_rated
                val width = resources.getDimensionPixelSize(R.dimen.popup_width) //Criar os Dimen do layout do login - 300dp - 300dp ??
                val height = resources.getDimensionPixelSize(R.dimen.popup_height_rated)

                alertDialog.window?.setLayout(width, height) //????????????
                alertDialog.show()

                if (addRated) {
                    no.visibility = View.VISIBLE
                } else {
                    no.visibility = View.GONE
                }

                no.setOnClickListener {

                    myRated?.child(id_filme.toString())?.setValue(null)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@FilmeActivity,
                                        resources.getText(R.string.remover_rated), Toast.LENGTH_SHORT).show()
                            }
                    alertDialog.dismiss()
                    fab_menu_filme.close(true)
                }

                ok.setOnClickListener(View.OnClickListener {


                    if (UtilsApp.isNetWorkAvailable(this@FilmeActivity)) {

                        if (ratingBar.rating == 0.0f) {
                            alertDialog.dismiss()
                            return@OnClickListener
                        }

                        val filmeDB = FilmeDB()
                        filmeDB.id = movieDb?.id!!
                        filmeDB.idImdb = movieDb?.imdbId
                        filmeDB.title = movieDb?.title
                        filmeDB.nota = ratingBar.rating.toInt()
                        filmeDB.poster = movieDb?.posterPath

                        myRated?.child(id_filme.toString())?.setValue(filmeDB)
                                ?.addOnCompleteListener {
                                    Toast.makeText(this@FilmeActivity, resources.getString(R.string.filme_rated), Toast.LENGTH_SHORT)
                                            .show()

                                    fab_menu_filme?.close(true)
                                }
                        Thread(Runnable { FilmeService.ratedMovieGuest(id_filme, ratingBar.rating.toInt(), this@FilmeActivity) }).start()
                    }
                    alertDialog.dismiss()
                })

            }
        }
    }

    private fun setColorFab(color: Int) {
        fab_menu_filme?.menuButtonColorNormal = color
        menu_item_favorite?.colorNormal = color
        menu_item_watchlist?.colorNormal = color
        menu_item_rated?.colorNormal = color
    }

    private fun addOrRemoveFavorite(): View.OnClickListener {
        return View.OnClickListener {
            val anim1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.2f)
            val anim2 = PropertyValuesHolder.ofFloat("scaley", 1.0f, 0.2f)
            val anim3 = PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat("scaley", 0.0f, 1.0f)
            val animator = ObjectAnimator
                    .ofPropertyValuesHolder(menu_item_favorite, anim1, anim2, anim3, anim4)
            animator.duration = 1600
            animator.start()


            var date: Date? = null
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                date = sdf.parse(movieDb?.releaseDate)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (!UtilsApp.verificaLancamento(date)) {
                Toast.makeText(this@FilmeActivity, R.string.filme_nao_lancado, Toast.LENGTH_SHORT).show()

            } else {

                if (addFavorite) {
                    //  Log.d(TAG, "Apagou Favorite");
                    myFavorite?.child(id_filme.toString())?.setValue(null)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@FilmeActivity, getString(R.string.filme_remove_favorite), Toast.LENGTH_SHORT).show()

                                fab_menu_filme?.close(true)
                            }
                } else {

                    val filmeDB = FilmeDB()
                    filmeDB.id = movieDb?.id!!
                    filmeDB.idImdb = movieDb?.imdbId
                    filmeDB.title = movieDb?.title
                    filmeDB.poster = movieDb?.posterPath

                    myFavorite?.child(id_filme.toString())?.setValue(filmeDB)
                            ?.addOnCompleteListener {
                                Toast.makeText(this@FilmeActivity, getString(R.string.filme_add_favorite), Toast.LENGTH_SHORT)
                                        .show()

                                fab_menu_filme?.close(true)
                            }
                }
            }
        }
    }

    private fun addOrRemoveWatch(): View.OnClickListener {
        return View.OnClickListener {
            val anim1 = PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f)
            val anim2 = PropertyValuesHolder.ofFloat("scaley", 1.0f, 0.0f)
            val anim3 = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1.0f)
            val anim4 = PropertyValuesHolder.ofFloat("scaley", 0.5f, 1.0f)
            val animator = ObjectAnimator
                    .ofPropertyValuesHolder(menu_item_watchlist, anim1, anim2, anim3, anim4)
            animator.duration = 1650
            animator.start()

            if (addWatch) {

                myWatch?.child(id_filme.toString())?.setValue(null)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@FilmeActivity, getString(R.string.filme_remove), Toast.LENGTH_SHORT).show()

                            fab_menu_filme?.close(true)
                        }


            } else {

                val filmeDB = FilmeDB()
                filmeDB.idImdb = movieDb?.imdbId
                filmeDB.id = movieDb?.id!!
                filmeDB.title = movieDb?.title
                filmeDB.poster = movieDb?.posterPath

                myWatch?.child(id_filme.toString())?.setValue(filmeDB)
                        ?.addOnCompleteListener {
                            Toast.makeText(this@FilmeActivity, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                    .show()

                            fab_menu_filme?.close(true)
                        }

            }
        }
    }

    private fun setTitle(title: String) {
        val collapsingToolbarLayout = findViewById<View>(R.id.collapsing_toolbar) as CollapsingToolbarLayout
        collapsingToolbarLayout.title = title
    }

    private fun setFragmentInfo() {

        val filmeFrag = FilmeInfoFragment()
        val bundle = Bundle()
        bundle.putSerializable(Constantes.FILME, movieDb)
        filmeFrag.arguments = bundle

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            if (!isDestroyed && !isFinishing /*&& tmdvAsync != null*/) { //Isdestroyed valido apenas acima desta api
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.filme_container, filmeFrag, null)
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .commitAllowingStateLoss()
            }
        } else {
            if (!isFinishing) {
                supportFragmentManager
                        .beginTransaction()
                        .add(R.id.filme_container, filmeFrag, null)
                        .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                        .commitAllowingStateLoss()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        if (valueEventWatch != null) {
            myWatch?.removeEventListener(valueEventWatch)
        }
        if (valueEventRated != null) {
            myRated?.removeEventListener(valueEventRated)
        }
        if (valueEventFavorite != null) {
            myFavorite?.removeEventListener(valueEventFavorite)
        }

        subscriptions.clear()
    }

    private inner class ImagemTopFragment(supportFragmentManager: FragmentManager) : FragmentPagerAdapter(supportFragmentManager) {

        override fun getItem(position: Int): Fragment? {
            return if (movieDb?.images?.backdrops != null) {
                if (position == 0) {
                    ImagemTopFilmeScrollFragment.newInstance(movieDb?.backdropPath)
                } else ImagemTopFilmeScrollFragment.newInstance(movieDb?.images?.backdrops!![position]?.filePath)
            } else null
        }

        override fun getCount(): Int {
            if (movieDb?.images?.backdrops != null) {

                val tamanho = movieDb?.images?.backdrops?.size
                return if (tamanho!! > 0) tamanho else 1 // ???????????????? tamahao vai ser nulo?
            }
            return 0
        }
    }

}