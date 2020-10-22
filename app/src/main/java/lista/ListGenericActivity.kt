package lista

import activity.BaseActivityAb
import adapter.ListUserAdapter
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import applicaton.BaseViewModel.BaseRequest.*
import br.com.icaro.filme.R
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_lista.linear_lista
import kotlinx.android.synthetic.main.activity_lista.recycleView_favorite
import lista.viewmodel.ListByTypeViewModel
import utils.Constant
import utils.InfiniteScrollListener
import utils.UtilsApp
import utils.kotterknife.bindBundle
import java.util.Random

/**
 * Created by icaro on 04/10/16.
 */
class ListGenericActivity(override var layout: Int = R.layout.activity_lista) : BaseActivityAb() {
	val model: ListByTypeViewModel by lazy {
		createViewModel(ListByTypeViewModel::class.java,
            this)
	}
	private lateinit var listId: String
	private val title: String by bindBundle(Constant.LISTA_NOME)
	private lateinit var map: Map<String, String>
	private var pagina = 1
	private var totalPagina = 1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(layout)
		setUpToolBar()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = title
		listId = intent.getStringExtra(Constant.LISTA_ID) ?: ""
		if (intent.hasExtra(Constant.BUNDLE)) {
			map = HashMap()
			map = intent.getSerializableExtra(Constant.BUNDLE) as Map<String, String>
		}
		setupRecycler()
		observers()
	}

	private fun setupRecycler() {
		recycleView_favorite.apply {
			val gridLayout = GridLayoutManager(this@ListGenericActivity, 3)
			layoutManager = gridLayout
			itemAnimator = DefaultItemAnimator()
			addOnScrollListener(InfiniteScrollListener({ getList() }, gridLayout))
			setHasFixedSize(true)
			recycleView_favorite.adapter = ListUserAdapter(this@ListGenericActivity)
		}
	}

	private fun observers() {
		model.moviesList.observe(this, Observer {
            when (it) {
                is Success -> {
                    model.setLoadingMovieId(false)
                    val result = it.result
                    (recycleView_favorite.adapter as ListUserAdapter).addItens(result.results,
                        result.totalResults)
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
			getList()
		} else {
			snack()
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
            R.id.nova_lista -> {
                setupRecycler()
                val numero = Random().nextInt(10).toString()
                supportActionBar?.title = map["title$numero"]
                listId = map["id$numero"].toString()
                pagina = 1
                totalPagina = 1
                getList()
            }
		}
		return super.onOptionsItemSelected(item)
	}

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        if (intent.hasExtra(Constant.BUNDLE))
            menuInflater.inflate(R.menu.menu_random_lista, menu)
        return true
    }

	private fun snack() {
		Snackbar.make(linear_lista, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
			.setAction(R.string.retry) {
				if (UtilsApp.isNetWorkAvailable(baseContext)) {
					getList()
				} else {
					snack()
				}
			}.show()
	}

	private fun getList() {
		model.fetchListById(listId, pagina)
	}
}
