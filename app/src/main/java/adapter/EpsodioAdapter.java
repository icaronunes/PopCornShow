package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import activity.EpsodioActivity;
import fragment.EpsodioFragment;
import info.movito.themoviedbapi.model.tv.TvSeason;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioAdapter extends FragmentPagerAdapter {

    Context context;
    TvSeason tvSeason;
    String nome_serie;
    int tvshow_id, color;

    public EpsodioAdapter(EpsodioActivity epsodioActivity, FragmentManager supportFragmentManager,
                          TvSeason tvSeason, String nome_serie, int tvshowid, int color) {
        super(supportFragmentManager);
        this.context = epsodioActivity;
        this.tvSeason = tvSeason;
        this.nome_serie = nome_serie;
        this.tvshow_id = tvshowid;
        this.color = color;

    }

    @Override
    public Fragment getItem(int position) {
        return EpsodioFragment.newInstance(tvSeason.getEpisodes().get(position), nome_serie, tvshow_id, color);
    }

    @Override
    public int getCount() {
        return tvSeason.getEpisodes().size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        if (tvSeason.getEpisodes().get(position).getEpisodeNumber() <= 9) {
            return "E0" + String.valueOf(tvSeason.getEpisodes().get(position).getEpisodeNumber());
        } else {
            return "E" + String.valueOf(tvSeason.getEpisodes().get(position).getEpisodeNumber());
        }
    }
}
