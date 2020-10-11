package produtora.activity

import Layout
import activity.BaseActivityAb
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.MenuItem
import android.widget.ImageView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import br.com.icaro.filme.R
import kotlinx.android.synthetic.main.produtora_layout.collapsing_toolbar
import kotlinx.android.synthetic.main.produtora_layout.produtora_filmes_recycler
import kotlinx.android.synthetic.main.produtora_layout.toolbar
import kotlinx.android.synthetic.main.produtora_layout.top_img_produtora
import produtora.adapter.ProdutoraAdapter
import produtora.viewmodel.ProductionViewModel
import utils.Constant
import utils.InfiniteScrollListener
import utils.kotterknife.bindBundle
import utils.makeToast
import utils.patternRecyclerGrid
import utils.resolver
import utils.setPicasso

/**
 * Created by icaro on 10/08/16.
 */
class ProductionActivity(override var layout: Int = Layout.produtora_layout) : BaseActivityAb() {

	private val model: ProductionViewModel by lazy { createViewModel(ProductionViewModel::class.java, this) }

	private val logo: String by bindBundle(Constant.ENDERECO)
	private val name: String by bindBundle(Constant.NAME)
	private val idProdutora: Int by bindBundle(Constant.ID, -1)

	private var pager = 1
	private var totalPager = 1

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(layout)
		setUpToolBar()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		setRecycler()
		setImageTop()
		observer()
		fetchCompany()
	}

	private fun observer() {
		model.company.observe(this, Observer {
			it.resolver(this, { company ->
				pager = company.page
				totalPager = company.totalPages

				(produtora_filmes_recycler.adapter as ProdutoraAdapter).addProdutoraMovie(
					company.results
						?.sortedBy { item -> item?.releaseDate }
						?.reversed(),
					company.totalResults)
				++pager
			}, {
				makeToast(R.string.ops)
			}, {
			})
		})
	}

	private fun setRecycler() {
		produtora_filmes_recycler.patternRecyclerGrid(3).apply {
			addOnScrollListener(InfiniteScrollListener({ fetchCompany() },
				layoutManager as GridLayoutManager))
			adapter = ProdutoraAdapter()
		}
	}

	private fun setTitle() {
		toolbar?.title = name
		collapsing_toolbar?.title = name
		top_img_produtora.scaleType = ImageView.ScaleType.CENTER_CROP
		toolbar.titleMarginTop = 15
		toolbar.titleMarginStart = 25
	}

	private fun fetchCompany() {
		model.fetchCompanyData(idProdutora, pager)
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}

	private fun setImageTop() {
		val setupTop = {
			AnimatorSet().apply {
				play(ObjectAnimator.ofFloat(top_img_produtora, "x", -100f, 0f)
					.setDuration(400))
			}.start()
			toolbar?.title = " "
			collapsing_toolbar?.title = " "
		}

		logo.let {
			top_img_produtora.setPicasso(it,
				4,
				img_erro = R.drawable.empty_produtora2,
				sucesso = setupTop,
				error = { setTitle() })
		}
	}
}
