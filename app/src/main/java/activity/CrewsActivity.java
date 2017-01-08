package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import adapter.CrewsAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;
import utils.UtilsFilme;



public class CrewsActivity extends BaseActivity {

    RecyclerView recyclerView;
    LinearLayout linear_crews_layout;
    int id;
    Multi.MediaType mediaType;
    ProgressBar progressBar;
    //Credits creditsTvShow;
    MovieDb movies;
    int season = -100;
    String title;
    private String TAG = this.getClass().getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crews);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getExtras();
        getSupportActionBar().setTitle(title);
        recyclerView = (RecyclerView) findViewById(R.id.crews_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linear_crews_layout = (LinearLayout) findViewById(R.id.linear_crews_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adview.loadAd(adRequest);


        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();
        } else {
            snack();
        }

    }

    private void getExtras() {
        if (getIntent().getBooleanExtra("notification" , true)) {
            id = getIntent().getIntExtra(Constantes.ID, 0);
            mediaType = (Multi.MediaType) getIntent().getSerializableExtra(Constantes.MEDIATYPE);
            season = getIntent().getIntExtra(Constantes.TVSEASONS, -100);
            title = getIntent().getStringExtra(Constantes.NOME);
        } else {
            id = getIntent().getIntExtra(Constantes.ID,0);
            String media  = getIntent().getStringExtra(Constantes.MEDIATYPE);
            switch (media) {

                case "tv": {
                    mediaType = (Multi.MediaType.TV_SERIES);
                    season = Integer.parseInt(getIntent().getStringExtra(Constantes.TVSEASONS));
                    break;
                }
                case "movie": {
                    mediaType = (Multi.MediaType.MOVIE);
                    break;
                }
            }
            title = getIntent().getStringExtra(Constantes.NOME);
        }
    }

    protected void snack() {
        Snackbar.make(linear_crews_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            new TMDVAsync().execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return true;
    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
        Credits creditsTvShow;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
                    creditsTvShow = FilmeService.getTmdbTvShow().getCredits(id, "en");
                    // Log.d("CrewsActivity", "IF " + creditsTvShow.getCrew().size());
                }
                if (Multi.MediaType.TV_SERIES.equals(mediaType) && season != -100) {
                    creditsTvShow = FilmeService.getTmdbTvSeasons()
                            .getSeason(id, season, "en", TmdbTvSeasons.SeasonMethod.credits)
                            .getCredits();
                    // Log.d("CrewsActivity", "-100 " + creditsTvShow.getCrew().size());
                }

                if (Multi.MediaType.MOVIE.equals(mediaType)) {
                    TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                    movies = tmdbMovies.getMovie(id, "en", TmdbMovies.MovieMethod.credits);
                    // Log.d("CrewsActivity", "" + movies.getCredits().getCast().size());
                }
                return null;
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
                FirebaseCrash.report(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CrewsActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid){
         //   Log.d("CrewsActivity", "onPostExecute");
            progressBar.setVisibility(View.GONE);
            if (Multi.MediaType.MOVIE.equals(mediaType)) {
                recyclerView.setAdapter(new CrewsAdapter(CrewsActivity.this,
                        movies != null ? movies.getCredits().getCrew() : null));
            }
            if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
            //    Log.d("CrewsActivity", "IF " + creditsTvShow.getCrew().size());
                recyclerView.setAdapter(new CrewsAdapter(CrewsActivity.this,
                       creditsTvShow != null ? creditsTvShow.getCrew() : null));
            }
        }
    }
}
