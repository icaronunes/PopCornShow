package pessoaspopulares.activity

import Layout
import activity.BaseActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_person_popular.activity_person_popular_no_internet
import kotlinx.android.synthetic.main.activity_person_popular.linear_person_popular
import kotlinx.android.synthetic.main.activity_person_popular.recycleView_person_popular
import lista.viewmodel.ListByTypeViewModel
import pessoaspopulares.adapter.PersonPopularAdapter
import utils.InfiniteScrollListener
import utils.UtilsApp
import utils.gone
import utils.visible

/**
 * Created by icaro on 04/10/16.
 */
class PersonPopularActivity(override var layout: Int = Layout.activity_person_popular) :
	BaseActivity() {
	val model: ListByTypeViewModel by lazy {
		createViewModel(ListByTypeViewModel::class.java,
            this)
	}
	private var pagina = 1
	private var totalPagina = 1
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(layout)
		setUpToolBar()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.setTitle(R.string.person_rated)

		if (UtilsApp.isNetWorkAvailable(baseContext)) {
			activity_person_popular_no_internet.gone()
		} else {
			activity_person_popular_no_internet.visible()
		}

		setupRecycler()
		observer()

		if (UtilsApp.isNetWorkAvailable(this)) {
			fetchPerson()
		} else {
			snackPerson()
		}
	}

	private fun setupRecycler() {
		recycleView_person_popular.apply {
			setHasFixedSize(true)
			itemAnimator = DefaultItemAnimator()
			val gridLayout = GridLayoutManager(this@PersonPopularActivity, 3)
			layoutManager = gridLayout
			addOnScrollListener(InfiniteScrollListener({ fetchPerson() }, gridLayout))
			recycleView_person_popular.adapter = PersonPopularAdapter()
		}
	}

	private fun observer() {
		model.personList.observe(this, Observer {
            when (it) {
                is Success -> {
                    val person = it.result
                    pagina = person.page
                    totalPagina = person.totalPages
                    (recycleView_person_popular.adapter as PersonPopularAdapter)
                        .addPersonPopular(person.results ?: listOf())
                    ++this.pagina
                }
                is Failure -> ops()
            }
        })
	}

	private fun fetchPerson() { model.fetchPerson(pagina) }

	private fun snackPerson() {
		Snackbar.make(linear_person_popular, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(baseContext)) {
					activity_person_popular_no_internet.visibility = View.GONE
					fetchPerson()
				} else {
					snackPerson()
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
