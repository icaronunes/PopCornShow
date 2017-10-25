package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;

import adapter.SearchAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import filme.activity.FilmeActivity;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Multi;
import pessoa.activity.PersonActivity;
import provider.SuggestionRecentProvider;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;


/**
 * Created by icaro on 08/07/16.
 */

public class SearchMultiActivity extends BaseActivity {


    private RecyclerView recyclerView;
    private String query = "";
    private List<Multi> movieDbList = null;
    private TextView text_search_empty;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private int pagina = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView_search);
        text_search_empty = (TextView) findViewById(R.id.text_search_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


        /**
         *
         * Arrumar! Gambiara. Funcionando.
         *
         */

        if (savedInstanceState == null) {
            if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
                query = getIntent().getStringExtra(SearchManager.QUERY);
                getSupportActionBar().setTitle(query);
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SuggestionRecentProvider.AUTHORITY, SuggestionRecentProvider.MODE);
                suggestions.saveRecentQuery(query, null);

            } else {
                if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

                    Intent intent;
                    if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(Multi.MediaType.MOVIE.name())) {

                        final int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));

                        intent = new Intent(this, FilmeActivity.class);
                        intent.putExtra(Constantes.INSTANCE.getFILME_ID(), id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(Multi.MediaType.TV_SERIES.name())) {
                        final int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));

                        intent = new Intent(this, TvShowActivity.class);
                        intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(Multi.MediaType.PERSON.name())) {

                        String string = getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY);
                        final String id = string.substring(0, string.indexOf('/'));
                        intent = new Intent(this, PersonActivity.class);
                        intent.putExtra(Constantes.INSTANCE.getPERSON_ID(), Integer.valueOf(id));
                        intent.putExtra(Constantes.INSTANCE.getNOME_PERSON(), string.substring(string.indexOf('/') + 1, string.length()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                        return;
                    }

                }
            }
        }

        if (UtilsApp.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();

        } else {
            text_search_empty.setText(R.string.no_internet);
            text_search_empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }


        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());
    }


    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return () -> {
            progressBar.setVisibility(View.INVISIBLE);
            if (UtilsApp.isNetWorkAvailable(SearchMultiActivity.this)) {
                TMDVAsync tmdvAsync = new TMDVAsync();
                tmdvAsync.execute();
                text_search_empty.setVisibility(View.GONE);
            } else {
                text_search_empty.setText(R.string.no_internet);
                text_search_empty.setText(View.VISIBLE);
                swipeRefreshLayout.setEnabled(false);
            }
        };
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

    @Override
    protected void onResume() {
        super.onResume();
        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);
    }

    private class TMDVAsync extends AsyncTask<Void, Void, List<Multi>> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setEnabled(false);
        }

        @Override
        protected List<Multi> doInBackground(Void... voids) {//
            if (!query.isEmpty()) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SearchMultiActivity.this);
                boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
                try {
                    if (idioma_padrao) {
                        TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
                        TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
                                getLocale() + "en,null", pagina);

                        return movieResultsPage.getResults();
                    } else {
                        TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
                        TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
                                "en,null", pagina);
                        return movieResultsPage.getResults();
                    }
                } catch (Exception e) {
                    FirebaseCrash.report(e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(SearchMultiActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Multi> movieDbs) {

            swipeRefreshLayout.setEnabled(true);
            if (movieDbs != null && pagina != 1) {
                List<Multi> x = movieDbList;
                movieDbList = movieDbs;
                for (Multi movie : x) {
                    movieDbList.add(movie);
                }
                pagina++;
            } else {

                movieDbList = movieDbs;
            }
            if (movieDbList != null && movieDbList.size() > 0) { // TODO: 09/01/17 pode ser null? - vai dar erro?
                swipeRefreshLayout.setRefreshing(false);
                recyclerView.setAdapter(new SearchAdapter(SearchMultiActivity.this, movieDbList));
                swipeRefreshLayout.setEnabled(true);
                pagina++;
                progressBar.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
                text_search_empty.setVisibility(View.VISIBLE);
            }
        }
    }
}
