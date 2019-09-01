package producao

import activity.BaseActivity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import domain.Api
import domain.CrewItem
import kotlinx.android.synthetic.main.activity_crews.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp


class CrewsActivity : BaseActivity() {

    private var season = -100
    private var title: String? = null
    private var lista: List<CrewItem?>? = null
    private var id: Int = 0
    private var subscriptions = CompositeSubscription()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crews)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getExtras()
        crews_recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }
        
        setAdMob(adView)

        supportActionBar?.title = title

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            if (id == 0 || season == -100) {
                crews_recyclerview?.adapter = CrewsAdapter(this@CrewsActivity, lista)
                progress_horizontal.visibility = View.GONE
            } else {
                val inscricaoMovie = Api(context = this).getTvCreditosTemporada(id, season)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            crews_recyclerview?.adapter = CrewsAdapter(this@CrewsActivity, it.crew)
                            progress_horizontal.visibility = View.GONE
                        }, {
                            Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                        })

                subscriptions.add(inscricaoMovie)
            }

        } else {
            snack()
        }
    }


    private fun getExtras() {
        title = intent.getStringExtra(Constantes.NOME)
        lista = intent.getSerializableExtra(Constantes.PRODUCAO) as List<CrewItem?>?
        id = intent.getIntExtra(Constantes.ID, 0)
        season = intent.getIntExtra(Constantes.TVSEASONS, -100)
    }

    override fun onDestroy() {
        super.onDestroy()
        subscriptions.clear()
    }

    private fun snack() {
        Snackbar.make(linear_crews_layout!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        if (id == 0 && season == -100) {
                            crews_recyclerview?.adapter = CrewsAdapter(this@CrewsActivity, lista)
                            progress_horizontal.visibility = View.GONE
                        } else {
                            val inscricaoMovie = Api(context = this).getTvCreditosTemporada(id, season)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe({
                                        crews_recyclerview?.adapter = CrewsAdapter(this@CrewsActivity, it.crew)
                                        progress_horizontal.visibility = View.GONE
                                    }, {
                                        Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()
                                    })

                            subscriptions.add(inscricaoMovie)
                        }
                    } else {
                        snack()
                    }
                }.show()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

}
