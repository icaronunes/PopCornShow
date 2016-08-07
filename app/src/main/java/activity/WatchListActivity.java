package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import adapter.WatchAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.Prefs;

public class WatchListActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage favoritos, watchlist, rated;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent()

                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.quero_assistir)));
        setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(WatchListActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }


    @Override
    protected void onResume() {
        super.onResume();
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FilmeApplication.getInstance().getBus().unregister(this);
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String user = Prefs.getString(WatchListActivity.this, Prefs.LOGIN, Prefs.LOGIN_PASS);
            String pass = Prefs.getString(WatchListActivity.this, Prefs.PASS, Prefs.LOGIN_PASS);
            watchlist = FilmeService.getWatchList(user, pass, 1);
            //Log.d("FavoriteActivity", "watch " + watchlist.getTotalResults());
            favoritos = FilmeApplication.getInstance().getFavorite();
            if (favoritos == null) {
                favoritos = FilmeService.getFavorite(user, pass);
            }
            // Log.d("FavoriteActivity", "favorito " + favoritos.getTotalResults());
            rated = FilmeService.getRated(user, pass, 1);
            Log.d("FavoriteActivity", "rated " + rated.getTotalResults());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("FavoriteActivity", "TMDVAsync.PosEx");
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new WatchAdapter(WatchListActivity.this, watchlist.getResults()
                    , rated.getResults(), favoritos.getResults()));

        }
    }

}
