package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.View;

import java.util.List;

import activity.FilmeActivity;
import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.TvShowFragment;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.tv.TvSeason;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 23/08/16.
 */
public class TvShowAdapter extends FragmentPagerAdapter {

    Context context;
    TvSeries series;


    public TvShowAdapter(Context context, FragmentManager supportFragmentManager, TvSeries series) {
        super(supportFragmentManager);
        this.context = context;
        this.series = series;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            Log.d("TvShowAdapter", "Series " + series.getName());
            return TvShowFragment.newInstance(R.string.informacoes,  series);
        }
        if (position == 1) {
            return TvShowFragment.newInstance(R.string.temporadas, series);
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