package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.List;

import adapter.SearchAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbSearch;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 08/07/16.
 */

public class SearchActivity extends BaseActivity {

    ListView listView;
    String query;
    List<MovieDb> movieDbList = null;
    TextView text_search_empty;
    SwipeRefreshLayout swipeRefreshLayout;
    LinearLayout linear_search_layout;
    private int pagina = 1;
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listView = (ListView) findViewById(R.id.listview_search); //Mudar ListView para Recycleview
        text_search_empty = (TextView) findViewById(R.id.text_search_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        linear_search_layout = (LinearLayout) findViewById(R.id.linear_search_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        Log.d("SearchActivity", "onCreate");
        if (savedInstanceState == null) {

//            query = myIntent.getStringExtra(SearchManager.QUERY);
//            getSupportActionBar().setTitle(query);
            Intent myIntent = getIntent();
            if (Intent.ACTION_SEARCH.equals(myIntent.getAction())) {
                query = myIntent.getStringExtra(SearchManager.QUERY);
                getSupportActionBar().setTitle(query);
                Log.d("ACTION_SEARCH", query);
            }
        }

        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            TMDVAsync tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
        } else {
            text_search_empty.setText(R.string.no_internet);
            text_search_empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
            snack();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                Intent intent = new Intent(SearchActivity.this, FilmeActivity.class);
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.FILME_ID, movieDbList.get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movieDbList.get(position).getTitle());
                startActivity(intent);

            }
        });
        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (UtilsFilme.isNetWorkAvailable(SearchActivity.this)) {
                    TMDVAsync tmdvAsync = new TMDVAsync();
                    tmdvAsync.execute();
                    text_search_empty.setVisibility(View.GONE);
                } else {
                    text_search_empty.setText(R.string.no_internet);
                    text_search_empty.setText(View.VISIBLE);
                    swipeRefreshLayout.setEnabled(false);
                    snack();
                }
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

        return true;//super.onCreateOptionsMenu(menu);
    }

    protected void snack() {
        Snackbar.make(linear_search_layout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            text_search_empty.setVisibility(View.GONE);
                            TMDVAsync tmdvAsync = new TMDVAsync();
                            tmdvAsync.execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    public class TMDVAsync extends AsyncTask<Void, Void, List<MovieDb>> {

        @Override
        protected void onPreExecute() {
            swipeRefreshLayout.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);

        }

        @Override
        protected List<MovieDb> doInBackground(Void... voids) {//
            Log.d("SearchAdapter", "doInBackground :" + query);
            TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
            MovieResultsPage movieResultsPage = tmdbSearch.searchMovie(query, 0, "pt-BR", false, pagina);
            return movieResultsPage.getResults();

        }

        @Override
        protected void onPostExecute(List<MovieDb> movieDbs) {
            swipeRefreshLayout.setEnabled(true);
            if (movieDbs != null && pagina != 1) {
                Log.d("SearchAdapter", "onPostExecute :" + query);
                List<MovieDb> x = movieDbList;
                movieDbList = movieDbs;
                for (MovieDb movie : x) {
                    movieDbList.add(movie);
                }
                pagina++;
            } else {

                movieDbList = movieDbs;
            }
            if (movieDbList.size() != 0) {
                swipeRefreshLayout.setRefreshing(false);
                listView.setAdapter(new SearchAdapter(SearchActivity.this, movieDbList));
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
