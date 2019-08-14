package activity

import adapter.PosterGridAdapter
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.Window
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import br.com.icaro.filme.R
import domain.PostersItem
import kotlinx.android.synthetic.main.poster_grid.*
import utils.Constantes

/**
 * Created by icaro on 28/07/16.
 */

class PosterGridActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.requestFeature(Window.FEATURE_CONTENT_TRANSITIONS)
        }
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.poster_grid)
        recycleView_poster_grid.apply {
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            layoutManager = GridLayoutManager(baseContext, 2)
        }
        
        setAdmob()
        
        if (intent.hasExtra(Constantes.POSTER)) {
            val posters = intent.getSerializableExtra(Constantes.POSTER) as List<PostersItem>
            val titulo = intent.getStringExtra(Constantes.NOME)
            recycleView_poster_grid.adapter = PosterGridAdapter(this@PosterGridActivity, posters, titulo)
        }

    }
}
