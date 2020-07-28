package favority

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.icaro.filme.R.string
import domain.MovieDb
import domain.TvshowDB
import loading.firebase.TypeDataRef
import loading.firebase.TypeDataRef.FAVORITY
import loading.firebase.TypeMediaFireBase.MOVIE
import loading.firebase.TypeMediaFireBase.TVSHOW

/**
 * Created by icaro on 23/08/16.
 */
class FavoriteAdapater(
	private val context: Context,
	supportFragmentManager: FragmentManager?,
	private val movies: List<MovieDb>,
	private val series: List<TvshowDB>,
	val type: TypeDataRef = FAVORITY
) : FragmentPagerAdapter(
	supportFragmentManager!!,
	BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
	override fun getItem(position: Int): Fragment = if (position == 0) {
		ListYourFragment.newInstance(type, MOVIE)
	} else {
		ListYourFragment.newInstance(type, TVSHOW)
	}

	override fun getPageTitle(position: Int): CharSequence? {
		if (position == 0) {
			return context.getString(string.filme)
		}
		return if (position == 1) {
			context.getString(string.tvshow)
		} else null
	}

	override fun getCount(): Int {
		return 2
	}

}