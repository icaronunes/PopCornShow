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

/**
 * Created by icaro on 01/08/16.
 */

public class FilmeApplication extends Application {

    private static final String TAG = "FilmeApplication";
    private static FilmeApplication instance = null;
    private static MovieResultsPage favorite;
    private static TmdbAccount tmdbAccount;
    private static TokenSession authentication;
    private static SessionToken token;
    private static AccountID accountID;
    private static String session;
    private static TmdbApi tmdbApi;
    private static Account account;
    private static String user, pass;
    private Bus bus = new Bus();
    private boolean logado = false;

    public static FilmeApplication getInstance() {
        return instance;
    }

    public static void setInstance(FilmeApplication instance) {
        FilmeApplication.instance = instance;
    }

    public static String getTAG() {
        return TAG;
    }

    public static TokenSession getAuthentication() {
        return authentication;
    }

    public static void setAuthentication(TokenSession authentication) {
        FilmeApplication.authentication = authentication;
    }

    public static SessionToken getToken() {
        return token;
    }

    public static void setToken(SessionToken token) {
        FilmeApplication.token = token;
    }

    public static AccountID getAccountID() {
        return accountID;
    }

    public static void setAccountID(AccountID accountID) {
        FilmeApplication.accountID = accountID;
    }

    public static String getSession() {
        return session;
    }

    public static void setSession(String session) {
        FilmeApplication.session = session;
    }

    public static TmdbApi getTmdbApi() {
        return tmdbApi;
    }

    public static void setTmdbApi(TmdbApi tmdbApi) {
        FilmeApplication.tmdbApi = tmdbApi;
    }

    public static TmdbAccount getTmdbAccount() {
        return tmdbAccount;
    }

    public static void setTmdbAccount(TmdbAccount tmdbAccount) {
        FilmeApplication.tmdbAccount = tmdbAccount;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        new TMDVAsync().execute();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d(TAG, "FilmeApplication.onTerminate");
    }

    public boolean isLogado() {
        return logado;
    }

    public void setLogado(boolean logado) {
        this.logado = logado;
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

    public void setBus(Bus bus) {
        this.bus = bus;
    }

    public MovieResultsPage getFavorite() {
        return favorite;
    }

    public void setFavorite(MovieResultsPage favorite) {
        this.favorite = favorite;
    }

    public void Reconectar() {
        new TMDVAsync().execute();
    }

    public class TMDVAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {

            user = Prefs.getString(getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
            pass = Prefs.getString(getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
            favorite = FilmeService.getFavorite(user, pass);
            account = FilmeService.getAccount(user, pass);

//            if (!user.isEmpty() && !pass.isEmpty() ) {
//                tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
//                authentication = tmdbApi
//                        .getAuthentication().getSessionLogin(user, pass);
//                session = authentication.getSessionId();
//                token = new SessionToken(session);
//                account = tmdbApi.getAccount().getAccount(token);
//                tmdbAccount = tmdbApi.getAccount();
//                accountID = new AccountID(account.getId());
//                setFavorite(FilmeService.getFavorite(user, pass));
//            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

        }

    }
}
