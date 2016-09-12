package fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import info.movito.themoviedbapi.TvResultsPage;

public class ViewPageMainTvTopFragment extends FragmentPagerAdapter {


    TvResultsPage tmdbtvshow;


    public ViewPageMainTvTopFragment(FragmentManager supportFragmentManager, TvResultsPage tmdbtvshow) {
        super(supportFragmentManager);
        this.tmdbtvshow = tmdbtvshow;
    }


    @Override
    public Fragment getItem(int position) {

        return new ImagemTopScrollFragment().newInstance(tmdbtvshow.getResults().get(position).getBackdropPath());

    }

    @Override
    public int getCount() {
        return 10;
    }
}