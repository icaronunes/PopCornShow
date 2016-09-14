package fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

public class ViewPageMainTopFragment extends FragmentPagerAdapter {

    Object tmdbMovies;

    public ViewPageMainTopFragment(FragmentManager supportFragmentManager, Object tmdbMovies) {
        super(supportFragmentManager);
        this.tmdbMovies = tmdbMovies;
        Log.d("ViewPageMainTopFragment", "Movie");
    }


    @Override
    public Fragment getItem(int position) {
        if (tmdbMovies.getClass().equals(MovieResultsPage.class)) {
            Log.d("ViewPageMainTopFragment", tmdbMovies.toString());
            return new ImagemTopScrollFragment().newInstance(((MovieResultsPage) tmdbMovies).getResults().get(position).getBackdropPath());
        } else {
            return new ImagemTopScrollFragment().newInstance(((TvResultsPage) tmdbMovies).getResults().get(position).getBackdropPath());
        }

    }

    @Override
    public int getCount() {
        return 20;
    }
}