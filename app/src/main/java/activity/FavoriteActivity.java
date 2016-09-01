package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import adapter.FavoriteAdapater;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class FavoriteActivity extends BaseActivity {

    ViewPager viewPager;
    TabLayout tabLayout;
    TvResultsPage tvResultsPage;
    MovieResultsPage movieResultsPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite2);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.favorite);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpage_favorite);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        new FavoriteAsync().execute();
    }


    private void setupViewPagerTabs() {

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        viewPager.setAdapter(new FavoriteAdapater(FavoriteActivity.this, getSupportFragmentManager(),
                tvResultsPage, movieResultsPage));
    }


    private class FavoriteAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            movieResultsPage = FilmeService.getTotalFavorite();
            tvResultsPage = FilmeService.getTotalFavoriteTvShow();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPagerTabs();
        }
    }

}
