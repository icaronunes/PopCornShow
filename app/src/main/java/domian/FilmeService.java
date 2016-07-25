package domian;

import android.util.Log;

import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbCollections;
import info.movito.themoviedbapi.TmdbFind;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Config;

import static android.R.attr.id;
import static android.support.v7.widget.AppCompatDrawableManager.get;

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


    public static MovieDb getTmdbMovie(int id, String idioma) {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        Log.d("getTmdbMovie", "Id: " + id);
        return tmdbApi.getMovies().getMovie(id, idioma);
    }

    public static TmdbCollections getTmdbCollections() {
        TmdbApi tmdbApi = new TmdbApi(Config.TMDB_API_KEY);
        Log.d("getTmdbMovie", "Id: " + id);
        return tmdbApi.getCollections();
    }

}
