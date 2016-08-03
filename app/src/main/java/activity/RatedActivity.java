package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import adapter.RatedAdapter;
import adapter.WatchAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.Prefs;

/**
 * Created by icaro on 03/08/16.
 */
public class RatedActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage favoritos, watchlist, rated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent()
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.avaliados)));
        setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(RatedActivity.this, 2));
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
            String user = Prefs.getString(RatedActivity.this, Prefs.LOGIN, Prefs.LOGIN_PASS);
            String pass = Prefs.getString(RatedActivity.this, Prefs.PASS, Prefs.LOGIN_PASS);
            //rated = FilmeService.getWatchList(user, pass, 1);
            //Log.d("FavoriteActivity", "watch " + watchlist.getTotalResults());
            favoritos = FilmeService.getFavorite(user, pass);
            // Log.d("FavoriteActivity", "favorito " + favoritos.getTotalResults());
            rated = FilmeService.getRated(user, pass, 1);
            Log.d("FavoriteActivity", "rated " + rated.getTotalResults());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Log.d("FavoriteActivity", "TMDVAsync.PosEx");
            recyclerView.setAdapter(new RatedAdapter(RatedActivity.this, rated.getResults()
                    , favoritos.getResults()));
        }
    }

}
