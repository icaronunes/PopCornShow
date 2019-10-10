package main

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

import br.com.icaro.filme.R

/**
 * Created by icaro on 23/08/16.
 */
class MainAdapter(private val context: Context, supportFragmentManager: FragmentManager)
    : FragmentPagerAdapter(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        if (position == 0) {
            return MainFragment.newInstance(R.string.tvshow_main)
        }
        return if (position == 1) {
            MainFragment.newInstance(R.string.filmes_main)
        } else Fragment()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return if (position == 0) {
            context.getString(R.string.tvshow)
        } else {
            context.getString(R.string.filme)
        }
    }

    override fun getCount(): Int {
        return 2
    }

}