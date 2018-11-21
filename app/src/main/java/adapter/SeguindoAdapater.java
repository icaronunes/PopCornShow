package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.List;

import seguindo.SeguindoActivity;
import br.com.icaro.filme.R;
import domain.UserTvshow;
import fragment.ListaSeguindoFragment;

/**
 * Created by icaro on 25/11/16.
 */

public class SeguindoAdapater extends FragmentPagerAdapter {

    private final String TAG = SeguindoAdapater.class.getName();
    private Context context;
    private List<UserTvshow> userTvshows;


    public SeguindoAdapater(SeguindoActivity seguindoActivity,
                            FragmentManager supportFragmentManager, List<UserTvshow> userTvshows) {
        super(supportFragmentManager);
        this.context = seguindoActivity;
        this.userTvshows = userTvshows;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ListaSeguindoFragment.Companion.newInstance(position, userTvshows);
        }

        if (position == 1){
            return ListaSeguindoFragment.Companion.newInstance(position, userTvshows);
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.proximos);
        }
        if (position == 1) {
            return context.getString(R.string.seguindo);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

}
