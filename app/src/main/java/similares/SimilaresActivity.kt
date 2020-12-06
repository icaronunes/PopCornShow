package similares

import Layout
import activity.BaseActivity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import filme.activity.MovieDetailsActivity
import kotlinx.android.synthetic.main.activity_similares.adView
import kotlinx.android.synthetic.main.activity_similares.similares_recyclerview
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp
import utils.kotterknife.bindBundle
import utils.patternRecyler

/**
 * Created by icaro on 12/08/16.
 */
open class SimilaresActivity(override var layout: Int = Layout.activity_similares) : BaseActivity() {
	private val listSimilares: List<SimilaresInfo>? by bindBundle<List<SimilaresInfo>>(Constant.SIMILARES,
		null)
	private val type: String by bindBundle(Constant.MEDIATYPE, "")
	private val title: String by bindBundle(Constant.NAME, "")

	public override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		supportActionBar?.title = title
		similares_recyclerview.patternRecyler(false)
		setDataRecycler()
		setAdMob(adView)
	}

	private fun setDataRecycler() {
		when (type) {
			Constant.MOVIE -> {
				similares_recyclerview.adapter =
					SimilaresAdapter(this@SimilaresActivity,
						listSimilares = listSimilares,
						callBack = { item: SimilaresInfo, imageView: ImageView ->
							startActivity(Intent(this@SimilaresActivity,
								MovieDetailsActivity::class.java).apply {
								putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(imageView))
								putExtra(Constant.ID, item.id())
							})
						})
			}
			Constant.TV -> {
				similares_recyclerview.adapter =
					SimilaresAdapter(this@SimilaresActivity,
						listSimilares = listSimilares,
						callBack = { item: SimilaresInfo, img: ImageView ->
							startActivity(Intent(this@SimilaresActivity,
								TvShowActivity::class.java).apply {
								putExtra(Constant.COLOR_TOP, UtilsApp.loadPalette(img))
								putExtra(Constant.ID, item.id())
							})
						})
			}
			else -> ops()
		}
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
