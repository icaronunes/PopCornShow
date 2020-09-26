package domain;


import android.content.Context;

import androidx.annotation.Keep;

import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import utils.key.ApiKeys;


/**
 * Created by icaro on 01/07/16.
 */
@Keep
public class FilmeService {

    public static boolean ratedTvshowEpsodioGuest(int tvshowId, int temporada, int ep, int nota, Context context) {
        /// id 297762 - mulher maravilha
        String TMDBAPI = ApiKeys.TMDB_API_KEY;
        try {
            GuestSession guestSession = getGuestSession(context, TMDBAPI);
            if (guestSession != null) {

                String URL = "https://api.themoviedb.org/3/tv/"
                        + tvshowId + "/season/" + temporada + "/episode/" + ep + "/rating?api_key="
                        + TMDBAPI + "&guest_session_id=" + guestSession.getGuestSessionId();
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
            FirebaseCrashlytics.getInstance().recordException(e);
        }
        return false;
    }

    private static GuestSession getGuestSession(Context context, String TMDBAPI) {
        String URL = "https://api.themoviedb.org/3/authentication/guest_session/new?api_key=";
        URL += TMDBAPI;
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
