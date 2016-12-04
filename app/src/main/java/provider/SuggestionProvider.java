package provider;

import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;

public class SuggestionProvider extends ContentProvider {

    private TmdbSearch.MultiListResultsPage multis;
    private String id;
    private String mediaType;
    private Bundle bundle;

    @Override
    public boolean onCreate() {
       // Log.d("SuggestionProvider", "Entrou");
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        String query1 = uri.getLastPathSegment().toLowerCase();
      //  Log.d("SuggestionProvider", query1);

        multis = new TmdbSearch(new TmdbApi("fb14e77a32282ed59a8122a266010b70")).searchMulti(query1, "en", 1);

        MatrixCursor cursor = new MatrixCursor(
                new String[]{
                        BaseColumns._ID,
                        SearchManager.SUGGEST_COLUMN_TEXT_1,
                        SearchManager.SUGGEST_COLUMN_TEXT_2,
                        SearchManager.SUGGEST_COLUMN_INTENT_EXTRA_DATA,
                        SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID
                }
        );
        if (multis != null) {
            String query = uri.getLastPathSegment().toUpperCase();
            int limit = Integer.parseInt(uri.getQueryParameter(SearchManager.SUGGEST_PARAMETER_LIMIT));

            int lenght = multis.getResults().size();
            for (int position = 0; position < lenght && cursor.getCount() < limit; position++) {
                String nome = "";
                String data = "";
                bundle = new Bundle();


                if (multis.getResults().get(position).getMediaType().equals(Multi.MediaType.MOVIE)) {
                   // Log.d("SuggestionProvider", String.valueOf(Multi.MediaType.MOVIE));
                    MovieDb movieDb = (MovieDb) multis.getResults().get(position);
                    id = String.valueOf(movieDb.getId());
                    nome = movieDb.getTitle();
                    data = movieDb.getReleaseDate();
                    mediaType = movieDb.getMediaType().name();

                }
                if (multis.getResults().get(position).getMediaType().equals(Multi.MediaType.TV_SERIES)) {
                    TvSeries series = (TvSeries) multis.getResults().get(position);
                   // Log.d("SuggestionProvider", String.valueOf(Multi.MediaType.TV_SERIES));
                    data = series.getFirstAirDate();
                    nome = series.getName();
                    id = String.valueOf(series.getId());
                    mediaType = series.getMediaType().name();

                }
                if (multis.getResults().get(position).getMediaType().equals(Multi.MediaType.PERSON)) {
                  //  Log.d("SuggestionProvider", String.valueOf(Multi.MediaType.PERSON));
                    Person person = (Person) multis.getResults().get(position);
                    mediaType = Multi.MediaType.PERSON.name();
                    nome = person.getName();
                    id = String.valueOf(person.getId() + "/"+person.getName());
                    data = "";
                }

                //Log.d("SuggestionProvider", nome);
                if (nome.toUpperCase().contains(query)) {
                    cursor.addRow(new Object[]{position, nome, data, id, mediaType});
                }
            }
        }
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(Uri uri, String s, String[] strings) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String s, String[] strings) {
        return 0;
    }


}