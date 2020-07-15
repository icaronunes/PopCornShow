package tvshow

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.icaro.filme.R.string
import domain.tvshow.Tvshow
import tvshow.fragment.TvShowFragment.Companion.newInstance

/**
 * Created by icaro on 23/08/16.
 */
class TvShowAdapter(
	private val context: Context,
	supportFragmentManager: FragmentManager?,
	private val series: Tvshow,
	private val color: Int
) : FragmentPagerAdapter(
	supportFragmentManager!!,
	BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
	override fun getItem(position: Int) = when (position) {
		0 -> newInstance(string.informacoes, series, color)
		1 -> newInstance(string.temporadas, series, color)
		else -> Fragment()
	}

	override fun getPageTitle(position: Int): String = when (position) {
		0 -> context.getString(string.informacoes)
		1 -> context.getString(string.temporadas_tv)
		else -> ""
	}

	override fun getCount() = 2
}