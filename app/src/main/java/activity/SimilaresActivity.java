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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Locale;

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
    String title;
    private String TAG = this.getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_similares);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getExtras();

        getSupportActionBar().setTitle(title);

        recyclerView = (RecyclerView) findViewById(R.id.similares_recyckeview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        text_similares_no_internet = (TextView) findViewById(R.id.text_similares_no_internet);
        linear_similares_layout = (LinearLayout) findViewById(R.id.linear_similares_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();
        } else {
            text_similares_no_internet.setVisibility(View.VISIBLE);
            snack();
        }
    }

    private void getExtras() {
        if (getIntent().getAction() == null){
            id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
            title = getIntent().getStringExtra(Constantes.NOME_FILME);
        } else {
            id_filme = getIntent().getIntExtra(Constantes.FILME_ID,0);
            title = getIntent().getStringExtra(Constantes.NOME_FILME);
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
            try {
                TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                similares = tmdbMovies.getSimilarMovies(id_filme, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry(), 1);
                return null;
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
                FirebaseCrash.report(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(SimilaresActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void Avoid) {
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new SimilaresAdapter(SimilaresActivity.this, similares.getResults()));
        }
    }

}
