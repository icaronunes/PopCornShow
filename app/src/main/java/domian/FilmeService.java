package domian;

import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.crash.FirebaseCrash;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import applicaton.FilmeApplication;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbCollections;
import info.movito.themoviedbapi.TmdbCompany;
import info.movito.themoviedbapi.TmdbLists;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.TmdbTvEpisodes;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.config.Account;
import info.movito.themoviedbapi.model.config.Timezone;
import info.movito.themoviedbapi.model.config.TokenSession;
import info.movito.themoviedbapi.model.core.AccountID;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import info.movito.themoviedbapi.model.core.ResponseStatusException;
import info.movito.themoviedbapi.model.core.SessionToken;
import info.movito.themoviedbapi.model.people.PersonCredits;
import info.movito.themoviedbapi.tools.ApiUrl;
import info.movito.themoviedbapi.tools.MovieDbException;
import info.movito.themoviedbapi.tools.RequestMethod;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.Config;
import utils.Prefs;

import static android.R.attr.id;
import static info.movito.themoviedbapi.TmdbAccount.PARAM_SESSION;
import static info.movito.themoviedbapi.TmdbAccount.TMDB_METHOD_ACCOUNT;
import static info.movito.themoviedbapi.TmdbPeople.TMDB_METHOD_PERSON;

/**
 * Created by icaro on 01/07/16.
 */

public class FilmeService {

    private static final Collection<Integer> SUCCESS_STATUS_CODES = Arrays.asList(
            1, // Success
            12, // The item/record was updated successfully.
            13 // The item/record was updated successfully.
    );

    public static TmdbLists getTmdbList() {
        return new TmdbApi(Config.TMDB_API_KEY).getLists();
        //Metodo não aceita TVShow
    }

    public static List<Timezone> getTimeZone() {
        return new TmdbApi(Config.TMDB_API_KEY).getTimezones();
        //Metodo não aceita TVShow
    }

    public static TmdbSearch getTmdbSearch() {
        Log.d("SuggestionProvider", "getTmdbSearch");
        return new TmdbApi(Config.TMDB_API_KEY).getSearch();
    }

    public static TmdbTV getTmdbTvShow() {
        return new TmdbApi(Config.TMDB_API_KEY).getTvSeries();
    }

    public static TmdbTvSeasons getTmdbTvSeasons() {
        return new TmdbApi(Config.TMDB_API_KEY).getTvSeasons();
    }

    public static TmdbTvEpisodes getTmdbTvEpisodes() {
        return new TmdbApi(Config.TMDB_API_KEY).getTvEpisodes();
    }


    public static TmdbMovies getTmdbMovies() {
        TmdbMovies movies = new TmdbApi(Config.TMDB_API_KEY).getMovies();
        return movies;
    }

    public static TmdbCompany getTmdbCompany() {
        TmdbCompany company = new TmdbApi(Config.TMDB_API_KEY).getCompany();
        return company;
    }

