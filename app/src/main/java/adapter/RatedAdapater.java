package adapter;

import android.content.Context;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import br.com.icaro.filme.R;
import domain.FilmeDB;
import domain.TvshowDB;
import fragment.ListaRatedFragment;

/**
 * Created by icaro on 23/08/16.
 */
public class RatedAdapater extends FragmentPagerAdapter {

    private Context context;
    private List<TvshowDB> series;
    private List<FilmeDB> movies;


    public RatedAdapater(Context context, FragmentManager supportFragmentManager,
                         List<TvshowDB> series, List<FilmeDB> movies) {
        super(supportFragmentManager);
        this.context = context;
        this.series = series;
        this.movies = movies;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ListaRatedFragment.newInstanceMovie(R.string.filme, movies);
        }
        if (position == 1) {
            return ListaRatedFragment.newInstanceTvShow(R.string.tvshow, series);
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