package intro

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import br.com.icaro.filme.R

/**
 * Created by icaro on 03/12/16.
 */
class ThreeSlide : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.intro, container, false).apply {
            val title = findViewById<View>(R.id.intro_tite) as TextView
            title.text = resources.getString(R.string.enjoy)

            val imageView = findViewById<View>(R.id.intro_img) as ImageView
            imageView.setImageResource(R.drawable.intro_icon)
        }
    }
}
