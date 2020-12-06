package pessoa.activity

import activity.BaseActivity
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
import pessoa.adapter.PersonAdapter
import pessoa.modelview.PersonViewModel
import utils.Constant
import utils.UtilsApp
import utils.gone
import utils.kotterknife.bindBundle
import kotlinx.android.synthetic.main.include_progress_horizontal.progress_horizontal as progress

class PersonActivity(override var layout: Int = Layout.activity_person) : BaseActivity() {

    private val idPerso: Int by bindBundle(Constant.ID, 0)
    private val nome: String by bindBundle(Constant.NOME_PERSON, "")
    private val model by lazy { createViewModel(PersonViewModel::class.java, this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpToolBar()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setTitleActionBar("")
        observers()

        if (UtilsApp.isNetWorkAvailable(context = baseContext)) {
            fetchData(idPerso)
        } else {
            snack()
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
            adapter = PersonAdapter(baseContext, supportFragmentManager)
            currentItem = 2
        }
        findViewById<TabLayout>(R.id.tabLayout).run {
            setupWithViewPager(viewPager_person)
            tabMode = TabLayout.MODE_SCROLLABLE
        }
    }

    private fun setupViewPagerTabsWithError() {
        viewPager_person?.adapter = PersonAdapter(baseContext, supportFragmentManager)
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
                    setTitleActionBar((it.result as Person).name)
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
}
