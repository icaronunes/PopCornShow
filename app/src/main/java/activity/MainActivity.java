package activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;

import com.viewpagerindicator.CirclePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapter.MainAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import domian.TopMain;
import fragment.ViewPageMainTopFragment;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.UtilsFilme;


public class MainActivity extends BaseActivity {

    ViewPager viewPager_main, viewpage_top_main;
    TvResultsPage tmdbTv;
    MovieResultsPage tmdbMovies;
    boolean idioma_padrao;
    TabLayout tabLayout;
    List<TopMain> multi = new ArrayList<>();
    CirclePageIndicator circlePageIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
        setUpToolBar();
        setupNavDrawer();
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");
        if (getIntent().hasExtra("click_action")){
            Log.d("MainActivity", getIntent().getStringExtra("click_action"));
            Log.d("MainActivity", "tamanhao bandle " + getIntent().getExtras().size());
        }

        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);
        viewpage_top_main = (ViewPager) findViewById(R.id.viewpage_top_main);
        viewpage_top_main.setOffscreenPageLimit(3);


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
        circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indication_main);
        viewpage_top_main.setAdapter(new ViewPageMainTopFragment(getSupportFragmentManager(), multi));
        circlePageIndicator.setViewPager(viewpage_top_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCheckable(R.id.menu_drav_home);
    }


    private void mescla() {

        for (int i = 0; i < 20 && multi.size() < 14; i++) {
            if (i % 2 == 0) {
                TopMain topMain = new TopMain();
                MovieDb movieDb = tmdbMovies.getResults().get(i);
                topMain.setId(movieDb.getId());
                topMain.setNome(movieDb.getTitle());
                topMain.setMediaType(movieDb.getMediaType().name());
                topMain.setImagem(movieDb.getBackdropPath());

                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

                try {
                    date = sdf.parse(movieDb.getReleaseDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (movieDb.getBackdropPath() != null && UtilsFilme.verificaLancamento(date)) {
                    multi.add(topMain);
                }
            } else {
                TopMain topMain = new TopMain();
                topMain.setId(tmdbTv.getResults().get(i).getId());
                topMain.setNome(tmdbTv.getResults().get(i).getName());
                topMain.setMediaType(tmdbTv.getResults().get(i).getMediaType().name());
                topMain.setImagem(tmdbTv.getResults().get(i).getBackdropPath());
                if (tmdbTv.getResults().get(i).getBackdropPath() != null) {
                    multi.add(topMain);
                }
            }
        }
        setupViewPagerTabs();
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            if (idioma_padrao) {
                tmdbTv = FilmeService.getTmdbTvShow()
                        .getAiringToday(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
                                //.toLanguageTag()
                                , 1, UtilsFilme.getTimezone());
                tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
                        //.toLanguageTag()
                         , 1);
            } else {
                tmdbTv = FilmeService.getTmdbTvShow().getAiringToday("en", 1, UtilsFilme.getTimezone());
                tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies("en", 1);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            mescla();
        }
    }

}
