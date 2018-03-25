package adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import domain.TvSeasons;
import domain.UserSeasons;
import fragment.EpsodioFragment;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioAdapter extends FragmentPagerAdapter {

    private final boolean seguindo;
    private TvSeasons tvSeason;
    private String nome_serie;
    private int tvshow_id, color, temporada_position;
    private UserSeasons seasons;


    public EpsodioAdapter(FragmentManager supportFragmentManager,
                          TvSeasons tvSeason, String nome_serie, int tvshowid,
                          int color, boolean seguindo, UserSeasons seasons, int temporada_position) {
        super(supportFragmentManager);
        this.tvSeason = tvSeason;
        this.nome_serie = nome_serie;
        this.tvshow_id = tvshowid;
        this.color = color;
        this.seguindo = seguindo;
        this.seasons = seasons;
        this.temporada_position = temporada_position;

    }

    @Override
    public Fragment getItem(int position) {
        return EpsodioFragment.newInstance(tvSeason.getEpisodes().get(position), nome_serie,
                tvshow_id, color, seguindo, position, seasons, temporada_position);
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
