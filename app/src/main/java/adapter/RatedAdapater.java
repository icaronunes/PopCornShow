package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.icaro.filme.R;
import fragment.ListaFavoriteFragment;
import fragment.ListaRatedFragment;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;

/**
 * Created by icaro on 23/08/16.
 */
public class RatedAdapater extends FragmentPagerAdapter {

    Context context;
    TvResultsPage tvResultsPage;
    MovieResultsPage movieResultsPage;


    public RatedAdapater(Context context, FragmentManager supportFragmentManager,
                         TvResultsPage series, MovieResultsPage movies) {
        super(supportFragmentManager);
        this.context = context;
        this.tvResultsPage = series;
        this.movieResultsPage = movies;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ListaRatedFragment.newInstanceMovie(R.string.filme, movieResultsPage);
        }
        if (position == 1) {
            return ListaRatedFragment.newInstanceTvShow(R.string.tvshow, tvResultsPage);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.filme);
        }
        if (position == 1) {
            return context.getString(R.string.tvshow);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}