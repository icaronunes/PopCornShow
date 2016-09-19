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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

import adapter.SearchAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.Multi;
import provider.SuggestionProvider;
import utils.UtilsFilme;


/**
 * Created by icaro on 08/07/16.
 */

public class SearchMultiActivity extends BaseActivity {


    RecyclerView recyclerView;
    String query;
    List<Multi> movieDbList = null;
    TextView text_search_empty;
    SwipeRefreshLayout swipeRefreshLayout;
    ProgressBar progressBar;
    private int pagina = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //listView = (ListView) findViewById(R.id.listview_search); //Mudar ListView para Recycleview
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_search);
        text_search_empty = (TextView) findViewById(R.id.text_search_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        //linear_search_layout = (LinearLayout) findViewById(R.id.linear_search_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Log.d("SearchMultiActivity", "Entrou");
        Log.d("SearchMultiActivity", "onCreate");
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        if (savedInstanceState == null) {

//            query = myIntent.getStringExtra(SearchManager.QUERY);
//            getSupportActionBar().setTitle(query);
            Intent myIntent = getIntent();
            if (Intent.ACTION_SEARCH.equals(myIntent.getAction())) {
                query = myIntent.getStringExtra(SearchManager.QUERY);
                getSupportActionBar().setTitle(query);

                SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                        SuggestionProvider.AUTHORITY, SuggestionProvider.MODE);
                suggestions.saveRecentQuery(query, null);

                Log.d("ACTION_SEARCH", query);
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

        return true;//super.onCreateOptionsMenu(menu);
    }

//    protected void snack() {
//        Snackbar.make(linear_search_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
//                .setAction(R.string.retry, new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
//                            text_search_empty.setVisibility(View.GONE);
//                            TMDVAsync tmdvAsync = new TMDVAsync();
//                            tmdvAsync.execute();
//                        } else {
//                            snack();
//                        }
//                    }
//                }).show();
//    }

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
                            ",en,null", pagina);
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
               // listView.setAdapter(new SearchAdapter(SearchMultiActivity.this, movieDbList));
                recyclerView.setAdapter(new SearchAdapter(SearchMultiActivity.this, movieDbList) );
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
