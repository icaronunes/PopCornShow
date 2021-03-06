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
 * Created by icaro on 21/11/16.
 */
class SecondSlide : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.intro, container, false).apply {
            val title = findViewById<View>(R.id.intro_tite) as TextView
            title.text = "Links"

            val subtitle = findViewById<View>(R.id.intro_subtitle) as TextView
            subtitle.text = resources.getText(R.string.subtitle_intro_2)

            val imageView = findViewById<View>(R.id.intro_img) as ImageView
            imageView.setImageResource(R.drawable.intro_link)
        }
    }
}
