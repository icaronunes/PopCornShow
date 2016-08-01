package domian;

import android.util.Log;

import java.util.List;

import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbCollections;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.config.Account;
import info.movito.themoviedbapi.model.config.TokenSession;
import info.movito.themoviedbapi.model.core.AccountID;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.SessionToken;
import utils.Config;

import static android.R.attr.id;

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
        TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                .getAuthentication().getSessionLogin(user, password);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        return account;//.getFavoriteMovies(token, accountID);

    }

    public static MovieResultsPage getFavorite (String user, String password) {
        TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                .getAuthentication().getSessionLogin(user, password);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, password).getId());
        return  account.getFavoriteMovies(token, accountID);
    }

}
