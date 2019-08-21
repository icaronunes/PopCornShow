package adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import br.com.icaro.filme.R;
import domain.UserTvshow;
import fragment.ListaSeguindoFragment;
import seguindo.SeguindoActivity;

/**
 * Created by icaro on 25/11/16.
 */

public class FollowingAdapater extends FragmentPagerAdapter {

    private Context context;
    private List<UserTvshow> userTvshows;

    public FollowingAdapater(SeguindoActivity seguindoActivity,
                             FragmentManager supportFragmentManager, List<UserTvshow> userTvshows) {
        super(supportFragmentManager);
        this.context = seguindoActivity;
        this.userTvshows = userTvshows;
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return ListaSeguindoFragment.Companion.newInstance(position, userTvshows);
        } else {
            return ListaSeguindoFragment.Companion.newInstance(position, userTvshows);
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.proximos);
        } else {
            return context.getString(R.string.seguindo);
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
