package activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.FilmeBottonFragment;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

public class FilmeActivity extends BaseActivity {

    private ImageView img_top;
    private int id_filme;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressBar progressBar;
    private MovieDb movieDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme);
        setUpToolBar();
        setupNavDrawer();
        setUpConponentes();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.NOME_FILME));
        progressBar = (ProgressBar) findViewById(R.id.progress);


        if (savedInstanceState == null) {
            FilmeBottonFragment filmeFrag = new FilmeBottonFragment();
            Bundle bundle = new Bundle(); //Tentar pegar nome que esta no bundle
            bundle.putInt(Constantes.FILME_ID, getIntent().getExtras().getInt(Constantes.FILME_ID));
            filmeFrag.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.filme_container, filmeFrag, null)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    }

    private void setTitle(String title) {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);

        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();

    }


    public void getImagemTopo(final MovieDb mdovieDb) { //APAGAR

        String urlBase = "http://image.tmdb.org/t/p/";
        final StringBuilder stringBuilder = new StringBuilder(urlBase);
        stringBuilder.append("/")
                .append("w780");
        Log.d("Aqui", stringBuilder.toString()+movieDb.getBackdropPath());
        Picasso.with(this).load(stringBuilder + movieDb.getBackdropPath()).into(img_top);
    }

    protected void setUpConponentes() {
        img_top = (ImageView) findViewById(R.id.img_top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setEnabled(false);
        return true;
    }

    public class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected MovieDb doInBackground(Void... voids) {//
            Log.d("FilmeFragment", "doInBackground :" + id_filme);
            movieDb = FilmeService.getTmdbMovie(id_filme, "pt-BR");
            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            getImagemTopo(movieDb);
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}