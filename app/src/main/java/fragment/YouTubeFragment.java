//package fragment;
//
//import android.os.Bundle;
//import android.support.v4.app.FragmentTransaction;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.google.android.youtube.player.YouTubeInitializationResult;
//import com.google.android.youtube.player.YouTubePlayer;
//import com.google.android.youtube.player.YouTubePlayer.Provider;
//import com.google.android.youtube.player.YouTubePlayerSupportFragment;
//
//import br.com.icaro.filme.R;
//import utils.Config;
//
///**
// * Created by icaro on 12/07/16.
// */
//
//public class YouTubeFragment extends YouTubePlayerSupportFragment {
//
//    @Override
//    public View onCreateView(LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle) {
//        View view = layoutInflater.inflate(R.layout.youtube_layout, viewGroup, false);
//
//        YouTubePlayerSupportFragment youTubePlayerSupportFragment = YouTubePlayerSupportFragment.newInstance();
//
//
//        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
//
//        transaction.add(R.id.youtube_layout, youTubePlayerSupportFragment).commit();
//
//        youTubePlayerSupportFragment.initialize(Config.YOUTUBE_API_KEY, new YouTubePlayer.OnInitializedListener() {
//            @Override
//            public void onInitializationSuccess(Provider provider, YouTubePlayer player, boolean wasRestored) {
//                if (!wasRestored) {
//                    player.setPlayerStyle(YouTubePlayer.PlayerStyle.CHROMELESS); // ? Ver os outros styles
//                    player.loadVideo("EGy39OMyHzw");
//                    player.play();
//
//                }
//            }
//
//            @Override
//            public void onInitializationFailure(Provider provider, YouTubeInitializationResult youTubeInitializationResult) {
//
//            }
//        });
//
//        return view;
//    }
//
//}
