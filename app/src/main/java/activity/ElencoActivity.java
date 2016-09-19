package activity;

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
import android.widget.TextView;

import adapter.ElencoAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.movie;
import static br.com.icaro.filme.R.string.movieDb;

/**
 * Created by icaro on 24/07/16.
 */
public class ElencoActivity extends BaseActivity {
    RecyclerView recyclerView;
    TextView text_elenco_no_internet;
    LinearLayout linear_search_layout;
    int id;
    int season = -100;
    ProgressBar progressBar;
    Credits creditsTvShow;
    MovieDb movies;
    Multi.MediaType mediaType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elenco);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getIntExtra(Constantes.ID, 0);
        mediaType = (Multi.MediaType) getIntent().getSerializableExtra(Constantes.MEDIATYPE);
        season = getIntent().getIntExtra(Constantes.TVSEASONS,-100);
        Log.d("ElencoActivity", " " + id);
        Log.d("ElencoActivity", "oncreate " + season);
        Log.d("ElencoActivity", "" + mediaType.toString());

        String title = getIntent().getStringExtra(Constantes.NOME);
        getSupportActionBar().setTitle(title);

        recyclerView = (RecyclerView) findViewById(R.id.elenco_recyckeview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        text_elenco_no_internet = (TextView) findViewById(R.id.text_elenco_no_internet);
        linear_search_layout = (LinearLayout) findViewById(R.id.linear_elenco_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);


        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            TMDVAsync tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
        } else {
            text_elenco_no_internet.setVisibility(View.VISIBLE);
            snack();
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
            Log.d("ElencoActivity", "ID " + id);

            if (Multi.MediaType.TV_SERIES.equals(mediaType) && season != -100){
                Log.d("ElencoActivity", "" + season);
                creditsTvShow = FilmeService.getTmdbTvSeasons().getSeason(id, season, "en", TmdbTvSeasons.SeasonMethod.credits).getCredits();
            }

            if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
                creditsTvShow = FilmeService.getTmdbTvShow().getCredits(id, "en");
            }

            if (Multi.MediaType.MOVIE.equals(mediaType)) {
                TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                movies = tmdbMovies.getMovie(id, "en", TmdbMovies.MovieMethod.credits);
                Log.d("ElencoActivity", "" + movies.getCredits().getCast().size());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid){
            Log.d("ElencoActivity", "onPostExecute");
            progressBar.setVisibility(View.GONE);
            if (Multi.MediaType.MOVIE.equals(mediaType)) {
                recyclerView.setAdapter(new ElencoAdapter(ElencoActivity.this, movies.getCredits().getCast()));
            }
            if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
                recyclerView.setAdapter(new ElencoAdapter(ElencoActivity.this, creditsTvShow.getCast()));
            }

        }
    }
}