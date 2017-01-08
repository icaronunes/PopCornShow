package domain;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by icaro on 03/01/17.
 */

public class NetflixRoulette {

    private static final String API_URL = "http://netflixroulette.net/api/api.php?";

    public NetflixRoulette() {
    }

    public final String getAllData(String title) throws JSONException, IOException {
        return this.getAllData(title, 0);
    }

    public String getAllData(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL+ "title=" + title + "&year=" + year);
        message = json.toString();
        return message;
    }

    public double getAPIVersion() {
        return 1.0D; // ??????????
    }

    public final String getMediaCast(String title) throws JSONException, IOException {
        return this.getMediaCast(title, 0);
    }

    public String getMediaCast(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";                          //http://netflixroulette.net/api/api.php?title=Attack%20on%20titan
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL+ "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("show_cast").toString();
        }

        return message;
    }

    public final String getMediaCategory(String title) throws JSONException, IOException {
        return this.getMediaCategory(title, 0);
    }

    public String getMediaCategory(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("category").toString();
        }

        return message;
    }

    public final String getMediaDirector(String title) throws JSONException, IOException {
        return this.getMediaDirector(title, 0);
    }

    public String getMediaDirector(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("director").toString();
        }

        return message;
    }

    public final String getMediaPoster(String title) throws JSONException, IOException {
        return this.getMediaPoster(title, 0);
    }

    public String getMediaPoster(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("poster").toString();
        }

        return message;
    }

    public final String getMediaRating(String title) throws JSONException, IOException {
        return this.getMediaRating(title, 0);
    }

    public String getMediaRating(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("rating").toString();
        }

        return message;
    }

    public final String getMediaReleaseYear(String title) throws JSONException, IOException {
        return this.getMediaReleaseYear(title, 0);
    }

    public String getMediaReleaseYear(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("release_year").toString();
        }

        return message;
    }

    public final String getMediaSummary(String title) throws JSONException, IOException {
        return this.getMediaSummary(title, 0);
    }

    public String getMediaSummary(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("summary").toString();
        }

        return message;
    }

    public final String getMediaType(String title) throws JSONException, IOException {
        return this.getMediaReleaseYear(title, 0);
    }

    public String getMediaType(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("mediatype").toString();
        }

        return message;
    }

    public final String getNetflixId(String title) throws JSONException, IOException {
        return this.getNetflixId(title, 0);
    }

    public String getNetflixId(String title, int year) throws JSONException, IOException {
        title = title.replace(" ", "%20");
        String message = "place";
        JSONObject json = RouletteFunctions.readJsonFromUrl(API_URL + "title=" + title + "&year=" + year);
        if(json.has("error")) {
            message = "Unable to locate data";
        } else {
            message = json.get("show_id").toString();
        }

        return message;
    }
}
