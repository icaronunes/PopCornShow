package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import activity.EpsodioActivity;
import br.com.icaro.filme.R;
import fragment.EpsodioFragment;
import fragment.TvShowFragment;
import info.movito.themoviedbapi.model.tv.TvSeason;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioAdapter extends FragmentPagerAdapter {

    Context context;
    TvSeason tvSeason;

    public EpsodioAdapter(EpsodioActivity epsodioActivity, FragmentManager supportFragmentManager, TvSeason tvSeason) {
        super(supportFragmentManager);
        this.context = epsodioActivity;
        this.tvSeason = tvSeason;
    }

    @Override
    public Fragment getItem(int position) {
            return EpsodioFragment.newInstance(tvSeason.getEpisodes().get(position), position);
    }

    @Override
    public int getCount() {
        return tvSeason.getEpisodes().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (tvSeason.getEpisodes().get(position).getEpisodeNumber() <= 9) {
            return "E0"+  String.valueOf(tvSeason.getEpisodes().get(position).getEpisodeNumber());
        } else {
            return  "E"+  String.valueOf(tvSeason.getEpisodes().get(position).getEpisodeNumber());
        }
    }
}
