package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.SearchRecentSuggestions;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import adapter.SearchAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Multi;
import provider.SuggestionRecentProvider;
import utils.Constantes;
import utils.UtilsFilme;

import static android.R.attr.id;


/**
 * Created by icaro on 08/07/16.
 */

public class SearchMultiActivity extends BaseActivity {


    RecyclerView recyclerView;
    String query = "";
    List<Multi> movieDbList = null;
    TextView text_search_empty;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    private int pagina = 1;
    private Intent intent;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycleView_search);
        text_search_empty = (TextView) findViewById(R.id.text_search_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Log.d("SearchMultiActivity", "Entrou");
        Log.d("SearchMultiActivity", "onCreate");
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        /**
         *
         * Arrumar! Gambiara funcionando.
         *
         */

        if (savedInstanceState == null) {
            if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
                query = getIntent().getStringExtra(SearchManager.QUERY);
                getSupportActionBar().setTitle(query);
                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SuggestionRecentProvider.AUTHORITY, SuggestionRecentProvider.MODE);
                suggestions.saveRecentQuery(query, null);
                Log.d("SearchMultiActivity", "ACTION_SEARCH");

            } else {
                if (Intent.ACTION_VIEW.equals(getIntent().getAction())) {

                    if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(Multi.MediaType.MOVIE.name())) {
                        Log.d("SearchMultiActivity", "ACTION_VIEW");
                        Log.d("SearchMultiActivity", String.valueOf(getIntent().getData()));
                        Log.d("SearchMultiActivity", String.valueOf(getIntent().getData().getLastPathSegment()));
                        final int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));
                        Log.d("SearchMultiActivity", id + " " + Multi.MediaType.MOVIE);
                        intent = new Intent(this, FilmeActivity.class);
                        intent.putExtra(Constantes.FILME_ID, id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(Multi.MediaType.TV_SERIES.name())) {
                        Log.d("SearchMultiActivity", "ACTION_VIEW");
                        Log.d("SearchMultiActivity", String.valueOf(getIntent().getData().getLastPathSegment()));
                        final int id = Integer.parseInt(getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY));
                        Log.d("SearchMultiActivity", "" + id);
                        intent = new Intent(this, TvShowActivity.class);
                        intent.putExtra(Constantes.TVSHOW_ID, id);
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if (getIntent().getData().getLastPathSegment().equalsIgnoreCase(Multi.MediaType.PERSON.name())) {
                        Log.d("SearchMultiActivity", "ACTION_VIEW");
                        Log.d("SearchMultiActivity", String.valueOf(getIntent().getData()));
                        Log.d("SearchMultiActivity", String.valueOf(getIntent().getData().getLastPathSegment()));
                        String string = getIntent().getExtras().getString(SearchManager.EXTRA_DATA_KEY);
                        final String id = string.substring(0, string.indexOf('/'));
                        Log.d("SearchMultiActivity", id);
                        Log.d("SearchMultiActivity", string.substring(string.indexOf('/') +1, string.length()));
                        intent = new Intent(this, PersonActivity.class );
                        intent.putExtra(Constantes.PERSON_ID, Integer.valueOf(id));
                        intent.putExtra(Constantes.NOME_PERSON, string.substring(string.indexOf('/') + 1, string.length()));
                        intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                        startActivity(intent);
                        finish();
                        return;
                    }
//: "1892/Matt Damon"
                }
            }
        }

        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();

        } else {
            text_search_empty.setText(R.string.no_internet);
            text_search_empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            // snack();
        }

        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());
    }


    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                progressBar.setVisibility(View.INVISIBLE);
                if (UtilsFilme.isNetWorkAvailable(SearchMultiActivity.this)) {
                    TMDVAsync tmdvAsync = new TMDVAsync();
                    tmdvAsync.execute();
                    text_search_empty.setVisibility(View.GONE);
                } else {
                    text_search_empty.setText(R.string.no_internet);
                    text_search_empty.setText(View.VISIBLE);
                    swipeRefreshLayout.setEnabled(false);
                    //snack();
                }
            }
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);
        Log.d("onCreateOptionsMenu", "Option Menu");
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setEnabled(false);

        return true;
    }

    private class TMDVAsync extends AsyncTask<Void, Void, List<Multi>> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<Multi> doInBackground(Void... voids) {//
            if (!query.isEmpty()) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(SearchMultiActivity.this);
                boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
                if (idioma_padrao) {
                    TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
                    TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
                            Locale.getDefault().toLanguageTag() + ",en,null", pagina);
                    return movieResultsPage.getResults();
                } else {
                    TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
                    TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
                            "en,null", pagina);
                    return movieResultsPage.getResults();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(List<Multi> movieDbs) {

            swipeRefreshLayout.setEnabled(true);
            if (movieDbs != null && pagina != 1) {
                Log.d("SearchMultiActivity", "onPostExecute :" + query);
                List<Multi> x = movieDbList;
                movieDbList = movieDbs;
                for (Multi movie : x) {
                    movieDbList.add(movie);
                }
                pagina++;
            } else {

                movieDbList = movieDbs;
            }
            if (movieDbList.size() != 0) {
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
