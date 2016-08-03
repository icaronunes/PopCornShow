package applicaton;

import android.app.Application;
import android.util.Log;

import com.squareup.otto.Bus;

import domian.FilmeService;
import info.movito.themoviedbapi.model.config.Account;
import utils.Prefs;

/**
 * Created by icaro on 01/08/16.
 */

public class FilmeApplication extends Application {

    private static final String TAG = "FilmeApplication";
    private static FilmeApplication instance = null;
    private Bus bus = new Bus();
    private boolean logado = false;

    public static FilmeApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "FilmeApplication.onTerminate");
    }

    public Bus getBus() {
        return bus;
    }


}
