package activity

import adapter.ListUserAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import br.com.icaro.filme.R
import utils.Api
import java.util.Random
import kotlin.collections.HashMap
import kotlinx.android.synthetic.main.activity_lista.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constant
import utils.InfiniteScrollListener
import utils.UtilsApp

/**
 * Created by icaro on 04/10/16.
 */
class ListGenericActivity : BaseActivity() {

    private lateinit var listId: String
    private var totalPagina: Int = 1
    private var pagina = 1
    private lateinit var map: Map<String, String>

    private var subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = intent.getStringExtra(Constant.LISTA_GENERICA)
        listId = intent.getStringExtra(Constant.LISTA_ID)

        if (intent.hasExtra(Constant.BUNDLE)) {
            map = HashMap()
            map = intent.getSerializableExtra(Constant.BUNDLE) as Map<String, String>
        }
        createRecyler()
    }

    private fun createRecyler() {
        recycleView_favorite.apply {
            val gridlayout = GridLayoutManager(this@ListGenericActivity, 3)
            layoutManager = gridlayout
            itemAnimator = DefaultItemAnimator()
            setHasFixedSize(true)
            addOnScrollListener(InfiniteScrollListener({ getLista() }, gridlayout))
            adapter = ListUserAdapter(this@ListGenericActivity)
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
        if (intent.hasExtra(Constant.BUNDLE))
            menuInflater.inflate(R.menu.menu_random_lista, menu)

        return true
    }

    override fun onDestroy() {
        super.onDestroy()
        if (subscriptions.hasSubscriptions()) subscriptions.clear()
    }
}
