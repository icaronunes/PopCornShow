package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import adapter.FavotireAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Prefs;

import static utils.UtilsFilme.getContext;

/**
 * Created by icaro on 01/08/16.
 */

public class FavotireActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage resultsPage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);
        setUpToolBar();
        setUpToolBar();
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new LinearLayoutManager(FavotireActivity.this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();

    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String user = Prefs.getString(FavotireActivity.this, Prefs.LOGIN, Prefs.LOGIN_PASS);
            String pass = Prefs.getString(FavotireActivity.this, Prefs.PASS, Prefs.LOGIN_PASS);
            resultsPage = FilmeService.getFavorite(user, pass);
            Log.d("FavotireActivity", "Total " + resultsPage.getTotalResults());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(new FavotireAdapter(FavotireActivity.this, resultsPage.getResults()));
        }
    }
}