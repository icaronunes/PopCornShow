package domain;


import android.content.Context;

import androidx.annotation.Keep;

import com.crashlytics.android.Crashlytics;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
import info.movito.themoviedbapi.model.config.Timezone;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import info.movito.themoviedbapi.model.core.ResponseStatusException;
import info.movito.themoviedbapi.model.people.PersonCredits;
import info.movito.themoviedbapi.tools.ApiUrl;
import info.movito.themoviedbapi.tools.MovieDbException;
import info.movito.themoviedbapi.tools.RequestMethod;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.Config;

import static info.movito.themoviedbapi.TmdbPeople.TMDB_METHOD_PERSON;


/**
 * Created by icaro on 01/07/16.
 */
@Keep
public class FilmeService {

    private static final Collection<Integer> SUCCESS_STATUS_CODES = Arrays.asList(
            1, // Success
            12, // The item/record was updated successfully.
            13 // The item/record was updated successfully.
    );
    private final String TAG = FilmeService.class.getName();

    public static TmdbLists getTmdbList() {
        return new TmdbApi(Config.TMDB_API_KEY).getLists();
        //Metodo não aceita TVShow
    }

    public static List<Timezone> getTimeZone() {
        return new TmdbApi(Config.TMDB_API_KEY).getTimezones();
        //Metodo não aceita TVShow
    }

    public static TmdbSearch getTmdbSearch() {
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
        return new TmdbApi(Config.TMDB_API_KEY).getMovies();
    }

    public static TmdbCompany getTmdbCompany() {
        return new TmdbApi(Config.TMDB_API_KEY).getCompany();
    }

    public static TmdbPeople getTmdbPerson() {
        return new TmdbApi(Config.TMDB_API_KEY).getPeople();
    }

    public static TmdbCollections getTmdbCollections() {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        return tmdbApi.getCollections();
    }


    public static ListaJava getLista(String stringExtra) {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        String url = "https://api.themoviedb.org/3/list/" + stringExtra +
                "?api_key=" + Config.TMDB_API_KEY + "&language=en-US&sort_by=release.date.desc";
        Request request = new Request.Builder()
                .url(url)
                .build();
        ListaJava items = null;
        try {
            Response response = client.newCall(request).execute();
            //  Log.d("domian.ListaJava", String.valueOf(response.body().charStream()));
            items = parseJSONLista(response);
        } catch (IOException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return items;
    } //TODO: arrumar para api 4

    private static ListaJava parseJSONLista(Response response) {
        Gson gson = new GsonBuilder().create();
        ListaJava items = null;
        items = gson.fromJson(response.body().charStream(), ListaJava.class);
        //    Log.d("domian.ListaJava", items.getName());
        return items;
    }


    public static Imdb getImdb(String id) {

        final String url = "http://www.omdbapi.com/?i=" + id + "&tomatoes=true&r=json&apikey=thewdb"; //Api de alguem
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("Accept", "application/json")
                .build();

        try {
            Response response = client.newCall(request).execute();
            return parseJSONImdb(response);

        } catch (IOException e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
        return null;
    }

    private static Imdb parseJSONImdb(Response response) {
        Gson gson = new GsonBuilder().create();
        Imdb imdb = null;
        try {
            imdb = gson.fromJson(response.body().string(), Imdb.class);

        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return imdb;
    }

    public static PersonCredits getPersonCreditsCombinado(int personId) {
        ApiUrl apiUrl = new ApiUrl(TMDB_METHOD_PERSON, personId, "tv_credits");
        return mapJsonResult(apiUrl, PersonCredits.class);
    }


    //Copiado da FrameWork - La não ha este metodo de combinar "trabalhos" de filme e SERIE

    private static <T> T mapJsonResult(ApiUrl apiUrl, Class<T> someClass) {
        return mapJsonResult(apiUrl, someClass, null);
    }

    private static <T> T mapJsonResult(ApiUrl apiUrl, Class<T> someClass, String jsonBody) {
        return mapJsonResult(apiUrl, someClass, jsonBody, RequestMethod.GET);
    }

    private static <T> T mapJsonResult(ApiUrl apiUrl, Class<T> someClass, String
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


    public static boolean ratedMovieGuest(int movioId, int nota, Context context) {
        /// id 297762 - mulher maravilha
        try {
            GuestSession guestSession = getGuestSession(context);
            if (guestSession != null) {

                String URL = "https://api.themoviedb.org/3/movie/" + movioId + "/rating?api_key="
                        + Config.TMDB_API_KEY + "&guest_session_id=" + guestSession.getGuestSessionId();
                MediaType mediaType = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                RequestBody body = RequestBody.create(mediaType, "{\"value\":" + nota + "}");
                Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.isSuccessful();

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return false;
    }

    public static boolean ratedTvshowGuest(int tvshowId, int nota, Context context) {

        try {
            GuestSession guestSession = getGuestSession(context);
            if (guestSession != null) {

                String URL = "https://api.themoviedb.org/3/tv/" + tvshowId + "/rating?api_key="
                        + Config.TMDB_API_KEY + "&guest_session_id=" + guestSession.getGuestSessionId();
                MediaType mediaType = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient.Builder()
                        .readTimeout(10, TimeUnit.SECONDS)
                        .build();
                RequestBody body = RequestBody.create(mediaType, "{\"value\":" + nota + "}");
                Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.isSuccessful();

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return false;
    }

    public static boolean ratedTvshowEpsodioGuest(int tvshowId, int temporada, int ep, int nota, Context context) {
        /// id 297762 - mulher maravilha
        try {

            GuestSession guestSession = getGuestSession(context);
            if (guestSession != null) {

                String URL = "https://api.themoviedb.org/3/tv/"
                        + tvshowId + "/season/" + temporada + "/episode/" + ep + "/rating?api_key="
                        + Config.TMDB_API_KEY + "&guest_session_id=" + guestSession.getGuestSessionId();
                MediaType mediaType = MediaType.parse("application/json");
                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .build();
                RequestBody body = RequestBody.create(mediaType, "{\"value\":" + nota + "}");
                Request request = new Request.Builder()
                        .url(URL)
                        .post(body)
                        .build();
                Response response = client.newCall(request).execute();
                return response.isSuccessful();

            }
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return false;
    }

    private static GuestSession getGuestSession(Context context) {
        String URL = "https://api.themoviedb.org/3/authentication/guest_session/new?api_key=";
        URL += Config.TMDB_API_KEY;
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        Request request = new Request.Builder()
                .url(URL)
                .build();
        try {
            GuestSession guestSession;
            Gson gson = new GsonBuilder().create();
            Response response = client.newCall(request).execute();
            guestSession = gson.fromJson(response.body().string(), GuestSession.class);
            return guestSession;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
