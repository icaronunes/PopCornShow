package domian;

import android.app.Application;
import android.util.Log;

import java.util.List;

import activity.CrewsActivity;
import applicaton.FilmeApplication;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbAuthentication;
import info.movito.themoviedbapi.TmdbChanges;
import info.movito.themoviedbapi.TmdbCollections;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.MovieList;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.config.Account;
import info.movito.themoviedbapi.model.config.TokenSession;
import info.movito.themoviedbapi.model.core.AccountID;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.NamedStringIdElement;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import info.movito.themoviedbapi.model.core.SessionToken;
import utils.Config;
import utils.Prefs;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.google.android.gms.analytics.internal.zzy.t;

/**
 * Created by icaro on 01/07/16.
 */

public class FilmeService {

    public static TmdbSearch getTmdbSearch() {
        return new TmdbApi(Config.TMDB_API_KEY).getSearch();
    }

    public static TmdbMovies getTmdbMovies() {
        TmdbMovies movies = new TmdbApi(Config.TMDB_API_KEY).getMovies();
        return movies;
    }


    public static TmdbCollections getTmdbCollections() {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        Log.d("getTmdbMovie", "Id: " + id);

        return tmdbApi.getCollections();
    }

    public static Account getAccount(String user, String pass) {
        Log.d("TmdbAuthentication", "TmdbAuthentication");
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        try {
            TokenSession authentication = tmdbApi
                    .getAuthentication().getSessionLogin(user, pass);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            Account account = tmdbApi.getAccount().getAccount(token);

            if (account != null) {
                return account;
            }

        } catch (Exception e) {
            Log.e("getAccount", e.getMessage());
        }
        Log.d("getAccount", "Account Nulo");
        return null;
    }

    public static SessionToken TmdbSessionToken(String user, String password) {
        Log.d("TmdbAuthentication", "TmdbAuthentication");
        TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                .getAuthentication().getSessionLogin("icaronunes", "knabs123");
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        MovieResultsPage movieResultsPage = account.getFavoriteMovies(token, accountID);
        List<MovieDb> dbList = movieResultsPage.getResults();

        Log.d("TmdbAuthentication", dbList.get(0).getOriginalTitle());

        if (token != null) {
            return token;
        } else {
            Log.d("SessionToken", "SessionToken Nulo");
            return null;
        }
    }

    public static TmdbAccount tmdbAccount(String user, String password) {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();

        return account;//.getFavoriteMovies(token, accountID);

    }

    public static ResponseStatus  addOrRemoverWatchList (String user, String password, Integer id_filme, boolean opcao) {

        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TokenSession authentication = tmdbApi
                .getAuthentication().getSessionLogin(user, password);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        if (opcao) {
            ResponseStatus status = account.addToWatchList(token, accountID, id_filme, TmdbAccount.MediaType.MOVIE);
            return status;
        } else {
            ResponseStatus status = account.removeFromWatchList(token, accountID, id_filme, TmdbAccount.MediaType.MOVIE);
            return status;
        }

    }

    public static MovieResultsPage getFavorite (String user, String password) {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TokenSession authentication = tmdbApi
                .getAuthentication().getSessionLogin(user, password);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        Log.d("Thread", account.getFavoriteMovies(token, accountID).getResults().get(0).getOverview());
        return  account.getFavoriteMovies(token, accountID);

    }

    public static MovieResultsPage getRated(String user, String password, int pagina){
        TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                .getAuthentication().getSessionLogin(user, password);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        return  account.getRatedMovies(token, accountID, pagina);

    }

    public static MovieResultsPage getWatchList(String user, String password, int pagina){
        TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                .getAuthentication().getSessionLogin(user, password);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        return  account.getWatchListMovies(token, accountID, pagina);
    }

}
