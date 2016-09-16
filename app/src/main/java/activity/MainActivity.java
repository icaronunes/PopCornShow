package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import adapter.MainAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.ViewPageMainTopFragment;
import fragment.ViewPageMainTvTopFragment;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.config.Timezone;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.Prefs;

public class MainActivity extends BaseActivity {

    ViewPager viewPager_main, viewpage_top_main;
    TvResultsPage tmdbTv;
    MovieResultsPage tmdbMovies;
    boolean idioma_padrao;
    TabLayout tabLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        setCheckable(R.id.menu_drav_home);
        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);
        viewpage_top_main = (ViewPager) findViewById(R.id.viewpage_top_main);

        new TMDVAsync().execute();

    }

    private void setupViewPagerTabs() {

        viewPager_main.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager_main.setCurrentItem(0);
        viewPager_main.setAdapter(new MainAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager_main);
        viewpage_top_main.setAdapter(new ViewPageMainTopFragment(getSupportFragmentManager(), tmdbMovies));
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                if (tab.getPosition() == 0) {
                    Log.d("MainActivity", "0");
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.accent2));
                    viewpage_top_main.setBackgroundColor(getResources().getColor(R.color.accent2));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
                    //  viewpage_top_main.setAdapter(new ViewPageMainTopFragment(getSupportFragmentManager(), tvSeries));
                }
                if (tab.getPosition() == 1) {
                    Log.d("MainActivity", "1");
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.accent));
                    viewpage_top_main.setBackgroundColor(getResources().getColor(R.color.accent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent2));
                    viewpage_top_main.setAdapter(new ViewPageMainTvTopFragment(getSupportFragmentManager(), tmdbTv));

                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

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
            case R.id.serie: {
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

            tmdbTv = FilmeService.getTmdbTvShow().getAiringToday("pt-BR", 1, new Timezone("Brasil", "Brazil"));
            tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies("pt-BR", 1);
            Log.d("MainActivity", "Movie - " + tmdbMovies.getResults().size());
            Log.d("MainActivity", "Tv - " + tmdbTv.getResults().size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPagerTabs();


        }
    }

}
