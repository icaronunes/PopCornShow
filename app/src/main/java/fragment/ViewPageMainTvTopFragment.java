package fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class ViewPageMainTvTopFragment extends FragmentPagerAdapter {


    TvResultsPage tmdbtvshow;


    public ViewPageMainTvTopFragment(FragmentManager supportFragmentManager, TvResultsPage tmdbtvshow) {
        super(supportFragmentManager);
        this.tmdbtvshow = tmdbtvshow;
        Log.d("ViewPageMainTopFragment", "Tvshow");
    }

    @Override
    public Fragment getItem(int position) {

        return new ImagemTopFilmeScrollFragment().newInstance(tmdbtvshow.getResults().get(position).getBackdropPath());

    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public int getCount() {
        return 10;
    }
}