package pessoa.adapter

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import br.com.icaro.filme.R.*
import fragment.FragmentError
import pessoa.fragment.PersonFragmentMovie
import pessoa.fragment.PersonFragmentPhoto
import pessoa.fragment.PersonFragmentProduction
import pessoa.fragment.PersonFragmentProfile
import pessoa.fragment.PersonFragmentTv

/**
 * Created by icaro on 18/08/16.
 */
class PersonAdapter(
	private val context: Context,
	supportFragmentManager: FragmentManager?,
	private val error: Boolean
) : FragmentPagerAdapter(
	supportFragmentManager!!, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
) {
	override fun getCount() = if (error) 1 else 5
	override fun getItem(position: Int): Fragment {
		if (error) return FragmentError()
		return when (position) {
			0 -> PersonFragmentTv()
			1 -> PersonFragmentMovie()
			2 -> PersonFragmentProfile()
			3 -> PersonFragmentPhoto()
			4 -> PersonFragmentProduction()
			else -> PersonFragmentTv()
		}
	}

	override fun getPageTitle(position: Int): CharSequence? {
		if (error) return "Error :("
		return when (position) {
			0 -> context.getString(string.tvshow)
			1 -> context.getString(string.filme)
			2 -> context.getString(string.person)
			3 -> context.getString(string.imagem_person)
			4 -> context.getString(string.producao)
			else -> "Error"
		}
	}


}