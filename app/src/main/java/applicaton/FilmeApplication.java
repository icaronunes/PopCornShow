package applicaton;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.otto.Bus;

import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.model.config.Account;
import info.movito.themoviedbapi.model.config.TokenSession;
import info.movito.themoviedbapi.model.core.AccountID;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.SessionToken;
import utils.Config;
import utils.Prefs;
import utils.UtilsFilme;

/**
 * Created by icaro on 01/08/16.
 */

public class FilmeApplication extends Application {

    private static final String TAG = "FilmeApplication";
    private static FilmeApplication instance = null;
    private static MovieResultsPage favorite;
    private static TokenSession authentication;
    private static SessionToken token;
    private static AccountID accountID;
    private static String session;
    private static TmdbApi tmdbApi;
    private static Account account;
    private boolean logado = false;
    private static String user, pass;
    private Bus bus = new Bus();

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
        if (UtilsFilme.isNetWorkAvailable(getApplicationContext())) {
            new TMDVAsync().execute();
        }
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "FilmeApplication.onTerminate");
    }


    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public Bus getBus() {
        return bus;
    }


    public MovieResultsPage getFavorite() {
        return favorite;
    }

    public void setFavorite(MovieResultsPage favorite) {
        this.favorite = favorite;
    }

    protected class TMDVAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            user = Prefs.getString(getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
            pass = Prefs.getString(getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
            account = FilmeService.getAccount(user, pass);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }
}
