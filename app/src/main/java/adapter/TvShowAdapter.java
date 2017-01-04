package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.icaro.filme.R;
import domian.Netflix;
import fragment.TvShowFragment;
import info.movito.themoviedbapi.model.tv.TvSeries;

/**
 * Created by icaro on 23/08/16.
 */
public class TvShowAdapter extends FragmentPagerAdapter {

    public static final String TAG = TvShowAdapter.class.getName();
    Context context;
    TvSeries series;
    int color;
    boolean seguindo;
    Netflix netflix;


    public TvShowAdapter(Context context, FragmentManager supportFragmentManager, TvSeries series, int color_top, boolean seguindo, Netflix netflix) {
        super(supportFragmentManager);
        this.context = context;
        this.series = series;
        this.color = color_top;
        this.seguindo = seguindo;
        this.netflix = netflix;
        //Log.d(TAG, "Adapter " +userTvshow.getNome());
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
           // Log.d("TvShowAdapter", "Series " + series.getName());
            return TvShowFragment.newInstance(R.string.informacoes,  series, color, seguindo, netflix);
        }
        if (position == 1) {
            return TvShowFragment.newInstance(R.string.temporadas, series, color, seguindo, netflix);
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