package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.icaro.filme.R;
import domain.tvshow.Tvshow;
import tvshow.fragment.TvShowFragment;

/**
 * Created by icaro on 23/08/16.
 */
public class TvShowAdapter extends FragmentPagerAdapter {

    private Context context;
    private Tvshow series;
    private int color;
    private boolean seguindo;

    public TvShowAdapter(Context context, FragmentManager supportFragmentManager,
                         Tvshow series, int color_top, boolean seguindo) {
        super(supportFragmentManager);
        this.context = context;
        this.series = series;
        this.color = color_top;
        this.seguindo = seguindo;

    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {

            return TvShowFragment.Companion.newInstance(R.string.informacoes,  series, color, seguindo);
        }
        if (position == 1) {
            return TvShowFragment.Companion.newInstance(R.string.temporadas, series, color, seguindo);
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