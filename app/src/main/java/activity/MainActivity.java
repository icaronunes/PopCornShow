package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapter.MainAdapter;
import adapter.TvShowAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.FilmesFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.Prefs;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.id.menu_item_favorite;
import static br.com.icaro.filme.R.id.menu_item_rated;
import static br.com.icaro.filme.R.id.menu_item_watchlist;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.credits;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.external_ids;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.images;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.videos;

public class MainActivity extends BaseActivity {

    ViewPager viewPager_main;
    ImageView image_top_main;
    TmdbTV tmdbTv;
    TmdbMovies tmdbMovies;
    boolean idioma_padrao;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);
        image_top_main = (ImageView) findViewById(R.id.img_top_main);
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();
    }

    private void setupViewPagerTabs() {

        viewPager_main.setOffscreenPageLimit(1);
        viewPager_main.setAdapter(new MainAdapter(this, getSupportFragmentManager(), tmdbMovies, tmdbTv));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager_main.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager_main);
        //tabLayout.setSelectedTabIndicatorColor(color_top);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Procura Filme");
        searchView.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.apagar:
                Prefs.apagar(MainActivity.this, Prefs.LOGIN_PASS);
                FilmeApplication.getInstance().setLogado(false);
                startActivity(new Intent(MainActivity.this, MainActivity.class));
                break;
            case R.id.serie:{
                Intent intent = new Intent(this, TvShowActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, 62560);
                intent.putExtra(Constantes.NOME_TVSHOW, "Breaking Bad: A Química do Mal");
                intent.putExtra(Constantes.COLOR_TOP, -14663350);
                startActivity(intent);
                break;
            }
            case R.id.filme: {
                Intent intent = new Intent(this, FilmeActivity.class);
                intent.putExtra(Constantes.FILME_ID, 76341);
                intent.putExtra(Constantes.NOME_FILME, "Mad Max: Estrada da Fúria");
                intent.putExtra(Constantes.COLOR_TOP, -14663350);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {

            tmdbTv = FilmeService.getTmdbTvShow();
            tmdbMovies = FilmeService.getTmdbMovies();
            //??????????/ usado pra que? alem to top?
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPagerTabs();
        }
    }

}
