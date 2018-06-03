package applicaton;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.onesignal.OneSignal;
import com.squareup.otto.Bus;

import br.com.icaro.filme.BuildConfig;
import io.fabric.sdk.android.Fabric;
import onsignal.CustomNotificationOpenedHandler;
import onsignal.CustomNotificationReceivedHandler;

/**
 * Created by icaro on 01/08/16.
 */

public class FilmeApplication extends MultiDexApplication {

    private static final String TAG = FilmeApplication.class.getName();
    private static FilmeApplication instance = null;
    private Bus bus = new Bus();

    public static FilmeApplication getInstance() {
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

        Crashlytics crashlyticsKit = new Crashlytics.Builder()
                .core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build())
                .build();
        Fabric.with(this, crashlyticsKit);

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
