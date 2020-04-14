package favorito;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import br.com.icaro.filme.R;
import domain.MovieDb;
import domain.TvshowDB;

/**
 * Created by icaro on 23/08/16.
 */
public class FavoriteAdapater extends FragmentPagerAdapter {

    private Context context;
    private List<MovieDb> movies;
    private List<TvshowDB> series;


    public FavoriteAdapater(Context context, FragmentManager supportFragmentManager,
                            List<MovieDb> movies, List<TvshowDB> series) {
        super(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.series = series;
        this.movies = movies;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ListaFavoriteFragment.newInstanceMovie(R.string.filme, movies);
        }
        if (position == 1) {
            return ListaFavoriteFragment.newInstanceTvShow(R.string.tvshow, series);
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
