package pessoaspopulares

import activity.BaseActivity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.view.MenuItem
import android.view.View
import br.com.icaro.filme.R
import domain.API
import kotlinx.android.synthetic.main.activity_person_popular.*
import pessoaspopulares.adapter.PersonPopularAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.InfiniteScrollListener
import utils.UtilsFilme

/**
 * Created by icaro on 04/10/16.
 */
class PersonPopularActivity : BaseActivity() {

    protected var subscriptions = CompositeSubscription()

    private var pagina = 1
    private var totalPagina = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person_popular)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setTitle(R.string.person_rated)

        if (UtilsFilme.isNetWorkAvailable(baseContext)) {
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

        /*     AdView adView = (AdView) findViewById(R.id.adView);
             AdRequest adRequest = new AdRequest.Builder()
                     .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                     .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                     .build();
             adView.loadAd(adRequest);*/

        if (UtilsFilme.isNetWorkAvailable(this)) {
            getPerson()

        } else {
            snack()
        }
    }

    fun getPerson() {
        if (pagina <= totalPagina) {
            val inscricao = API().PersonPopular(pagina)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        it.results?.forEach { resultsItem ->
                            pagina = it?.page!!
                            totalPagina = it?.totalPages!!
                        (recycleView_person_popular.adapter as PersonPopularAdapter).addPersonPopular(it.results)
                        }
                        ++this.pagina
                    }, { erro ->
                        Log.d(javaClass.simpleName, "Erro " + erro.message)
                    })
            subscriptions.add(inscricao)
        }
    }


    override fun onResume() {
        super.onResume()
        subscriptions = CompositeSubscription()
    }

    override fun onPause() {
        super.onPause()
        subscriptions.clear()
    }

    private fun snack() {
        Snackbar.make(linear_person_popular, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsFilme.isNetWorkAvailable(baseContext)) {
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

