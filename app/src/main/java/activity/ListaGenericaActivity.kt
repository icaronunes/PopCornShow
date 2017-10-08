package activity

import adapter.ListUserAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import br.com.icaro.filme.R
import domain.API
import kotlinx.android.synthetic.main.activity_lista.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.InfiniteScrollListener
import utils.UtilsApp
import java.util.*

/**
 * Created by icaro on 04/10/16.
 */
class ListaGenericaActivity : BaseActivity() {


    private lateinit var list_id: String
    private var totalPagina: Int = 1
    private var pagina = 1
    private lateinit var map: Map<String, String>
    private val TAG = this.javaClass.name


    private var subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(Constantes.LISTA_GENERICA)
        list_id = intent.getStringExtra(Constantes.LISTA_ID)
        if (intent.hasExtra(Constantes.BUNDLE)) {
            map =  HashMap()
            map = intent.getSerializableExtra(Constantes.BUNDLE) as Map<String, String>
        }
        recycleView_favorite.apply {
            val gridlayout = GridLayoutManager(this@ListaGenericaActivity, 3);
            layoutManager = gridlayout
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            addOnScrollListener(InfiniteScrollListener({ getLista() }, gridlayout))
            adapter = ListUserAdapter(this@ListaGenericaActivity)
        }

        //        AdView adview = (AdView) findViewById(R.id.adView);
        //        AdRequest adRequest = new AdRequest.Builder()
        //                .build();
        //        adview.loadAd(adRequest);

    }

    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
        if (UtilsApp.isNetWorkAvailable(this)) {
            getLista()
        }
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }


    fun getLista() {
        if (totalPagina >= pagina) {
            val inscricao = API(context = this).getLista(id = list_id, pagina = pagina)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        (recycleView_favorite.adapter as ListUserAdapter).addItens(it.results, it?.totalResults!!)
                        pagina = it.page!!
                        totalPagina = it.totalPages!!
                        ++pagina
                    }, { erro ->
                        Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
                    })
            subscriptions.add(inscricao)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when (item.itemId) {
            android.R.id.home -> finish()
            R.id.nova_lista -> {
                val numero = Random().nextInt(10).toString()
                supportActionBar?.title = map?.get("title$numero")
                list_id = map?.get("id$numero").toString()
                recycleView_favorite.adapter = ListUserAdapter(this)
                pagina = 1
                totalPagina = 1
                getLista()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.hasExtra(Constantes.BUNDLE) )
        menuInflater.inflate(R.menu.menu_random_lista, menu)

        return true
    }

}
