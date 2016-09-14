package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import activity.MainActivity;
import br.com.icaro.filme.R;
import fragment.MainFragment;
import fragment.TvShowFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Created by icaro on 23/08/16.
 */
public class MainAdapter extends FragmentPagerAdapter {

    Context context;


    public MainAdapter(MainActivity context, FragmentManager supportFragmentManager) {
        super(supportFragmentManager);
        this.context = context;

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return MainFragment.newInstance(R.string.tvshow_main);
        }
        if (position == 1) {
            return MainFragment.newInstance(R.string.filmes_main);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.tvshow);
        }
        if (position == 1) {
            return context.getString(R.string.filme);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}