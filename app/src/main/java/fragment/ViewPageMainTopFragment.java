package fragment;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.List;

import domain.TopMain;

public class ViewPageMainTopFragment extends FragmentPagerAdapter {

    private List<TopMain> multis;

    public ViewPageMainTopFragment(FragmentManager supportFragmentManager, List<TopMain> objects) {
        super(supportFragmentManager);
        this.multis = objects;
    }


    @Override
    public Fragment getItem(int position) {

       // Log.d("ViewPageMainTopFragment", multis.get(position).toString());
        return new ImagemTopScrollFragment().newInstance(multis.get(position));
    }

    @Override
    public int getCount() {
        return multis.size();
    }
}