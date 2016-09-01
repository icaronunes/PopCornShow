package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import br.com.icaro.filme.R;
import fragment.TvShowFragment;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Created by icaro on 23/08/16.
 */
public class TvShowAdapter extends FragmentPagerAdapter {

    Context context;
    TvSeries series;
    int color;


    public TvShowAdapter(Context context, FragmentManager supportFragmentManager, TvSeries series, int color_top) {
        super(supportFragmentManager);
        this.context = context;
        this.series = series;
        this.color = color_top;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.d("TvShowAdapter", "Series " + series.getName());
            return TvShowFragment.newInstance(R.string.informacoes,  series, color);
        }
        if (position == 1) {
            return TvShowFragment.newInstance(R.string.temporadas, series, color);
        }
        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.informacoes);
        }
        if (position == 1) {
            return context.getString(R.string.temporadas);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}