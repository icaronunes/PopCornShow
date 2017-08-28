package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import br.com.icaro.filme.R;
import domain.FilmeDB;
import domain.TvshowDB;
import fragment.ListaWatchlistFragment;

/**
 * Created by icaro on 23/08/16.
 */
public class WatchListAdapter extends FragmentPagerAdapter {

    private Context context;
    private List<TvshowDB> series;
    private List<FilmeDB> movies;


    public WatchListAdapter(Context context, FragmentManager supportFragmentManager,
                            List<TvshowDB> series, List<FilmeDB> movies) {
        super(supportFragmentManager);
        this.context = context;
        this.series = series;
        this.movies = movies;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ListaWatchlistFragment.newInstanceMovie(R.string.filme, movies);
        }
        if (position == 1) {
            return ListaWatchlistFragment.newInstanceTvShow(R.string.tvshow, series);
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