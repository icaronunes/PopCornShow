package applicaton;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;
import com.squareup.otto.Bus;

import onsignal.CustomNotificationOpenedHandler;
import onsignal.CustomNotificationReceivedHandler;

/**
 * Created by icaro on 01/08/16.
 */

public class FilmeApplication extends Application {

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
       // Log.d(TAG, "FilmeApplication.onCreate");

            OneSignal.startInit(this)
                    .setNotificationOpenedHandler(new CustomNotificationOpenedHandler())
                    .setNotificationReceivedHandler(new CustomNotificationReceivedHandler())
                    .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                    .init();


        if (br.com.icaro.filme.BuildConfig.REPORT_CRASH) {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(true);
          //  Log.d(TAG, "REPORT_CRASH - TRUE");
        } else {
            FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            mFirebaseAnalytics.setAnalyticsCollectionEnabled(false);
         //   Log.d(TAG, "REPORT_CRASH - FALSE");
        }

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

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

    public Bus getBus(){
        return bus;
    }

}
