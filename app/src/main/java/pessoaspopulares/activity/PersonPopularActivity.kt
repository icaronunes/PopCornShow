package pessoaspopulares.activity

import activity.BaseActivityAb
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_person_popular.activity_person_popular_no_internet
import kotlinx.android.synthetic.main.activity_person_popular.linear_person_popular
import kotlinx.android.synthetic.main.activity_person_popular.recycleView_person_popular
import pessoaspopulares.adapter.PersonPopularAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import utils.Api
import utils.InfiniteScrollListener
import utils.UtilsApp

/**
 * Created by icaro on 04/10/16.
 */
class PersonPopularActivity(override var layout: Int = Layout.activity_person_popular) : BaseActivityAb() {

    // val model:

    private var pagina = 1
    private var totalPagina = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.person_rated)

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            activity_person_popular_no_internet.visibility = View.GONE
        } else {
            activity_person_popular_no_internet.visibility = View.VISIBLE
        }

        recycleView_person_popular.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            val gridLayout = GridLayoutManager(this@PersonPopularActivity, 3)
            layoutManager = gridLayout
            addOnScrollListener(InfiniteScrollListener({ getPerson() }, gridLayout))
            recycleView_person_popular.adapter = PersonPopularAdapter()
        }

        if (UtilsApp.isNetWorkAvailable(this)) {
            getPerson()
        } else {
            snack()
        }
    }

    fun getPerson() {
        if (pagina <= totalPagina) {
            val inscricao = Api(this).personPopular(pagina)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        pagina = it?.page!!
                        totalPagina = it.totalPages!!
                        (recycleView_person_popular.adapter as PersonPopularAdapter)
                            .addPersonPopular(it.results ?: listOf())
                        ++this.pagina
                    }, { erro ->
                        Toast.makeText(this, resources.getString(R.string.ops), Toast.LENGTH_SHORT).show()
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
                    })
            // subscriptions.add(inscricao)
        }
    }

    private fun snack() {
        Snackbar.make(linear_person_popular, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        activity_person_popular_no_internet.visibility = View.GONE
                        getPerson()
                    } else {
                        snack()
                        activity_person_popular_no_internet.visibility = View.VISIBLE
                    }
                }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}