package activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Picasso;

import java.util.Locale;

import adapter.ProdutoraAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbCompany;
import info.movito.themoviedbapi.model.Company;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;
import utils.UtilsFilme;

import static domian.FilmeService.getTmdbCompany;

/**
 * Created by icaro on 10/08/16.
 */
public class ProdutoraActivity extends BaseActivity {
    Company company;
    int pagina = 1;
    TmdbCompany.CollectionResultsPage resultsPage;
    RecyclerView recyclerView;
    Multi.MediaType mediaType;
    LinearLayout info_layout;
    int id_produtora;
    TextView home_produtora, headquarters;
    ImageView top_img_produtora;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ProgressBar progressBar;
    SwipeRefreshLayout refreshLayout;
    TmdbCompany.CollectionResultsPage temp;
    CoordinatorLayout layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produtora_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        home_produtora = (TextView) findViewById(R.id.home_produtora);
        headquarters = (TextView) findViewById(R.id.headquarters);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh_produtora);
        info_layout = (LinearLayout) findViewById(R.id.info_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        top_img_produtora = (ImageView) findViewById(R.id.top_img_produtora);
        getExtras();
        recyclerView = (RecyclerView) findViewById(R.id.produtora_filmes_container);
        recyclerView.setLayoutManager(new GridLayoutManager(ProdutoraActivity.this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        InfoLayout();


        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        new TMDVAsync().execute();
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new TMDVAsync().execute();
            }
        });
        layout = (CoordinatorLayout) findViewById(R.id.coordinator_layout);


        if (!layout.hasFocus()) {
            info_layout.setVisibility(View.INVISIBLE);

        }


    }

    private void getExtras() {
        if (getIntent().getAction() == null){
            id_produtora = getIntent().getIntExtra(Constantes.PRODUTORA_ID, 0);
        } else {
            id_produtora = getIntent().getIntExtra(Constantes.PRODUTORA_ID, 0);
        }
    }

    private void setHeadquarters() {
        if (company.getHeadquarters() != null) {
            headquarters.setText(company.getHeadquarters());
            headquarters.setVisibility(View.VISIBLE);
        } else {
            headquarters.setVisibility(View.GONE);
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setHome() {
        if (company.getHomepage() != null) {
            home_produtora.setText(company.getHomepage());
            home_produtora.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(company.getHomepage()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }
            });
        } else {
            home_produtora.setVisibility(View.GONE);
        }

    }

    private void setImageTop() {
        if (company.getLogoPath() != null) {
            Picasso.with(this).load(UtilsFilme.getBaseUrlImagem(4) + company.getLogoPath())
                    .into(top_img_produtora);
        } else {
            Picasso.with(this).load(R.drawable.empty_produtora2)
                    .into(top_img_produtora);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(top_img_produtora, "x", -100, 0)
                .setDuration(1000);
        animatorSet.playTogether(alphaStar);
        animatorSet.start();

        top_img_produtora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info_layout.getVisibility() == View.INVISIBLE) {
                    info_layout.setVisibility(View.VISIBLE);
                } else {
                    if (info_layout.getVisibility() == View.VISIBLE) {
                        info_layout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private void InfoLayout() {

        info_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info_layout.getVisibility() == View.INVISIBLE) {
                    info_layout.setVisibility(View.VISIBLE);
                } else {
                    if (info_layout.getVisibility() == View.VISIBLE) {
                        info_layout.setVisibility(View.INVISIBLE);
                    }
                }
            }
        });
    }

    private class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {
            //não é possivel buscar TVShow da company. Esperar API
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ProdutoraActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            company = getTmdbCompany().getCompanyInfo(id_produtora);
            if (pagina == 1) {
                if (idioma_padrao) {
                    resultsPage = FilmeService.getTmdbCompany()
                            .getCompanyMovies(id_produtora, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry() + ",en,null", pagina);
                } else {
                    resultsPage = FilmeService.getTmdbCompany()
                            .getCompanyMovies(id_produtora, "en,null", pagina);
                }
            } else {
                if (idioma_padrao) {
                    temp = resultsPage;
                    resultsPage = FilmeService.getTmdbCompany()
                            .getCompanyMovies(id_produtora, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry() + ",en,null", pagina);
                    resultsPage.getResults().addAll(temp.getResults());
                } else {
                    resultsPage = FilmeService.getTmdbCompany()
                            .getCompanyMovies(id_produtora, "en,null", pagina);
                }
            }
            Log.d("PRODUTORA", "Total : " + resultsPage.getTotalPages());
            return null;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            Log.d("PRODUTORA", "post : " + resultsPage.getTotalPages());
            refreshLayout.setRefreshing(false);
            if (pagina == 1) {
                setImageTop();
            }
            if (pagina <= resultsPage.getTotalPages()) {
                pagina = pagina + 1;
            }
            progressBar.setVisibility(View.GONE);
            setHome();
            setHeadquarters();
            getSupportActionBar().setTitle(company.getName().isEmpty() ? "" : company.getName());
            recyclerView.setAdapter(new ProdutoraAdapter(ProdutoraActivity.this, resultsPage.getResults()));
        }
    }
}
