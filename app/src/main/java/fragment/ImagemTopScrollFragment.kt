package fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import applicaton.BaseFragment
import br.com.icaro.filme.R
import br.com.icaro.filme.R.drawable
import br.com.icaro.filme.R.layout
import domain.TopMain
import filme.activity.MovieDetailsActivity
import info.movito.themoviedbapi.model.Multi.MediaType.MOVIE
import kotlinx.android.synthetic.main.page_scroll_image_top.img_top_scroll
import kotlinx.android.synthetic.main.page_scroll_image_top.title
import tvshow.activity.TvShowActivity
import utils.Constantes
import utils.UtilsApp.loadPalette
import utils.setPicasso

/**
 * Created by icaro on 26/07/16.
 */
class ImagemTopScrollFragment : BaseFragment() {

    companion object {
        const val MENUS1_000 = -1000f
        const val ZERO = 0f
        const val TIME = 1400L

        @JvmStatic
        fun newInstance(topMainList: TopMain?): Fragment {
            val topScrollFragment = ImagemTopScrollFragment()
            val bundle = Bundle()
            bundle.putSerializable(Constantes.MAIN, topMainList)
            topScrollFragment.arguments = bundle
            return topScrollFragment
        }
    }

    private lateinit var topMains: TopMain

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        topMains = arguments!!.getSerializable(Constantes.MAIN) as TopMain
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        with(inflater.inflate(layout.page_scroll_image_top, container, false)) {
           findViewById<TextView>(R.id.title).text = topMains.nome
            val img = findViewById<ImageView>(R.id.img_top_scroll)
            if (topMains.mediaType.equals(MOVIE.name, ignoreCase = true)) {
                img.setPicasso(topMains.imagem, 6, img_erro = drawable.top_empty)
                    .setOnClickListener {
                        val intent = Intent(context, MovieDetailsActivity::class.java)
                        intent.putExtra(Constantes.NOME_FILME, topMains.nome)
                        intent.putExtra(Constantes.FILME_ID, topMains.id)
                        intent.putExtra(Constantes.COLOR_TOP, loadPalette(it))
                        startActivity(intent)
                    }
            } else {
                img.setPicasso(topMains.imagem, 6, img_erro = drawable.top_empty)
                    .setOnClickListener {
                        val intent = Intent(context, TvShowActivity::class.java)
                        intent.putExtra(Constantes.NOME_TVSHOW, topMains.nome)
                        intent.putExtra(Constantes.TVSHOW_ID, topMains.id)
                        intent.putExtra(Constantes.COLOR_TOP, loadPalette(it))
                        startActivity(intent)
                    }
            }

            AnimatorSet().apply {
                play(ObjectAnimator.ofFloat(img, View.X, MENUS1_000, ZERO)
                    .setDuration(TIME))
            }.start()
            this
        }
}