package trailer

import android.os.Bundle
import android.widget.Toast
import br.com.icaro.filme.R.id
import br.com.icaro.filme.R.layout
import br.com.icaro.filme.R.string
import com.crashlytics.android.Crashlytics
import com.google.android.youtube.player.YouTubeBaseActivity
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubePlayer
import com.google.android.youtube.player.YouTubePlayer.OnInitializedListener
import com.google.android.youtube.player.YouTubePlayer.Provider
import com.google.android.youtube.player.YouTubePlayerView
import utils.Api
import utils.Constant
import kotlinx.android.synthetic.main.youtube_layout.trailer_sinopse as sinopse

/**
 * Created by icaro on 12/07/16.
 */
class TrailerActivity : YouTubeBaseActivity(), OnInitializedListener {

    private lateinit var idYoutube: String
    private val YOUTUBE_KEY by lazy { Api(context = this).getKey("YOUTUBE_API_KEY") }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layout.youtube_layout)
        val youTubeView: YouTubePlayerView = findViewById(id.youtube_view)
        idYoutube = intent.getStringExtra(Constant.YOU_TUBE_KEY)
        sinopse.text = intent.getStringExtra(Constant.SINOPSE)
        youTubeView.initialize(YOUTUBE_KEY, this)
    }

    override fun onInitializationSuccess(provider: Provider, player: YouTubePlayer, wasRestored: Boolean) {
        try {
                player.apply {
                    cueVideo(idYoutube)
                    setFullscreen(true)
                    addFullscreenControlFlag(1)
                    play()
                }
        } catch (e: Exception) {
            Crashlytics.logException(e)
            if (!isFinishing) Toast.makeText(this, string.ops, Toast.LENGTH_LONG).show()
        }
    }

    override fun onInitializationFailure(provider: Provider, youTubeInitializationResult: YouTubeInitializationResult) {
        Crashlytics.logException(Exception("Erro em \"onInitializationFailure\" dentro de " + this.javaClass))
    }
}