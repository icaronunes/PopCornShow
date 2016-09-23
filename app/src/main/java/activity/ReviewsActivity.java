package activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.List;
import java.util.Locale;

import adapter.ReviewsAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Reviews;
import utils.Constantes;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;
import static java.util.ResourceBundle.getBundle;

public class ReviewsActivity extends BaseActivity {
    int id_filme;
    RecyclerView recyclerView;
    MovieDb movieDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.NOME_FILME));
        id_filme = getIntent().getExtras().getInt(Constantes.FILME_ID);
        Log.d("ReviewsActivity", "onCreate " + id_filme);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("FilmeInfoFragment", "doInBackground: -> " + id_filme);

            movieDb = movies.getMovie(id_filme, Locale.getDefault().toLanguageTag() , reviews);
            movieDb.getReviews().addAll(movies.getMovie(id_filme, "en, null", reviews).getReviews());
            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            if (!movieDb.getReviews().isEmpty());
            recyclerView.setAdapter(new ReviewsAdapter(getBaseContext(), movieDb.getReviews()));
        }
    }

}
