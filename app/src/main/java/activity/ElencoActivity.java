package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;

import adapter.ElencoAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 24/07/16.
 */
public class ElencoActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private TextView text_elenco_no_internet;
    private LinearLayout linear_search_layout;
    private int id;
    private int season = -100;
    private ProgressBar progressBar;
    private Credits creditsTvShow;
    private MovieDb movies;
    private Multi.MediaType mediaType;
    private String title;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elenco);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getExtras();

        getSupportActionBar().setTitle(title);

        recyclerView = (RecyclerView) findViewById(R.id.elenco_recyckeview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        text_elenco_no_internet = (TextView) findViewById(R.id.text_elenco_no_internet);
        linear_search_layout = (LinearLayout) findViewById(R.id.linear_elenco_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);

//        AdView adview = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                .build();
//        adview.loadAd(adRequest);


        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            TMDVAsync tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
        } else {
            text_elenco_no_internet.setVisibility(View.VISIBLE);
            snack();
        }

    }

    private void getExtras() {
        if (getIntent().getBooleanExtra("notification" , true)) {
            id = getIntent().getIntExtra(Constantes.INSTANCE.getID(), 0);
            mediaType = (Multi.MediaType) getIntent().getSerializableExtra(Constantes.INSTANCE.getMEDIATYPE());
            season = getIntent().getIntExtra(Constantes.INSTANCE.getTVSEASONS(), -100);
            title = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
        } else {
            id = getIntent().getIntExtra(Constantes.INSTANCE.getID(),0);
            String media  = getIntent().getStringExtra(Constantes.INSTANCE.getMEDIATYPE());
            switch (media) {

                case "tv": {
                    mediaType = (Multi.MediaType.TV_SERIES);
                    season = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getTVSEASONS()));
                    break;
                }
                case "movie": {
                    mediaType = (Multi.MediaType.MOVIE);
                    break;
                }
            }
            title = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    protected void snack() {
        Snackbar.make(linear_search_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            text_elenco_no_internet.setVisibility(View.GONE);
                            TMDVAsync tmdvAsync = new TMDVAsync();
                            tmdvAsync.execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {



        @Override
        protected Void doInBackground(Void... voids) {
          //  Log.d("ElencoActivity", "ID " + id);
            try {
                if (Multi.MediaType.TV_SERIES.equals(mediaType) && season != -100) {
                    //      Log.d("ElencoActivity", "" + season);
                    creditsTvShow = FilmeService.getTmdbTvSeasons().getSeason(id, season, "en", TmdbTvSeasons.SeasonMethod.credits).getCredits();
                }

                if (Multi.MediaType.TV_SERIES.equals(mediaType) && season == -100) {
                    creditsTvShow = FilmeService.getTmdbTvShow().getCredits(id, "en");
                }

                if (Multi.MediaType.MOVIE.equals(mediaType)) {
                    TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                    movies = tmdbMovies.getMovie(id, "en", TmdbMovies.MovieMethod.credits);
                    //    Log.d("ElencoActivity", "" + movies.getCredits().getCast().size());
                }
                return null;
            } catch (Exception e){
                FirebaseCrash.report(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ElencoActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid){
          //  Log.d("ElencoActivity", "onPostExecute");
            progressBar.setVisibility(View.GONE);
            if (Multi.MediaType.MOVIE.equals(mediaType)) {
                recyclerView.setAdapter(new ElencoAdapter(ElencoActivity.this,
                        movies != null ?movies.getCredits().getCast() : null));
            }
            if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
                recyclerView.setAdapter(new ElencoAdapter(ElencoActivity.this,
                        creditsTvShow != null ? creditsTvShow.getCast() : null));
            }

        }
    }
}