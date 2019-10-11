package seguindo

import android.content.Context

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import br.com.icaro.filme.R
import domain.UserTvshow

/**
 * Created by icaro on 25/11/16.
 */

class FollowingAdapater(seguindoActivity: SeguindoActivity,
                        supportFragmentManager: FragmentManager,
                        private val userTvshows: MutableList<UserTvshow>)
    : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    private val context: Context

    init {
        this.context = seguindoActivity
    }

    override fun getItem(position: Int): Fragment {
        return if (position == 0) {
            ListaSeguindoFragment.newInstance(position, userTvshows)
        } else {
            ListaSeguindoFragment.newInstance(position, userTvshows)
        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.proximos)
        } else {
            context.getString(R.string.seguindo)
        }
    }

    override fun getCount(): Int {
        return 2
    }
}
