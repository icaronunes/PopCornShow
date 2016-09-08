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

import adapter.CrewsAdapter;
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

public class CrewsActivity extends BaseActivity {

    RecyclerView recyclerView;
    TextView text_crews_no_internet;
    LinearLayout linear_crews_layout;
    int id;
    Multi.MediaType mediaType;
    ProgressBar progressBar;
    //Credits creditsTvShow;
    MovieDb movies;
    int season = -100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crews);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id = getIntent().getIntExtra(Constantes.ID, 0);
        mediaType = (Multi.MediaType) getIntent().getSerializableExtra(Constantes.MEDIATYPE);
        season = getIntent().getIntExtra(Constantes.TVSEASONS, -100);
        Log.d("CrewsActivity", " " + id);
        String title = getIntent().getStringExtra(Constantes.NOME);
        getSupportActionBar().setTitle(title);

        recyclerView = (RecyclerView) findViewById(R.id.crews_recyckeview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        text_crews_no_internet = (TextView) findViewById(R.id.text_crews_no_internet);
        linear_crews_layout = (LinearLayout) findViewById(R.id.linear_crews_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);


        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            TMDVAsync tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
        } else {
            text_crews_no_internet.setVisibility(View.VISIBLE);
            snack();
        }

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

    protected void snack() {
        Snackbar.make(linear_crews_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            text_crews_no_internet.setVisibility(View.GONE);
                            TMDVAsync tmdvAsync = new TMDVAsync();
                            tmdvAsync.execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
        Credits creditsTvShow;
        @Override
        protected Void doInBackground(Void... voids) {
            if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
                creditsTvShow = FilmeService.getTmdbTvShow().getCredits(id, "en");
                Log.d("CrewsActivity", "IF " + creditsTvShow.getCrew().size());
            }
            if (Multi.MediaType.TV_SERIES.equals(mediaType) && season != -100){
                creditsTvShow = FilmeService.getTmdbTvSeasons()
                        .getSeason(id, season,"en", TmdbTvSeasons.SeasonMethod.credits)
                        .getCredits();
                Log.d("CrewsActivity", "-100 " + creditsTvShow.getCrew().size());
            }

            if (Multi.MediaType.MOVIE.equals(mediaType)) {
                TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                movies = tmdbMovies.getMovie(id, "en", TmdbMovies.MovieMethod.credits);
                Log.d("CrewsActivity", "" + movies.getCredits().getCast().size());
            }
            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid){
            Log.d("CrewsActivity", "onPostExecute");
            progressBar.setVisibility(View.GONE);
            if (Multi.MediaType.MOVIE.equals(mediaType)) {
                recyclerView.setAdapter(new CrewsAdapter(CrewsActivity.this, movies.getCredits().getCrew()));
            }
            if (Multi.MediaType.TV_SERIES.equals(mediaType)) {
                Log.d("CrewsActivity", "IF " + creditsTvShow.getCrew().size());
                recyclerView.setAdapter(new CrewsAdapter(CrewsActivity.this, creditsTvShow.getCrew()));
            }

        }
    }
}
