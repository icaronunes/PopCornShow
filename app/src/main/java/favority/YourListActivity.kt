package favority

import Color
import ID
import Layout
import activity.BaseActivityAb
import android.R.*
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout
import loading.firebase.TypeDataRef
import utils.Constant
import utils.kotterknife.bindBundle
import utils.kotterknife.findView

class YourListActivity(override var layout: Int = Layout.activity_usuario_list) : BaseActivityAb() {

	private val title: String by bindBundle(Constant.ABA)
	private val type: TypeDataRef by bindBundle(Constant.MEDIATYPE)
	private val viewPager: ViewPager by findView(ID.viewpage_usuario)
	private val tabLayout: TabLayout by findView(ID.tabLayout)

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setUpToolBar()
		supportActionBar?.title = title
		supportActionBar!!.setDisplayHomeAsUpEnabled(true)
		setupViewPagerTabs()
	}

	private fun setupViewPagerTabs() {
		viewPager.apply {
			offscreenPageLimit = 1
			currentItem = 0
			tabLayout.setupWithViewPager(viewPager)
			tabLayout.setSelectedTabIndicatorColor(ContextCompat.getColor(context, Color.accent))
			viewPager.adapter =
				FavoriteAdapater(this@YourListActivity, supportFragmentManager, type)
		}
	}

	override fun onOptionsItemSelected(item: MenuItem): Boolean {
		if (item.itemId == id.home) {
			finish()
			return true
		}
		return super.onOptionsItemSelected(item)
	}
}