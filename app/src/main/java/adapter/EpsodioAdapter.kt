package adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import domain.TvSeasons
import domain.UserSeasons
import fragment.EpsodioFragment

/**
 * Created by icaro on 27/08/16.
 */
class EpsodioAdapter(supportFragmentManager: FragmentManager,
                     private val tvSeason: TvSeasons, private val tvshowId: Int,
                     private val color: Int, private val seguindo: Boolean,
                     private val temporadaPosition: Int) : FragmentPagerAdapter(supportFragmentManager) {

    override fun getItem(position: Int): Fragment {
        return EpsodioFragment.newInstance(tvSeason.episodes!![position],
                tvshowId, color, seguindo, position, temporadaPosition)
    }

    override fun getCount(): Int {
        return tvSeason.episodes!!.size
    }

    override fun getPageTitle(position: Int): CharSequence? {

        return if (tvSeason.episodes!![position]?.episodeNumber!! <= 9) {
            "E0" + tvSeason.episodes[position]?.episodeNumber!!
        } else {
            "E" + tvSeason.episodes[position]?.episodeNumber!!
        }
    }
}
