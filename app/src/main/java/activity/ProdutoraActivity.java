package activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import adapter.ProdutoraAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbCompany;
import info.movito.themoviedbapi.model.Company;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.movieDb;
import static domian.FilmeService.getTmdbCompany;

/**
 * Created by icaro on 10/08/16.
 */
public class ProdutoraActivity extends BaseActivity {
    Company company;
    TmdbCompany.CollectionResultsPage resultsPage;
    RecyclerView recyclerView;
    RelativeLayout info_layout;
    int id_produtora;
    TextView home_produtora, headquarters, descricao;
    ImageView top_img_produtora;
    CollapsingToolbarLayout collapsingToolbarLayout;
    ProgressBar progressBar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.produtora_layout);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.PRODUTORA));

        home_produtora = (TextView) findViewById(R.id.home_produtora);
        headquarters = (TextView) findViewById(R.id.headquarters);
        info_layout = (RelativeLayout) findViewById(R.id.info_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        descricao = (TextView) findViewById(R.id.descricao_produtora);
        top_img_produtora = (ImageView) findViewById(R.id.top_img_produtora);
        recyclerView = (RecyclerView) findViewById(R.id.produtora_filmes_container);
        recyclerView.setLayoutManager(new GridLayoutManager(ProdutoraActivity.this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        InfoLayout();

    }

    @Override
    protected void onStart() {
        super.onStart();
        id_produtora = getIntent().getIntExtra(Constantes.PRODUTORA_ID, 0);
        new TMDVAsync().execute();
    }

    private void setTitle(String title) {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    private void setDescricao() {
        if (company.getDescription() != null) {
            descricao.setText(company.getDescription());
        } else {
            descricao.setVisibility(View.GONE);
        }
    }

    private void setHeadquarters() {
        if (company.getHeadquarters() != null) {
            headquarters.setText(company.getHeadquarters());
        } else {
            headquarters.setVisibility(View.GONE);
        }
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
                    .placeholder(R.drawable.top_empty)
                    .into(top_img_produtora);
        }

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(top_img_produtora, "y", -100, 0)
                .setDuration(1000);
        animatorSet.playTogether(alphaStar);
        animatorSet.start();

        top_img_produtora.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (info_layout.getVisibility() == View.INVISIBLE) {
                    info_layout.setVisibility(View.VISIBLE);
                    collapsingToolbarLayout.setTitle(company.getName());

                } else {
                    if (info_layout.getVisibility() == View.VISIBLE) {
                        info_layout.setVisibility(View.INVISIBLE);
                        setTitle(" ");//???????????????
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
            company = getTmdbCompany().getCompanyInfo(id_produtora);
            //resultsPage = FilmeService.getTmdbCompany().getCompanyMovies(id_produtora, getResources().getString(R.string.IDIOMAS), 1);
            resultsPage = FilmeService.getTmdbCompany().getCompanyMovies(id_produtora, getResources().getString(R.string.IDIOMAS), 1);

            return null;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            setTitle(company.getName());
            progressBar.setVisibility(View.GONE);
            setDescricao();
            setHome();
            setHeadquarters();
            setImageTop();
            recyclerView.setAdapter(new ProdutoraAdapter(ProdutoraActivity.this, resultsPage.getResults()));
        }
    }
}
