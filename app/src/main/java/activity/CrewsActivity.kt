package activity

import adapter.CrewsAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import br.com.icaro.filme.R
import domain.CrewItem
import kotlinx.android.synthetic.main.activity_crews.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import utils.Constantes
import utils.UtilsApp


class CrewsActivity : BaseActivity() {

    private var season = -100
    private var title: String? = null
    private var lista: List<CrewItem?>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crews)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getExtras()
        crews_recyclerview.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
        }

        supportActionBar?.title = title

        if (UtilsApp.isNetWorkAvailable(baseContext)) {
            crews_recyclerview?.adapter = CrewsAdapter(this@CrewsActivity, lista)
            progress_horizontal.visibility = View.GONE
        } else {
            snack()
        }

    }

    private fun getExtras() {

        title = intent.getStringExtra(Constantes.NOME)
        lista = intent.getSerializableExtra(Constantes.PRODUCAO) as List<CrewItem?>?

    }

    private fun snack() {
        Snackbar.make(linear_crews_layout!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        crews_recyclerview?.adapter = CrewsAdapter(this@CrewsActivity, lista)
                        progress_horizontal.visibility = View.GONE
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
