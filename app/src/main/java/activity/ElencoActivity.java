package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import adapter.ElencoAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.credits;

/**
 * Created by icaro on 24/07/16.
 */
public class ElencoActivity extends BaseActivity {
    RecyclerView recyclerView;
    TextView text_elenco_no_internet;
    LinearLayout linear_search_layout;
    int id_filme;
    ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elenco);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
        Log.d("ElencoActivity", " " + id_filme);

        String title = getIntent().getStringExtra(Constantes.NOME_FILME);
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


    public class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {


        @Override
        protected MovieDb doInBackground(Void... voids) {
            TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
            MovieDb movies = tmdbMovies.getMovie(id_filme, "en", credits);
            Log.d("ElencoActivity", "" + movies.getCredits().getCast().size());
            return movies;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            Log.d("ElencoActivity", "onPostExecute");
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new ElencoAdapter(ElencoActivity.this, movieDb.getCredits().getCast()));
        }
    }
}