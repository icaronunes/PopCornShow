package activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Locale;

import adapter.MainAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.ViewPageMainTopFragment;
import fragment.ViewPageMainTvTopFragment;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.Prefs;
import utils.UtilsFilme;


public class MainActivity extends BaseActivity {

    ViewPager viewPager_main, viewpage_top_main;
    TvResultsPage tmdbTv;
    MovieResultsPage tmdbMovies;
    boolean idioma_padrao;
    TabLayout tabLayout;
    TextView internet;

    private FirebaseAnalytics mFirebaseAnalytics;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
        setUpToolBar();
        setupNavDrawer();
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);
        viewpage_top_main = (ViewPager) findViewById(R.id.viewpage_top_main);


        if (UtilsFilme.isNetWorkAvailable(this)) {
            new TMDVAsync().execute();
        } else {
            snack();
        }

    }

    protected void snack() {
        Snackbar.make(viewpage_top_main, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            new TMDVAsync().execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
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
                Bundle bundle;
                if (tab.getPosition() == 0) {
                    Log.d("MainActivity", "0");
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.accent2));
                    viewpage_top_main.setBackgroundColor(getResources().getColor(R.color.accent2));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "tab_main");
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_NAME, tab.getPosition());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
                if (tab.getPosition() == 1) {
                    Log.d("MainActivity", "1");
                    tabLayout.setBackgroundColor(getResources().getColor(R.color.accent));
                    viewpage_top_main.setBackgroundColor(getResources().getColor(R.color.accent));
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent2));
                    viewpage_top_main.setAdapter(new ViewPageMainTvTopFragment(getSupportFragmentManager(), tmdbTv));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "tab_main");
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_NAME, tab.getPosition());
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
    protected void onResume() {
        super.onResume();
        setCheckable(R.id.menu_drav_home);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.apagar:
                Prefs.apagarLoginSenha(MainActivity.this, Prefs.LOGIN_PASS);
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
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            if (idioma_padrao) {
                tmdbTv = FilmeService.getTmdbTvShow()
                        .getAiringToday(Locale.getDefault().toLanguageTag() + ",en,null", 1, UtilsFilme.getTimezone());
                tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies(Locale
                        .getDefault().toLanguageTag() + ",en,null", 1);
                Log.d("MainActivity", "Movie - " + tmdbMovies.getResults().size());
                Log.d("MainActivity", "Tv - " + tmdbTv.getResults().size());
            } else {
                tmdbTv = FilmeService.getTmdbTvShow().getAiringToday("en", 1, UtilsFilme.getTimezone());
                tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies("en", 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPagerTabs();

        }
    }

}
