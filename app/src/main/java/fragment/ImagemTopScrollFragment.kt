package fragment

import Layout
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import applicaton.BaseFragment
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import domain.TopMain
import filme.activity.MovieDetailsActivity
import tvshow.activity.TvShowActivity
import utils.Constant
import utils.UtilsApp.loadPalette
import utils.enums.EnumTypeMedia.*
import utils.setPicasso

/**
 * Created by icaro on 26/07/16.
 */
class ImagemTopScrollFragment(override val layout: Int = Layout.page_scroll_image_top) : BaseFragment() {
	companion object {
		const val MENUS1_000 = -1000f
		const val ZERO = 0f
		const val TIME = 1400L

		@JvmStatic
		fun newInstance(topMainList: TopMain?): Fragment {
			val topScrollFragment = ImagemTopScrollFragment()
			val bundle = Bundle()
			bundle.putSerializable(Constant.MAIN, topMainList)
			topScrollFragment.arguments = bundle
			return topScrollFragment
		}
	}

	private lateinit var topMains: TopMain
	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		topMains = requireArguments().getSerializable(Constant.MAIN) as TopMain
	}

	override fun onCreateView(
		inflater: LayoutInflater,
		container: ViewGroup?,
		savedInstanceState: Bundle?
	): View? =
		with(inflater.inflate(layout, container, false)) {
			findViewById<TextView>(R.id.title).text = topMains.nome
			val img = findViewById<ImageView>(R.id.img_top_scroll)

			if (topMains.mediaType.equals(MOVIE.name, ignoreCase = true)) {
				findViewById<ConstraintLayout>(R.id.layout_scroll_image_top)
					.setBackgroundColor(ContextCompat.getColor(requireActivity(), R.color.red))
				img.setPicasso(topMains.imagem, 6, img_erro = drawable.top_empty)
					.setOnClickListener {
						val intent = Intent(context, MovieDetailsActivity::class.java)
						intent.putExtra(Constant.NOME_FILME, topMains.nome)
						intent.putExtra(Constant.FILME_ID, topMains.id)
						intent.putExtra(Constant.COLOR_TOP, loadPalette(it as ImageView))
						startActivity(intent)
					}
			} else {
				findViewById<ConstraintLayout>(R.id.layout_scroll_image_top)
					.setBackgroundColor(
						ContextCompat.getColor(
							requireActivity(),
							R.color.blue_main
						)
					)
				img.setPicasso(topMains.imagem, 6, img_erro = drawable.top_empty)
					.setOnClickListener {
						val intent = Intent(context, TvShowActivity::class.java)
						intent.putExtra(Constant.NOME_TVSHOW, topMains.nome)
						intent.putExtra(Constant.TVSHOW_ID, topMains.id)
						intent.putExtra(Constant.COLOR_TOP, loadPalette(it as ImageView))
						startActivity(intent)
					}
			}

			AnimatorSet().apply {
				play(
					ObjectAnimator.ofFloat(img, View.X, MENUS1_000, ZERO)
						.setDuration(TIME)
				)
			}.start()
			this
		}
}