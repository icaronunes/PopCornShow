package activity;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.crash.FirebaseCrash;
import com.viewpagerindicator.CirclePageIndicator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import adapter.MainAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.TopMain;
import fragment.ViewPageMainTopFragment;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.UtilsFilme;


public class MainActivity extends BaseActivity  {

    private static final String TAG = MainActivity.class.getName();
    private ViewPager viewPager_main, viewpage_top_main;
    private TvResultsPage tmdbTv;
    private MovieResultsPage tmdbMovies;
    private TabLayout tabLayout;
    private List<TopMain> multi = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        setupNavDrawer();
        setDefaultKeyMode(DEFAULT_KEYS_SEARCH_LOCAL);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(" ");

        viewPager_main = (ViewPager) findViewById(R.id.viewPager_main);
        viewpage_top_main = (ViewPager) findViewById(R.id.viewpage_top_main);
        viewpage_top_main.setOffscreenPageLimit(3);



        if (UtilsFilme.isNetWorkAvailable(this)) {
            new TMDVAsync().execute();
        } else {
            snack();
        }

        final SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);

        if (sharedPref.getBoolean("38", true)) {
            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_popcorn2)
                    .setTitle(R.string.novidades_title)
                    .setMessage(R.string.novidades_text)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putBoolean("38", false);
                            editor.remove("37");

                            editor.apply();
                        }
                    }).create();
            dialog.show();
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

    @SuppressWarnings("deprecation")
    private void setupViewPagerTabs() {

        viewPager_main.setOffscreenPageLimit(2);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager_main.setCurrentItem(0);
        viewPager_main.setAdapter(new MainAdapter(this, getSupportFragmentManager()));
        tabLayout.setupWithViewPager(viewPager_main);
        CirclePageIndicator circlePageIndicator = (CirclePageIndicator) findViewById(R.id.indication_main);
        viewpage_top_main.setAdapter(new ViewPageMainTopFragment(getSupportFragmentManager(), multi));
        circlePageIndicator.setViewPager(viewpage_top_main);

        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_main));
        tabLayout.setTabTextColors(getResources().getColor(R.color.red), getResources().getColor(R.color.white));

        viewPager_main.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @SuppressWarnings("deprecation")
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.blue_main));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.red), getResources().getColor(R.color.white));
                } else {
                    tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.red));
                    tabLayout.setTabTextColors(getResources().getColor(R.color.blue_main), getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        setCheckable(R.id.menu_drav_home);
    }


    private void mescla() {
        if (tmdbMovies != null && tmdbTv != null) {
            for (int i = 0; i < 20 && multi.size() < 15; i++) {
                if (i % 2 == 0) {
                    if (tmdbMovies.getResults().size() > i) {
                        TopMain topMain = new TopMain();
                        final MovieDb movieDb = tmdbMovies.getResults().get(i);
                        topMain.setId(movieDb.getId());
                        topMain.setNome(movieDb.getTitle());
                        topMain.setMediaType(movieDb.getMediaType().name());
                        topMain.setImagem(movieDb.getBackdropPath());


                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                        try {
                            Date date = sdf.parse(movieDb.getReleaseDate());
                            if (movieDb.getBackdropPath() != null && UtilsFilme.verificaLancamento(date)) {
                                multi.add(topMain);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    }

                } else {
                    if (tmdbTv.getResults().size() > i) {
                        TopMain topMain = new TopMain();
                        final TvSeries tv = tmdbTv.getResults().get(i);
                        topMain.setId(tv.getId());
                        topMain.setNome(tv.getName());
                        topMain.setMediaType(tv.getMediaType().name());
                        topMain.setImagem(tv.getBackdropPath());
                        if (tv.getBackdropPath() != null) {
                            multi.add(topMain);
                        }
                    }
                }
            }
        }
        setupViewPagerTabs();
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            boolean idioma_padrao = false;
            if (!UtilsFilme.isNetWorkAvailable(MainActivity.this)) {
                return null;
            }
            try {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            } catch (Exception e){
                FirebaseCrash.report(e);
            }


            if (idioma_padrao) {
                try {
                    tmdbTv = FilmeService.getTmdbTvShow()
                            .getAiringToday(getLocale()
                                    , 1, UtilsFilme.getTimezone());
                    tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies(getLocale()
                            , 1);
                } catch (Exception e) {
                   //  Log.d(TAG, e.getMessage());
                    FirebaseCrash.report(e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    });
                }


            } else {
                try {
                    tmdbTv = FilmeService.getTmdbTvShow().getAiringToday("en", 1, UtilsFilme.getTimezone());
                    tmdbMovies = FilmeService.getTmdbMovies().getNowPlayingMovies("en", 1);
                } catch (Exception e) {
                      // Log.d(TAG, e.toString());
                    FirebaseCrash.report(e);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(MainActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
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
