package adapter

import adapter.TrailerAdapter.*
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import br.com.icaro.filme.R
import br.com.icaro.filme.R.*
import com.google.android.youtube.player.YouTubeInitializationResult
import com.google.android.youtube.player.YouTubeThumbnailLoader
import com.google.android.youtube.player.YouTubeThumbnailView
import com.google.android.youtube.player.YouTubeThumbnailView.*
import com.google.firebase.crashlytics.FirebaseCrashlytics
import domain.ResultsVideosItem
import trailer.TrailerActivity
import utils.Constant
import utils.key.ApiKeys
import utils.visible

/**
 * Created by icaro on 22/02/17.
 */
class TrailerAdapter(private val videos: MutableList<ResultsVideosItem?>?, private val sinopse: String) : Adapter<TrailerViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = TrailerViewHolder(parent = parent)
    override fun onBindViewHolder(holder: TrailerViewHolder, position: Int) = holder.bind(videos?.get(position)!!)
    override fun getItemCount() = videos?.size ?: 0

    inner class TrailerViewHolder(parent: ViewGroup)
        : RecyclerView.ViewHolder(LayoutInflater.from(parent.context)
        .inflate(layout.scroll_trailer, parent, false)) {
        private val YOUTUBE_KEY = ApiKeys.YOUTUBE_API_KEY

        fun bind(video: ResultsVideosItem) = with(itemView)  {
            contentDescription = video.name
            findViewById<YouTubeThumbnailView>(R.id.youtube_view_thumbnail)
                .initialize(YOUTUBE_KEY, object : OnInitializedListener {
                    override fun onInitializationSuccess(
                        youTubeThumbnailView: YouTubeThumbnailView,
                        youTubeThumbnailLoader: YouTubeThumbnailLoader
                    ) {
                        findViewById<ImageView>(R.id.play_treiler_img).visible()
                        youTubeThumbnailLoader.setVideo(video.key)
                    }

                    override fun onInitializationFailure(
                        youTubeThumbnailView: YouTubeThumbnailView,
                        youTubeInitializationResult: YouTubeInitializationResult
                    ) {
                        FirebaseCrashlytics.getInstance().log("Erro em \"onInitializationFailure\" dentro de " + this.javaClass)
                    }
                })
            setOnClickListener {
                context.startActivity(Intent(context, TrailerActivity::class.java).apply {
                    putExtra(Constant.YOU_TUBE_KEY, video.key)
                    putExtra(Constant.SINOPSE, sinopse)
                })
            }
        }
    }

    fun addVideos(videosTrailers: MutableList<ResultsVideosItem?>?) {
            videos?.addAll(videosTrailers?.toList() ?: listOf())
            notifyDataSetChanged()
    }

}