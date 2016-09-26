package fragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import java.util.List;

import domian.TopMain;

public class ViewPageMainTopFragment extends FragmentPagerAdapter {

    List<TopMain> multis;

    public ViewPageMainTopFragment(FragmentManager supportFragmentManager, List<TopMain> objects) {
        super(supportFragmentManager);
        this.multis = objects;
    }


    @Override
    public Fragment getItem(int position) {

        Log.d("ViewPageMainTopFragment", multis.get(position).toString());
        return new ImagemTopScrollFragment().newInstance(multis.get(position));
    }

    @Override
    public int getCount() {
        return multis.size();
    }
}