    public static TmdbPeople getTmdbPerson() {
        TmdbPeople people = new TmdbApi(Config.TMDB_API_KEY).getPeople();
        return people;
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
            if (FirebaseCrash.isSingletonInitialized()) {
                FirebaseCrash.report(e);
            }
        }
        Log.d("getAccount", "Account Nulo");
        return null;
    }


    public static TmdbAccount getTmdbAccount() {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TmdbAccount account = tmdbApi.getAccount();
        return account;

    }

    public static TmdbAccount.MovieListResultsPage getListAccount(String languegem, int pagina) {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();

            TokenSession authentication = tmdbApi
                    .getAuthentication().getSessionLogin(user, pass);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            AccountID accountID = new AccountID(getAccount(user, pass).getId());
            return account.getLists(token, accountID, languegem, pagina);

        }
        return null;
    }

    public static ResponseStatus addOrRemoverWatchList(Integer id_filme, boolean opcao, TmdbAccount.MediaType mediaType) {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TokenSession authentication = tmdbApi
                .getAuthentication().getSessionLogin(user, pass);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, pass).getId());
        if (opcao) {
            ResponseStatus status = account.addToWatchList(token, accountID, id_filme, mediaType);
            Log.d("addOrRemoverWatchList", status.toString());
            return status;
        } else {
            ResponseStatus status = account.removeFromWatchList(token, accountID, id_filme, mediaType);
            Log.d("addOrRemoverWatchList", status.toString());
            return status;
        }

    }

    public static ResponseStatus addOrRemoverFavorite(Integer id, boolean opcao, TmdbAccount.MediaType mediaType) {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        TokenSession authentication = tmdbApi
                .getAuthentication().getSessionLogin(user, pass);
        String session = authentication.getSessionId();
        SessionToken token = new SessionToken(session);
        TmdbAccount account = tmdbApi.getAccount();
        AccountID accountID = new AccountID(getAccount(user, pass).getId());
        ResponseStatus status = null;
        if (opcao) {
            try {
                status = account.addFavorite(token, accountID, id, mediaType);
            } catch (Exception e) {
                Log.d("addOrRemoverFavoriteM", e.toString());

            } finally {

//                Log.d("addOrRemoverFavoriteM", status.toString());
//                Log.d("addOrRemoverFavoriteM", String.valueOf(status.getStatusCode()));
//                Log.d("addOrRemoverFavoriteM", String.valueOf(status.getStatusMessage()));
            }
            return status;
        } else {
            try {
                status = account.removeFavorite(token, accountID, id, mediaType);
            } catch (Exception e) {
                Log.d("addOrRemoverFavoriteM", e.toString());

            } finally {

//                Log.d("addOrRemoverFavoriteM", status.toString());
//                Log.d("addOrRemoverFavoriteM", String.valueOf(status.getStatusCode()));
//                Log.d("addOrRemoverFavoriteM", String.valueOf(status.getStatusMessage()));
            }
            return status;
        }
    }


    public static Lista getLista(String stringExtra) {
        OkHttpClient client = new OkHttpClient();
        String url = "https://api.themoviedb.org/3/list/" + stringExtra +
                "?api_key=fb14e77a32282ed59a8122a266010b70&language=en-US&order_by=release.date.asc";
        Request request = new Request.Builder()
                .url(url)
                .build();
        Lista items = null;
        try {
            Response response = client.newCall(request).execute();
            Log.d("domian.Lista", String.valueOf(response.body().charStream()));
            items =  parseJSON(response);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return items;

    }

    public static Lista parseJSON(Response response) {
        Gson gson = new GsonBuilder().create();
        Lista items = null;
        items = gson.fromJson(response.body().charStream(), Lista.class);
            //    Log.d("domian.Lista", items.getName());
        return items;
    }

    public static TvResultsPage getTotalFavoriteTvShow() {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        TvResultsPage favoritosTvshow;
        if (user != null && pass != null) {
            favoritosTvshow = getFavoriteTvShow(user, pass, 1);
            if (favoritosTvshow != null) {
                if (favoritosTvshow.getTotalPages() > 1) {
                    for (int i = 2; i <= favoritosTvshow.getTotalPages(); i++) {
                        favoritosTvshow.getResults().addAll(getFavoriteTvShow(user, pass, i).getResults());
                    }
                }
                Log.d("getTotalFavorite", "Total page" + favoritosTvshow.getTotalPages());
            }
            return favoritosTvshow;
        }
        return null;
    }

    public static MovieResultsPage getTotalFavorite() {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        MovieResultsPage favoritos;
        if (user != null && pass != null) {
            favoritos = getFavorite(user, pass);
            if (favoritos != null) {
                if (favoritos.getTotalPages() > 1) {
                    for (int i = 2; i <= favoritos.getTotalPages(); i++) {
                        favoritos.getResults().addAll(FilmeService.getFavoriteMovies(user, pass, i).getResults());
                    }
                }
                Log.d("getTotalFavorite", "Total page" + favoritos.getTotalPages());
            }
            return favoritos;
        }
        return null;
    }

    public static TvResultsPage getFavoriteTvShow(String user, String password, int pagina) {
        //Criado novo getFavorito que aceita numero da pagina
        if (user != null && password != null) {
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TokenSession authentication = tmdbApi
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbAccount account = tmdbApi.getAccount();
            AccountID accountID = new AccountID(getAccount(user, password).getId());
            return account.getFavoriteSeries(token, accountID, pagina);
        }
        return null;
    }

    //Copia de TMDBAPI para pegar paginas do Favoritos

    public static MovieResultsPage getFavorite(String user, String password) {
        //Criado novo getFavorito que aceita numero da pagina
        if (user != null && password != null) {
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TokenSession authentication = tmdbApi
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbAccount account = tmdbApi.getAccount();
            AccountID accountID = new AccountID(getAccount(user, password).getId());
            return account.getFavoriteMovies(token, accountID);
        }
        return null;
    }

    public static MovieResultsPage getFavoriteMovies(String user, String password, Integer
            page) {
        if (user != null && password != null) {
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TokenSession authentication = tmdbApi
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_ACCOUNT, getAccount(user, password).getId(), "favorite/movies");
            apiUrl.addParam(PARAM_SESSION, token);
            apiUrl.addPage(page);

            return mapJsonResult(apiUrl, MovieResultsPage.class);
        }
        return null;
    }

    //Copiado da FrameWork - La não ha este metodo de combinar "trabalhos" de filme e Serie

    public static PersonCredits getPersonCreditsCombinado(int personId) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_PERSON, personId, "tv_credits");

        return mapJsonResult(apiUrl, PersonCredits.class);
    }

    public static <T> T mapJsonResult(ApiUrl apiUrl, Class<T> someClass) {
        return mapJsonResult(apiUrl, someClass, null);
    }

    public static <T> T mapJsonResult(ApiUrl apiUrl, Class<T> someClass, String jsonBody) {
        return mapJsonResult(apiUrl, someClass, jsonBody, RequestMethod.GET);
    }

    public static <T> T mapJsonResult(ApiUrl apiUrl, Class<T> someClass, String
            jsonBody, RequestMethod requestMethod) {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        final ObjectMapper jsonMapper = new ObjectMapper();
        String webpage = tmdbApi.requestWebPage(apiUrl, jsonBody, requestMethod);

//        System.out.println(webpage);

        try {
//            if(someClass.equals(TmdbTimezones.class)) {
//            	return (T) new TimezoneJsonMapper(webpage);
//            }

            // check if was error responseStatus
            ResponseStatus responseStatus = jsonMapper.readValue(webpage, ResponseStatus.class);
            // work around the problem that there's no status code for suspected spam names yet
            String suspectedSpam = "Unable to create list because: Description is suspected to be spam.";
            if (webpage.contains(suspectedSpam)) {
                responseStatus = new ResponseStatus(-100, suspectedSpam);
            }

            // if null, the json response was not a error responseStatus code, and but something else
            Integer statusCode = responseStatus.getStatusCode();
            if (statusCode != null && !SUCCESS_STATUS_CODES.contains(statusCode)) {
                throw new ResponseStatusException(responseStatus);
            }
            return jsonMapper.readValue(webpage, someClass);
        } catch (IOException ex) {
            throw new MovieDbException("mapping failed:\n" + webpage);
        }
    }

    //Copia de TMDBAPI para pegar paginas do Favoritos ^^^^^^^^


    public static MovieResultsPage getRatedMovie(String user, String password, int pagina) {
        if (!user.isEmpty() && !password.isEmpty()) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount tmdbAccount = tmdbApi.getAccount();
            Account account = tmdbApi.getAccount().getAccount(token);
            Log.d("MovieResultsPage", account.getName());
            Log.d("MovieResultsPage", user + "  " + password);
            AccountID accountID = new AccountID(account.getId());

            return tmdbAccount.getRatedMovies(token, accountID, pagina);

        }
        return null;
    }

    public static MovieResultsPage getRatedMovieListTotal() {
       String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
       String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (!user.isEmpty() && !pass.isEmpty()) {
            MovieResultsPage rated = FilmeService.getRatedMovie(user, pass, 1);
            if (rated != null) {
                if (rated.getTotalPages() > 1) {
                    for (int i = 2; i <= rated.getTotalPages(); i++) {
                        rated.getResults().addAll(FilmeService.getRatedMovie(user, pass, i).getResults());
                    }
                }
            }
            return rated;
        }
        return null;
    }

    public static TvResultsPage getRatedTvShow(String user, String password, int pagina) {
        if (user != null && password != null) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();
            AccountID accountID = new AccountID(getAccount(user, password).getId());
            return account.getRatedTvSeries(token, accountID, pagina);
        }
        return null;
    }

    public static TvResultsPage getRatedListTotal() {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            TvResultsPage rated = FilmeService.getRatedTvShow(user, pass, 1);
            if (rated != null) {
                if (rated.getTotalPages() > 1) {
                    for (int i = 2; i <= rated.getTotalPages(); i++) {
                        rated.getResults().addAll(FilmeService.getRatedTvShow(user, pass, i).getResults());
                    }
                }
            }
            return rated;
        }
        return null;
    }


    public static MovieResultsPage getWatchList(String user, String password, int pagina) {
        if (user != null && password != null) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();
            AccountID accountID = new AccountID(getAccount(user, password).getId());
            return account.getWatchListMovies(token, accountID, pagina);
        }
        return null;
    }

    public static MovieResultsPage getWatchListMovieTotal() {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            MovieResultsPage watch = getWatchList(user, pass, 1);
            if (watch != null) {
                if (watch.getTotalPages() > 1) {
                    for (int i = 2; i <= watch.getTotalPages(); i++) {
                        watch.getResults().addAll(getWatchList(user, pass, i).getResults());
                    }
                }
            }
            return watch;
        }
        return null;
    }

    public static TvResultsPage getWatchListTvshowTotal() {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            TvResultsPage watch = getWatchListTvShow(user, pass, 1);
            if (watch != null) {
                if (watch.getTotalPages() > 1) {
                    for (int i = 2; i <= watch.getTotalPages(); i++) {
                        watch.getResults().addAll(getWatchListTvShow(user, pass, i).getResults());
                    }
                }
            }

            return watch;
        }
        return null;
    }


    public static TvResultsPage getWatchListTvShow(String user, String password, int pagina) {
        if (user != null && password != null) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, password);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();
            AccountID accountID = new AccountID(getAccount(user, password).getId());
            return account.getWatchListSeries(token, accountID, pagina);
        }
        return null;
    }

    public static boolean setRatedMovie(int id_filme, float nota) {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, pass);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();
            if (nota != 0) {
                boolean status = account.postMovieRating(token, id_filme, (int) nota);
                Log.d("setRatedMovie", "" + status);
                return status;
            }
        }
        return false;
    }

    public static boolean setRatedTvShowEpsodio(int id_tvshow, int seasonid, int id_epsodio,
                                                float nota) {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, pass);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();
            if (nota != 0) {

                return account.postTvExpisodeRating(token, id_tvshow, seasonid, id_epsodio, (int) nota);
            }
        }
        return false;
    }

    public static boolean setRatedTvShow(int id_tvshow, float nota) {
        String user = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
        String  pass = Prefs.getString(FilmeApplication.getInstance().getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
        if (user != null && pass != null) {
            TokenSession authentication = new TmdbApi(Config.TMDB_API_KEY)
                    .getAuthentication().getSessionLogin(user, pass);
            String session = authentication.getSessionId();
            SessionToken token = new SessionToken(session);
            TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
            TmdbAccount account = tmdbApi.getAccount();

            if (nota != 0) {
                boolean status = account.postTvSeriesRating(token, id_tvshow, (int) nota);
                Log.d("setRatedMovie", "" + status);
                return status;
            }
        }
        return false;
    }

}
