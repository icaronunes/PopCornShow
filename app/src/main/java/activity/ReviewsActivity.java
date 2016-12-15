package activity;


import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

import adapter.ReviewsAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;

public class ReviewsActivity extends BaseActivity {
    int id_filme;
    RecyclerView recyclerView;
    MovieDb movieDb = null;
    private String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.NOME_FILME));
        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
      //  Log.d("ReviewsActivity", "onCreate " + id_filme);
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
            try {
                TmdbMovies movies = FilmeService.getTmdbMovies();
                movieDb = movies.getMovie(id_filme, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry(), reviews);
                movieDb.getReviews().addAll(movies.getMovie(id_filme, "en, null", reviews).getReviews());
                return movieDb;
            } catch (Exception e){
                FirebaseCrash.report(e);
                Log.d(TAG, e.getMessage());
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ReviewsActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return  null;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            if (movieDb == null) {return;}
            if (!movieDb.getReviews().isEmpty()) {
                recyclerView.setAdapter(new ReviewsAdapter(ReviewsActivity.this,
                        movieDb.getReviews()));
            }
        }
    }

}
