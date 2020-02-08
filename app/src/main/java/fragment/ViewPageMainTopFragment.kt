package fragment

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import domain.TopMain

class ViewPageMainTopFragment(supportFragmentManager: FragmentManager?, private val multis: List<TopMain>) :
    FragmentPagerAdapter(supportFragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    override fun getItem(position: Int): Fragment {
        return ImagemTopScrollFragment.newInstance(multis[position])
    }

    override fun getCount(): Int {
        return multis.size
    }
}