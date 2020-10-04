package oscar

import activity.BaseActivity
import activity.BaseActivityAb
import adapter.ListUserAdapter
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_lista.linear_lista
import kotlinx.android.synthetic.main.activity_lista.recycleView_favorite
import listafilmes.viewmodel.ListByTypeViewModel
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Api
import utils.BaseActivityKt
import utils.Constant
import utils.InfiniteScrollListener
import utils.UtilsApp

/**
 * Created by icaro on 04/10/16.
 */
class OscarActivity(override var layout: Int = R.layout.activity_lista) : BaseActivityAb() {

    val model: ListByTypeViewModel by lazy { createViewModel(ListByTypeViewModel::class.java, this) }

    private val listId = Constant.ListOnTheMovie.OSCAR
    private var pagina = 1
    private var totalPagina = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
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
        observers()
    }
    private fun observers() {
        model.moviesList.observe(this, Observer {
            when(it) {
                is Success -> {
                    model.setLoadingMovieId(false)
                    val result = it.result
                    (recycleView_favorite.adapter as ListUserAdapter).addItens(result.results, result.totalResults)
                    pagina = result.page
                    totalPagina = result.totalPages
                    ++pagina
                }
                is Failure -> {
                    ops()
                    model.setLoadingMovieId(false)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            getOscar()
        } else {
            snack()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun snack() {
        Snackbar.make(linear_lista, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
            .setAction(R.string.retry) {
                if (UtilsApp.isNetWorkAvailable(baseContext)) {
                    getOscar()
                } else {
                    snack()
                }
            }.show()
    }

    private fun getOscar() { model.fetchListById(listId, pagina) }
}
