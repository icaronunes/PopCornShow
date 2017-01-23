package activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import br.com.icaro.filme.R;
import utils.Config;
import utils.Constantes;

/**
 * Created by icaro on 12/07/16.
 */

public class TrailerActivity extends YouTubeBaseActivity implements YouTubePlayer.OnInitializedListener {

    YouTubePlayerView youTubeView;
    String youtube_key;
    TextView sinopse;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.youtube_layout);
        youTubeView = (YouTubePlayerView) findViewById(R.id.youtube_view);
        sinopse = (TextView) findViewById(R.id.treiler_sinopse);
        youTubeView.initialize(Config.YOUTUBE_API_KEY, this);
        youtube_key = getIntent().getStringExtra(Constantes.YOU_TUBE_KEY);
        sinopse.setText(getIntent().getStringExtra(Constantes.SINOPSE));

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adview.loadAd(adRequest);

    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer player, boolean wasRestored) {
        try {
            if (!wasRestored) {
                player.cueVideo(youtube_key);
                player.setFullscreen(true);
                player.addFullscreenControlFlag(1);
                player.play();

                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Play_youTube");
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, youtube_key);
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        } catch ( Exception e){
            FirebaseCrash.report(e);
            Toast.makeText(this, R.string.ops, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider, YouTubeInitializationResult youTubeInitializationResult) {

    }
}
