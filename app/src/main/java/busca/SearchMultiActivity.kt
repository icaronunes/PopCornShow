package busca

import Layout
import activity.BaseActivity
import android.app.SearchManager
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseViewModel.*
import br.com.icaro.filme.R
import busca.adapter.SearchDelegateAdapter
import domain.search.SearchMulti
import kotlinx.android.synthetic.main.include_progress.progress
import kotlinx.android.synthetic.main.search_layout.adView
import kotlinx.android.synthetic.main.search_layout.text_search_empty
import utils.InfiniteScrollListener
import utils.UtilsApp.isNetWorkAvailable
import utils.gone
import utils.kotterknife.bindBundle
import utils.kotterknife.findView
import utils.patternRecyler
import utils.visible

/**
 * Created by icaro on 08/07/16.
 */
class SearchMultiActivity(override var layout: Int = Layout.search_layout) : BaseActivity() {
	private val recyclerView: RecyclerView by findView(R.id.recycleView_search)
	private val query: String by bindBundle(SearchManager.QUERY)
	private var pager: Int = 1

	private val model: SearchMultiModelView by lazy {
		createViewModel(SearchMultiModelView::class.java, this)
	}

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		setupNavDrawer()
		handleTitle()
		setupRecycler()
		observers()
		if (isNetWorkAvailable(baseContext)) {
			model.fetchData(query)
		} else {
			snack(adView) { model.fetchData(query) }
		}
	}
	private fun setupRecycler() {
		recyclerView.patternRecyler(false).apply {
			addOnScrollListener(InfiniteScrollListener({ model.fetchData(query, pager) },
				layoutManager as LinearLayoutManager))
			adapter = SearchDelegateAdapter(this@SearchMultiActivity)
		}
	}

	private fun handleTitle() {
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = query
	}

	private fun observers() {
		model.response.observe(this, Observer {
			when (it) {
				is BaseRequest.Success -> {
					model.setLoading(false)
					fillRecycler(it.result as SearchMulti)
				}
				is BaseRequest.Failure -> {
					snack(adView, getString(R.string.tryagain)) {
						model.fetchData(query)
					}
				}
				is BaseRequest.Loading -> {
					setLoading(it.loading)
				}
			}
		})
	}

	private fun setLoading(loading: Boolean) {
		progress.visibility = if (loading) View.VISIBLE else View.GONE
	}

	private fun fillRecycler(it: SearchMulti) {
		if (it.results.isEmpty()) {
			text_search_empty.visible()
		} else {
			text_search_empty.gone()
			(recyclerView.adapter as SearchDelegateAdapter).addItens(it.results)
			pager = it.page + 1
		}
	}

	override fun onResume() {
		super.onResume()
		setAdMob(adView)
	}
}