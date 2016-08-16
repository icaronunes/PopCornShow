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

import adapter.SimilaresAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 12/08/16.
 */
public class SimilaresActivity extends BaseActivity{
    RecyclerView recyclerView;
    TextView text_similares_no_internet;
    LinearLayout linear_similares_layout;
    int id_filme;
    ProgressBar progressBar;
    MovieResultsPage similares;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similares);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
        Log.d("ElencoActivity", " " + id_filme);

        String title = getIntent().getStringExtra(Constantes.NOME_FILME);
        getSupportActionBar().setTitle(title);

        recyclerView = (RecyclerView) findViewById(R.id.similares_recyckeview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        text_similares_no_internet = (TextView) findViewById(R.id.text_similares_no_internet);
        linear_similares_layout = (LinearLayout) findViewById(R.id.linear_similares_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);


        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();
        } else {
            text_similares_no_internet.setVisibility(View.VISIBLE);
            snack();
        }
    }

    protected void snack() {
        Snackbar.make(linear_similares_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            text_similares_no_internet.setVisibility(View.GONE);
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

        @Override
        protected Void doInBackground(Void... voids) {
            TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
            similares = tmdbMovies.getSimilarMovies(id_filme, getResources().getString(R.string.IDIOMAS), 1);
            return null;
        }

        @Override
        protected void onPostExecute(Void Avoid) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new SimilaresAdapter(SimilaresActivity.this, similares.getResults()));
        }
    }

}
