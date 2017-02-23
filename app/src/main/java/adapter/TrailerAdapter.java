package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;

import activity.TrailerActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Video;
import utils.Config;
import utils.Constantes;

/**
 * Created by icaro on 22/02/17.
 */
public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {
    Context context;
    private List<Video> videos;
    private String sinopse;

    public TrailerAdapter(FragmentActivity activity, List<Video> videos, String overview) {
        context = activity;
        this.videos = videos;
        this.sinopse = overview;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_trailer, parent, false);
        return new TrailerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapter.TrailerViewHolder holder, final int position) {
        final String youtube_key = videos.get(position).getKey();

        holder.play_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, TrailerActivity.class);
                //  Log.d("OnClick", youtube_key);
                intent.putExtra(Constantes.YOU_TUBE_KEY, youtube_key);
                intent.putExtra(Constantes.SINOPSE, sinopse);

                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TrailerActivity.class.getName());
                bundle.putString("Endereço do youtube", youtube_key);
                FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        holder.thumbnailView.initialize(Config.YOUTUBE_API_KEY, OnInitializedListener(youtube_key));

    }


    @Override
    public int getItemCount() {
        return videos.size();
    }

    private YouTubeThumbnailView.OnInitializedListener OnInitializedListener(final String youtube_key) {
        return new YouTubeThumbnailView.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(youtube_key);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                FirebaseCrash.report(new Exception("Erro em \"onInitializationFailure\" dentro de " + this.getClass()));
            }
        };
    }


    public class TrailerViewHolder extends RecyclerView.ViewHolder {
        FrameLayout play_view;
        YouTubeThumbnailView thumbnailView;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            play_view = (FrameLayout) itemView.findViewById(R.id.frame_youtube_view_thumbnail);
            thumbnailView = (YouTubeThumbnailView) itemView.findViewById(R.id.youtube_view_thumbnail);

        }
    }
}