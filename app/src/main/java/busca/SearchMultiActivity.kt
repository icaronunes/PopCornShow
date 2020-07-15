package busca

import Layout
import activity.BaseActivityAb
import android.app.SearchManager
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import applicaton.BaseViewModel.BaseRequest
import br.com.icaro.filme.R
import busca.adapter.SearchDelegateAdapter
import domain.search.SearchMulti
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.include_progress.progress
import kotlinx.android.synthetic.main.search_layout.adView
import kotlinx.android.synthetic.main.search_layout.recycleView_search
import kotlinx.android.synthetic.main.search_layout.text_search_empty
import pessoa.activity.PersonActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp.isNetWorkAvailable
import utils.enums.EnumTypeMedia.MOVIE
import utils.enums.EnumTypeMedia.PERSON
import utils.enums.EnumTypeMedia.TV
import utils.gone
import utils.patternRecyler
import utils.visible

/**
 * Created by icaro on 08/07/16.
 */
class SearchMultiActivity(override var layout: Int = Layout.search_layout) : BaseActivityAb() {

	private val recyclerView: RecyclerView by lazy { recycleView_search.patternRecyler(false) }
	private val model: SearchMultiModelView by lazy {
		createViewModel(
			SearchMultiModelView::class.java,
			this
		)
	}
	private var query = ""

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		setupNavDrawer()
		handleTitle()
		query = intent.getStringExtra(SearchManager.QUERY)
		observers()
		if (Intent.ACTION_VIEW == intent.action) { //TODO pra que server isso?
			when {
				intent.data?.lastPathSegment.equals(MOVIE.type, ignoreCase = true) -> {
					startActivity(Intent(this, MovieDetailsActivity::class.java).apply {
						val id =
							intent?.extras?.getString(SearchManager.EXTRA_DATA_KEY)?.toInt() ?: -1
						intent.putExtra(Constant.FILME_ID, id)
						intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
					})
					finish()
					return
				}
				intent.data?.lastPathSegment.equals(TV.type, ignoreCase = true) -> {
					startActivity(Intent(this, TvShowActivity::class.java).apply {
						intent.putExtra(
							Constant.TVSHOW_ID,
							intent?.extras?.getString(SearchManager.EXTRA_DATA_KEY)?.toInt() ?: -1
						)
						intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
					})
					finish()
					return
				}
				intent.data?.lastPathSegment.equals(PERSON.type, ignoreCase = true) -> {
					startActivity(Intent(this, PersonActivity::class.java).apply {
						intent.putExtra(
							Constant.PERSON_ID,
							intent?.extras?.getString(SearchManager.EXTRA_DATA_KEY)?.toInt() ?: -1
						)
						intent.flags = Intent.FLAG_ACTIVITY_FORWARD_RESULT
					})
					finish()
					return
				}
			}
		} else {
			if (isNetWorkAvailable(baseContext)) {
				model.fetchData(query)
			} else {
				snack(adView) {
					model.fetchData(query)
				}
			}
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
			model.setLoading(false)
		} else {
			text_search_empty.gone()
			model.setLoading(false)
			recyclerView.adapter = SearchDelegateAdapter(this@SearchMultiActivity, it)
		}
	}

	override fun onResume() {
		super.onResume()
		setAdMob(adView)
	}
}