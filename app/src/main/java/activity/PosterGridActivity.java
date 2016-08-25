package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Window;

import java.util.List;

import br.com.icaro.filme.R;

import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;

/**
 * Created by icaro on 28/07/16.
 */

public class PosterGridActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    TvSeries series;
    MovieDb movieDb;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.poster_grid);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_poster_grid);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new GridLayoutManager(getBaseContext(), 2));
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            if (bundle.getSerializable(Constantes.SERIE) != null) {
                series = (TvSeries) bundle.getSerializable(Constantes.SERIE);
                List<Artwork> artworks = series.getImages().getPosters();
                recyclerView.setAdapter(new adapter.PosterGridAdapter(PosterGridActivity.this, artworks, series.getId()));
            }
            if (bundle.getSerializable(Constantes.FILME) != null) {
                movieDb = (MovieDb) bundle.getSerializable(Constantes.FILME);
                List<Artwork> artworks = movieDb.getImages(ArtworkType.POSTER);
                recyclerView.setAdapter(new adapter.PosterGridAdapter(PosterGridActivity.this, artworks, movieDb.getId()));
            }
        }
    }

}
