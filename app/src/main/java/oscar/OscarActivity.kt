package oscar

import activity.BaseActivity
import adapter.ListUserAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import br.com.icaro.filme.R
import domain.API
import kotlinx.android.synthetic.main.activity_lista.*
import pessoaspopulares.InfiniteScrollListener
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.UtilsFilme

/**
 * Created by icaro on 04/10/16.
 */
class OscarActivity : BaseActivity() {

    private var list_id = 28
    private var pagina = 1
    private var totalPagina = 1
    private val TAG = this.javaClass.name

    private var subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.oscar)

        recycleView_favorite.apply {
            val gridLayout = GridLayoutManager(this@OscarActivity, 3)
            layoutManager = gridLayout
            itemAnimator = DefaultItemAnimator()
            addOnScrollListener(InfiniteScrollListener({ getOscar() }, gridLayout))
            setHasFixedSize(true)
            recycleView_favorite.adapter = ListUserAdapter(this@OscarActivity)
        }

        //        AdView adview = (AdView) findViewById(R.id.adView);
        //        AdRequest adRequest = new AdRequest.Builder()
        //                .build();
        //        adview.loadAd(adRequest);

    }


    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
        if (UtilsFilme.isNetWorkAvailable(baseContext)) {
            getOscar()
        } else {
            snack()
        }
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }

    protected fun snack() {
        Snackbar.make(linear_lista, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsFilme.isNetWorkAvailable(baseContext)) {
                        getOscar()
                    } else {
                        snack()
                    }
                }.show()
    }

    fun getOscar() {
        if (totalPagina >= pagina) {
            val inscricao = API().OscarLista(id = list_id, pagina = pagina)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        (recycleView_favorite.adapter as ListUserAdapter).addPersonPopular(it)
                        pagina = it?.page!!
                        totalPagina = it?.totalPages!!
                        ++pagina
                    }, { erro ->
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
                    })
            subscriptions.add(inscricao)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

//    private inner class TMDVAsync : AsyncTask<Void, Void, Void>() {
//
//        override fun doInBackground(vararg voids: Void): Void? {
//            if (listaJava == null) {
//                try {
//                    listaJava = FilmeService.getLista(list_id)
//                    //Metodos criados. Tudo gambiara. Precisa arrumar
//                    if (listaJava != null) {
//                        Collections.sort<ItemsLista>(listaJava!!.getItems())
//                    }
//                } catch (e: Exception) {
//                    FirebaseCrash.report(e)
//                    // Log.d(TAG, e.getMessage());
//                    runOnUiThread { Toast.makeText(this@OscarActivity, R.string.ops, Toast.LENGTH_SHORT).show() }
//                }
//
//            }
//
//            return null
//        }
//
//        override fun onPostExecute(aVoid: Void) {
//            super.onPostExecute(aVoid)
//            progressBar!!.visibility = View.GONE
//            recyclerView!!.adapter = ListUserAdapter(this@OscarActivity,
//                    if (listaJava != null) listaJava!!.items else null)
//        }
//    }
}
