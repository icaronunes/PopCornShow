package seguindo

import Txt
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

/**
 * Created by icaro on 25/11/16.
 */

class FollowingAdapater(
	private val fallowingActivity: FallowingActivity,
	supportFragmentManager: FragmentManager
) :
	FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

	override fun getItem(position: Int): Fragment {
		return if (position == 0) {
			ListFollowFragment()
		} else {
			ListFollowFragment2.newInstance(position)
		}
	}

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
	        fallowingActivity.getString(Txt.proximos)
        } else {
	        fallowingActivity.getString(Txt.tvshow)
        }
    }

    override fun getCount(): Int {
        return 2
    }
}

