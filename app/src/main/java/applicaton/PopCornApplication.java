package applicaton;

import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.onesignal.OneSignal;
import br.com.icaro.filme.R;
import onsignal.CustomNotificationOpenedHandler;
import onsignal.CustomNotificationReceivedHandler;


/**
 * Created by icaro on 01/08/16.
 */

public class PopCornApplication extends MultiDexApplication {

    private static PopCornApplication instance = null;
    public static PopCornApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        instance = this;

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new CustomNotificationOpenedHandler())
                .setNotificationReceivedHandler(new CustomNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true);

        MobileAds.initialize(this, getString(R.string.admob_id_app));

        try {
            if (getExternalCacheDir() != null && getExternalCacheDir().exists()) {
                if (getExternalCacheDir().length() > 1000000 * 3) {
                    getExternalCacheDir().delete();
                }
            }
        } catch (Exception ignored) {}

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
