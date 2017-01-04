package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;

import com.net.codeusa.NetflixRoulette;

/**
 * Created by icaro on 03/01/17.
 */
public class NetflixActivity extends BaseActivity{
    private final String TAG = this.getClass().getName();
    private NetflixRoulette netflix;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
         netflix =  new NetflixRoulette();
        try {
            Log.d(TAG, "Version " + netflix.getAPIVersion());
            Log.d(TAG, "Version " + netflix.getAllData("Archer"));
            Log.d(TAG, "Version " + netflix.getMediaPoster("Archer"));
            Log.d(TAG, "Version " + netflix.getMediaCast("Breaking Bad"));
            Log.d(TAG, "Version " + "Getting a Director: " + netflix.getMediaDirector("Pulp Fiction"));
            Log.d(TAG, "Version " + "Getting media cast: " + netflix.getMediaCast("Breaking Bad"));

        } catch (Exception e){
            Log.d(TAG, e.toString());
        }
    }
}
