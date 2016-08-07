package activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Window;

import java.util.List;

import adapter.PosterGridAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

/**
 * Created by icaro on 28/07/16.
 */

public class PosterGridActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    int id_filme;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poster_grid);
        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_poster_grid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();
        Log.d("PosterGridActivity", "onCreate ");
    }


    public class TMDVAsync extends AsyncTask<Void, Void, List<Artwork>> {


        @Override
        protected List<Artwork> doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("PosterGridActivity", "doInBackground: -> " + id_filme);
            MovieDb movieDb = movies.getMovie(id_filme, getString(R.string.IDIOMAS), TmdbMovies.MovieMethod.images);

            return movieDb.getImages(ArtworkType.POSTER);
        }

        @Override
        protected void onPostExecute(List<Artwork> artworks) {
            Log.d("PosterGridActivity", "onCreate ");
            recyclerView.setAdapter(new adapter.PosterGridAdapter(PosterGridActivity.this, artworks, id_filme));

        }
    }

}
