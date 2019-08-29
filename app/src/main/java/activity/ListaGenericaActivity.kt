package activity

import adapter.ListUserAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import br.com.icaro.filme.R
import domain.Api
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


    private lateinit var listId: String
    private var totalPagina: Int = 1
    private var pagina = 1
    private lateinit var map: Map<String, String>

    private var subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(Constantes.LISTA_GENERICA)
        listId = intent.getStringExtra(Constantes.LISTA_ID)
        
        if (intent.hasExtra(Constantes.BUNDLE)) {
            map = HashMap()
            map = intent.getSerializableExtra(Constantes.BUNDLE) as Map<String, String>
        }
        createRecyler()
    }
    
    private fun createRecyler() {
        recycleView_favorite.apply {
            val gridlayout = GridLayoutManager(this@ListaGenericaActivity, 3)
            layoutManager = gridlayout
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            addOnScrollListener(InfiniteScrollListener({ getLista() }, gridlayout))
            adapter = ListUserAdapter(this@ListaGenericaActivity)
        }
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

    private fun getLista() {
        if (totalPagina >= pagina) {
            val inscricao = Api(context = this).getLista(id = listId, pagina = pagina)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ listaFilmes ->
                        val itens = listaFilmes.results.sortedBy { it?.releaseDate }
                                .reversed()
                        (recycleView_favorite.adapter as ListUserAdapter)
                                .addItens(itens, listaFilmes?.totalResults!!)
                        pagina = listaFilmes.page
                        totalPagina = listaFilmes.totalPages
                        ++pagina
                    }, {
                        Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                    })
            subscriptions.add(inscricao)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
            R.id.nova_lista -> {
                createRecyler()
                val numero = Random().nextInt(10).toString()
                supportActionBar?.title = map["title$numero"]
                listId = map["id$numero"].toString()
                pagina = 1
                totalPagina = 1
                getLista()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.hasExtra(Constantes.BUNDLE))
            menuInflater.inflate(R.menu.menu_random_lista, menu)

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if(subscriptions.hasSubscriptions()) subscriptions.clear()
    }

}
