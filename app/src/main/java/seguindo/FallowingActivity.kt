package seguindo

import Layout
import activity.BaseActivityAb
import android.os.Bundle
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R
import com.google.android.material.tabs.TabLayout
import utils.kotterknife.findView

/**
 * Created by icaro on 25/11/16.
 */
class FallowingActivity(override var layout: Int = Layout.activity_usuario_list) :
	BaseActivityAb() {

	private val viewPager: ViewPager by findView(R.id.viewpage_usuario)
	private val tabView: TabLayout by findView(R.id.tabLayout)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		supportActionBar?.setTitle(R.string.seguindo)
		supportActionBar?.setDisplayHomeAsUpEnabled(true)
		setupViewPagerTabs()
	}

	private fun setupViewPagerTabs() {
		viewPager.apply {
			offscreenPageLimit = 1
			currentItem = 2
			tabView.setupWithViewPager(viewPager)
			tabView.setSelectedTabIndicatorColor(
				ActivityCompat.getColor(
					this@FallowingActivity,
					R.color.accent
				)
			)
			adapter = FollowingAdapater(this@FallowingActivity, supportFragmentManager)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == android.R.id.home) {
			finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}
}
