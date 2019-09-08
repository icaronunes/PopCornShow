package applicaton;

import android.content.Context;
import android.util.Log;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.onesignal.OneSignal;
import com.squareup.otto.Bus;

import br.com.icaro.filme.BuildConfig;
import br.com.icaro.filme.R;
import io.fabric.sdk.android.Fabric;
import onsignal.CustomNotificationOpenedHandler;
import onsignal.CustomNotificationReceivedHandler;

/**
 * Created by icaro on 01/08/16.
 */

public class PopCornApplication extends MultiDexApplication {

    private static final String TAG = PopCornApplication.class.getName();
    private static PopCornApplication instance = null;
    private Bus bus = new Bus();

    public static PopCornApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;

        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new CustomNotificationOpenedHandler())
                .setNotificationReceivedHandler(new CustomNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();

        if (!BuildConfig.DEBUG) {
            Crashlytics crashlyticsKit = new Crashlytics.Builder()
                    .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                    .build();

            Fabric.with(this, crashlyticsKit);
        }
        MobileAds.initialize(this, getString(R.string.admob_id_app));

        try {
            if (getExternalCacheDir().exists()) {
                if (getExternalCacheDir().length() > 1000000 * 3) {
                    getExternalCacheDir().delete();
                }
            }
        } catch (Exception e) {
        }

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

    public Bus getBus() {
        return bus;
    }

}
