package fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class ViewPageMainTopFragment extends FragmentPagerAdapter {

    MovieResultsPage tmdbMovies;

    public ViewPageMainTopFragment(FragmentManager supportFragmentManager, MovieResultsPage tmdbMovies) {
        super(supportFragmentManager);
        this.tmdbMovies = tmdbMovies;
    }


    @Override
    public Fragment getItem(int position) {
        return new ImagemTopScrollFragment().newInstance(tmdbMovies.getResults().get(position).getBackdropPath());

    }

    @Override
    public int getCount() {
        return 10;
    }
}