package poster

import Layout
import activity.BaseActivity
import android.os.Build.*
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R.*
import com.viewpagerindicator.LinePageIndicator
import domain.PostersItem
import utils.Constant
import utils.kotterknife.bindBundle
import utils.kotterknife.findView

/**
 * Created by icaro on 12/07/16.
 */
class PosterActivity(override var layout: Int = Layout.activity_scroll_poster) : BaseActivity() {
	private val artworks: List<PostersItem> by bindBundle(Constant.ARTWORKS)
	private val nome: String by bindBundle(Constant.NAME, "")
	private val viewPager: ViewPager by findView(id.pager)
	private val titlePageIndicator: LinePageIndicator by findView(id.indicator)
	private val position: Int by bindBundle(Constant.POSICAO)
	public override fun onCreate(savedInstanceState: Bundle?) {
		if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
			window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
		}
		super.onCreate(savedInstanceState)
		viewPager.adapter = PosterFragment(supportFragmentManager)
		titlePageIndicator.setViewPager(viewPager)
		titlePageIndicator.setCurrentItem(position)
	}

	private inner class PosterFragment(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_SET_USER_VISIBLE_HINT) {
		override fun getItem(position: Int): PosterScrollFragment
		= PosterScrollFragment.newInstance(artworks[position].filePath, nome)
		override fun getCount() = artworks.size
	}
}