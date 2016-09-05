package activity;

import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import adapter.ListasAdapater;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class RatedActivity extends BaseActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    TvResultsPage tvResultsPage;
    MovieResultsPage movieResultsPage;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_watch_list);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.avaliados);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpage_rated);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        new WatchlistAsync().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViewPagerTabs() {

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        viewPager.setAdapter(new ListasAdapater(RatedActivity.this, getSupportFragmentManager(),
                tvResultsPage, movieResultsPage));
    }


    private class WatchlistAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            movieResultsPage = FilmeService.getRatedMovieListTotal();
            tvResultsPage = FilmeService.getRatedListTotal();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPagerTabs();
            progressBar.setVisibility(View.GONE);
        }
    }
}
