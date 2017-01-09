package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import adapter.ActorNetflixAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.Netflix;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 08/01/17.
 */
public class ActivityPersonNetflix extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout linear_person_netflix_layout;
    private String nome;

    private String TAG = this.getClass().getName();
    private List<Netflix> netflixs;
    private TextView text_netflix_empty;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_netflix);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getExtras();

        recyclerView = (RecyclerView) findViewById(R.id.person_netflix_recyckeview);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        linear_person_netflix_layout = (LinearLayout) findViewById(R.id.linear_person_netflix_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        text_netflix_empty = (TextView) findViewById(R.id.text_netflix_empty);

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
        nome = getIntent().getStringExtra(Constantes.NOME_PERSON);
        getSupportActionBar().setTitle(nome);
    }

    protected void snack() {
        Snackbar.make(linear_person_netflix_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
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
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
           netflixs = new ArrayList<>();
            try {
                Netflix[] netflixActors = FilmeService.getNetflixActor(nome);
                Collections.addAll(netflixs, netflixActors);

                Netflix[] netflixDirector = FilmeService.getNetflixDirector(nome);
                Collections.addAll(netflixs, netflixDirector);

            } catch (Exception e) {
                Log.d(TAG, e.getMessage());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (netflixs != null && netflixs.size() > 0) {
                recyclerView.setAdapter(new ActorNetflixAdapter(ActivityPersonNetflix.this,
                        netflixs));
                progressBar.setVisibility(View.GONE);
            } else {
                text_netflix_empty.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }
    }
}
