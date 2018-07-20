package pessoa.activity

import activity.BaseActivity
import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.design.widget.TabLayout
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import br.com.icaro.filme.R
import domain.API
import domain.person.Person
import kotlinx.android.synthetic.main.activity_person.*
import kotlinx.android.synthetic.main.include_progress_horizontal.*
import pessoa.adapter.PersonAdapter
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import rx.subscriptions.CompositeSubscription
import utils.Constantes
import utils.UtilsApp

class PersonActivity : BaseActivity() {

    private var id_person: Int = 0
    private var nome: String? = null
    private val subscription: CompositeSubscription? = null
    //private lateinit var person: Person

    private val context: Context
        get() = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setUpToolBar()
        getExtras()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = nome

        if (UtilsApp.isNetWorkAvailable(context)) {
            getDados()
        } else {
            snack()
        }

    }

    private fun setTitleActionBar(title: String?){
        supportActionBar?.title = title
    }

    private fun getExtras() {
        if (intent.action == null) {
            nome = intent.getStringExtra(Constantes.NOME_PERSON)
            id_person = intent.getIntExtra(Constantes.PERSON_ID, 0)
        } else {
            nome = intent.getStringExtra(Constantes.NOME_PERSON)
            id_person = Integer.parseInt(intent.getStringExtra(Constantes.PERSON_ID))
        }
    }

    protected fun snack() {
        Snackbar.make(viewPager_person!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(context)) {
                        getDados()
                    } else {
                        snack()
                    }
                }.show()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {
            finish()
        }
        return true
    }

    private fun setupViewPagerTabs(person: Person) {

        viewPager_person?.offscreenPageLimit = 2
        viewPager_person?.adapter = PersonAdapter(context, supportFragmentManager, person)
        val tabLayout = findViewById<View>(R.id.tabLayout) as TabLayout
        viewPager_person?.currentItem = 2
        tabLayout.setupWithViewPager(viewPager_person)
        tabLayout.tabMode = TabLayout.MODE_SCROLLABLE

    }

    private fun getDados() {
        val inscricao = API(context = this).personDetalhes(id_person)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    setTitleActionBar(it.name)
                    setupViewPagerTabs(it)
                    progress_horizontal.visibility = View.GONE
                }, { erro ->
                    Toast.makeText(this, getString(R.string.ops), Toast.LENGTH_LONG).show()

                })
        subscription?.add(inscricao)

    }

    override fun onDestroy() {
        super.onDestroy()
        subscription?.unsubscribe()
    }
}
