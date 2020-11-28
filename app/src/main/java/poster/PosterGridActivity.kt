package poster

import activity.BaseActivity
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import domain.PostersItem
import kotlinx.android.synthetic.main.poster_grid.adView
import kotlinx.android.synthetic.main.poster_grid.recycleView_poster_grid
import utils.Constant
import utils.kotterknife.bindBundle

/**
 * Created by icaro on 28/07/16.
 */

class PosterGridActivity(override var layout: Int = Layout.poster_grid) : BaseActivity() {

    private val title: String by bindBundle(Constant.NAME, "")
    private val posters: List<PostersItem> by bindBundle(Constant.POSTER)

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onCreate(savedInstanceState)
        recycleView_poster_grid.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(baseContext, 2)
        }
        setAdMob(adView)
        recycleView_poster_grid.adapter = PosterGridAdapter(this@PosterGridActivity, posters, title)
    }
}
