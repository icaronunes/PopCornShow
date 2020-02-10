package pessoa.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.lifecycle.Observer
import applicaton.BaseViewModel.BaseRequest.Failure
import applicaton.BaseViewModel.BaseRequest.Success
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import domain.person.Person
import kotlinx.android.synthetic.main.activity_person.viewPager_person
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal as progress
import pessoa.adapter.PersonAdapter
import pessoa.modelview.PersonViewModel
import utils.BaseActivityKt
import utils.CallBackError
import utils.Constantes
import utils.UtilsApp
import utils.gone

class PersonActivity : BaseActivityKt(), CallBackError {

    lateinit var person: Person
    private var idPerso: Int = 0
    private var nome: String? = null
    private val model by lazy { createViewModel(PersonViewModel::class.java, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_person)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        getExtras()
        observers()

        if (UtilsApp.isNetWorkAvailable(context = baseContext)) {
            fetchData(idPerso)
        } else {
            snack()
        }
    }

    private fun getExtras() {
        if (intent.action == null) {
            nome = intent.getStringExtra(Constantes.NOME_PERSON)
            idPerso = intent.getIntExtra(Constantes.PERSON_ID, 0)
        } else {
            nome = intent.getStringExtra(Constantes.NOME_PERSON)
            idPerso = Integer.parseInt(intent.getStringExtra(Constantes.PERSON_ID))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setTitleActionBar(title: String?) { supportActionBar?.title = title ?: "" }

    private fun snack() {
        Snackbar.make(viewPager_person!!, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry) {
                    if (UtilsApp.isNetWorkAvailable(baseContext)) {
                        fetchData(idPerso)
                    } else {
                        snack()
                    }
                }.show()
    }

    private fun setupViewPagerTabs() {
        viewPager_person?.apply {
            offscreenPageLimit = 2
            adapter = PersonAdapter(baseContext, supportFragmentManager, false)
            currentItem = 2
        }
        findViewById<TabLayout>(R.id.tabLayout).run {
            setupWithViewPager(viewPager_person)
            tabMode = TabLayout.MODE_SCROLLABLE
        }
    }

    private fun setupViewPagerTabsWithError() {
        viewPager_person?.adapter = PersonAdapter(baseContext, supportFragmentManager, true)
        findViewById<TabLayout>(R.id.tabLayout).apply {
            setupWithViewPager(viewPager_person)
            tabMode = TabLayout.MODE_SCROLLABLE
        }.gone()
    }

    private fun fetchData(idPerson: Int) { model.getPersonDate(idPerson) }

    private fun observers() {
        model.response.observe(this, Observer {
            when (it) {
                is Success -> {
                    person = it.result as Person
                    setTitleActionBar(person.name)
                    setupViewPagerTabs()
                    progress.gone()
                }
                is Failure -> {
                    ops()
                    progress.gone()
                    setupViewPagerTabsWithError()
                }
                else -> {
                }
            }

        })
    }

    override fun onDestroy() {
        model.response.removeObservers(this)
        super.onDestroy()
    }

    override fun tryAgain() { fetchData(idPerso) }
}
