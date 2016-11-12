package applicaton;

import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.onesignal.OneSignal;

import domian.FilmeService;
import info.movito.themoviedbapi.model.config.Account;
import onsignal.CustomNotificationOpenedHandler;
import onsignal.CustomNotificationReceivedHandler;
import utils.Prefs;
import utils.UtilsFilme;

/**
 * Created by icaro on 01/08/16.
 */

public class FilmeApplication extends Application {

    private static final String TAG = "FilmeApplication";
    private static FilmeApplication instance = null;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static void setAccount(Account account) {
        FilmeApplication.account = account;
    }

    private static Account account = null;
    private boolean logado = false;
    private static String user, pass;



    public boolean isLogado() {
        return logado;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
    }

    public static FilmeApplication getInstance() {
        return instance;
    }

    public static String getTAG() {
        return TAG;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Log.d(TAG, "FilmeApplication.onCreate");
        if (UtilsFilme.isNetWorkAvailable(getApplicationContext())) {
            new TMDVAsync().execute();
        }
        OneSignal.startInit(this)
                .setNotificationOpenedHandler(new CustomNotificationOpenedHandler())
                .setNotificationReceivedHandler(new CustomNotificationReceivedHandler())
                .inFocusDisplaying(OneSignal.OSInFocusDisplayOption.Notification)
                .init();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mFirebaseAnalytics.setAnalyticsCollectionEnabled(false);


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
       // Log.d(TAG, "FilmeApplication.onTerminate");
    }


    public Account getAccount() {
        return account;
    }


    public String getUser() {
        return user;
    }

    public String getPass() {
        return pass;
    }


    protected class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            user = Prefs.getString(getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
            pass = Prefs.getString(getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
           // Log.d(this.getClass().getName(), "User: " + user);
         //   Log.d(this.getClass().getName(), "Pass: " + pass);

            account = FilmeService.getAccount(user, pass);
            if (account != null){
                setLogado(true);
               // Log.d(this.getClass().getName(), "account - true");
            } else {
                setLogado(false);
               // Log.d(this.getClass().getName(), "account - false");
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }
}
