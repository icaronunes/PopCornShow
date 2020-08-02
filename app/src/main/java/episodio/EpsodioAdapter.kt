package episodio

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import domain.TvSeasons

/**
 * Created by icaro on 27/08/16.
 */
class EpsodioAdapter(
	supportFragmentManager: FragmentManager,
	private val tvSeason: TvSeasons,
	private val tvShowId: Int,
	private val color: Int,
	private val fallow: Boolean,
	private val seasonPosition: Int
) :
	FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	override fun getItem(position: Int): Fragment {
		return EpsodioFragment.newInstance(
			tvSeason.episodes[position],
			tvShowId, color, fallow, position, seasonPosition, tvSeason
		)
	}

	override fun getCount(): Int {
		return tvSeason.episodes.size
	}

	override fun getPageTitle(position: Int): CharSequence? {

		return if (tvSeason.episodes[position].episodeNumber <= 9) {
			"E0" + tvSeason.episodes[position].episodeNumber
		} else {
			"E" + tvSeason.episodes[position].episodeNumber
		}
	}
}
