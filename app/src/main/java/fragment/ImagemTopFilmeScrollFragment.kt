package fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import br.com.icaro.filme.R
import utils.Constant
import utils.kotterknife.bindArgument
import utils.setPicassoWithCache

/**
 * Created by icaro on 26/09/16.
 */
class ImagemTopFilmeScrollFragment : Fragment() {

	val path: String by bindArgument(Constant.ENDERECO)

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	) = inflater.inflate(R.layout.page_scroll_viewpage_top, container, false).apply {
		val img = findViewById<ImageView>(R.id.img_top_viewpager)
		img.setPicassoWithCache(path, 6, img_erro = R.drawable.top_empty, sucesso = {
			AnimatorSet().apply {
				ObjectAnimator.ofFloat(img, View.Y, -100f, 0f)
				duration = 800
			}.start()
		})
	}

	companion object {
		fun newInstance(artwork: String?) = ImagemTopFilmeScrollFragment().apply {
			arguments = bundleOf(Constant.ENDERECO to artwork)
		}
	}
}
