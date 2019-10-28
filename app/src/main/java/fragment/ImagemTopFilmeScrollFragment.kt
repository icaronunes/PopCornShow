package fragment

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import br.com.icaro.filme.R
import utils.Constantes
import utils.setPicassoWithCache

/**
 * Created by icaro on 26/09/16.
 */
class ImagemTopFilmeScrollFragment : Fragment() {

    internal var path: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        path = arguments?.getString(Constantes.ENDERECO)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.page_scroll_viewpage_top, container, false)
        val img = view.findViewById<ImageView>(R.id.img_top_viewpager)
        img.setPicassoWithCache(path, 6, img_erro = R.drawable.top_empty, sucesso = {
            AnimatorSet().apply {
                ObjectAnimator.ofFloat(img, View.Y, -100f, 0f)
                duration = 8000
            }.start()
        })

        return view
    }

    companion object {

        fun newInstance(artwork: String?): Fragment {
            val topScrollFragment = ImagemTopFilmeScrollFragment()
            val bundle = Bundle()
            bundle.putString(Constantes.ENDERECO, artwork)
            topScrollFragment.arguments = bundle
            return topScrollFragment
        }
    }
}
