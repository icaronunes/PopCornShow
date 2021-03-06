package pessoa.activity

import activity.BaseActivity
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.viewpager.widget.ViewPager
import br.com.icaro.filme.R
import domain.person.ProfilesItem
import kotlinx.android.synthetic.main.activity_scroll_poster.indicator
import kotlinx.android.synthetic.main.activity_scroll_poster.pager
import poster.PosterScrollFragment
import utils.Constant

/**
 * Created by icaro on 12/07/16.
 */

class PhotoPersonActivity(override var layout: Int = Layout.activity_scroll_poster) : BaseActivity() {
    private var position: Int? = 0
    private lateinit var artworks: List<ProfilesItem>
    private var nome: String? = null

    public override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onCreate(savedInstanceState)
        getExtras()

        pager?.adapter = PosterFragment(supportFragmentManager)
        indicator?.setViewPager(pager as ViewPager)
        indicator?.setCurrentItem(position!!)
    }

    private fun getExtras() {
        if (intent.action == null) {
            artworks = intent.extras?.getSerializable(Constant.PERSON) as List<ProfilesItem>
            nome = intent.extras?.getString(Constant.NOME_PERSON)
            position = intent.extras?.getInt(Constant.POSICAO)
        } else {
            artworks = intent.extras?.getSerializable(Constant.PERSON) as List<ProfilesItem>
            nome = intent.extras?.getString(Constant.NOME_PERSON)
            position = intent.extras?.getInt(Constant.POSICAO)
        }
    }

    private inner class PosterFragment constructor(fm: FragmentManager) : FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getItem(position: Int): Fragment {
            return PosterScrollFragment.newInstance(artworks[position].filePath, nome)
        }

        override fun getCount(): Int {
            return if (artworks.isNotEmpty()) {
                artworks.size
            } else {
                0
            }
        }
    }
}
