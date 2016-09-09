package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 08/07/16.
 */

public class SearchMultiActivity extends BaseActivity {

    ListView listView;
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

        listView = (ListView) findViewById(R.id.listview_search); //Mudar ListView para Recycleview
        text_search_empty = (TextView) findViewById(R.id.text_search_empty);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        //linear_search_layout = (LinearLayout) findViewById(R.id.linear_search_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        Log.d("SearchMultiActivity", "Entrou");
        Log.d("SearchMultiActivity", "onCreate");
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
            new TMDVAsync().execute();

        } else {
            text_search_empty.setText(R.string.no_internet);
            text_search_empty.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
           // snack();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (movieDbList.get(position).getMediaType().equals(Multi.MediaType.MOVIE)) {
                    MovieDb movieDb = ((MovieDb) movieDbList.get(position));
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(SearchMultiActivity.this, FilmeActivity.class);
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);

                    intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                    Log.d("setOnItemClickListener", movieDb.getOriginalTitle());

                    intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                    startActivity(intent);
                }

                if (movieDbList.get(position).getMediaType().equals(Multi.MediaType.PERSON)) {
                    Person person = ((Person) movieDbList.get(position));
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(SearchMultiActivity.this, PersonActivity.class);
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                    intent.putExtra(Constantes.PERSON_ID, person.getId());
                    Log.d("setOnItemClickListener", person.getName());
                    intent.putExtra(Constantes.NOME_PERSON, person.getName());
                    startActivity(intent);
                }
                if (movieDbList.get(position).getMediaType().equals(Multi.MediaType.TV_SERIES)) {
                    TvSeries serie = ((TvSeries) movieDbList.get(position));
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(SearchMultiActivity.this, TvShowActivity.class);
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                    intent.putExtra(Constantes.TVSHOW_ID, serie.getId());
                    Log.d("setOnItemClickListener", serie.getName());
                    intent.putExtra(Constantes.NOME_TVSHOW, serie.getName());
                    startActivity(intent);
                }
            }
        });
        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());
    }

    private SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
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
                TmdbSearch tmdbSearch = FilmeService.getTmdbSearch();
                TmdbSearch.MultiListResultsPage movieResultsPage = tmdbSearch.searchMulti(query,
                        getString(R.string.IDIOMAS_BUSCA), pagina);
                return movieResultsPage.getResults();
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
                listView.setAdapter(new SearchAdapter(SearchMultiActivity.this, movieDbList));
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
