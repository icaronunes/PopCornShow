package adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.icaro.filme.R;
import fragment.PersonFragment;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonAdapter extends FragmentPagerAdapter {
    private final int id_person;
    Context context;

    public PersonAdapter(Context context, FragmentManager supportFragmentManager, int id_person) {
        super(supportFragmentManager);
        this.context = context;
        this.id_person = id_person;

    }


    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return PersonFragment.newInstance(R.string.tvshow, id_person);
        }
        if (position == 1) {
            return PersonFragment.newInstance(R.string.filme, id_person);
        }
        if (position == 2) {
            return PersonFragment.newInstance(R.string.person, id_person);
        }
        if (position == 3){
            return PersonFragment.newInstance(R.string.imagem_person, id_person);
        }
        if (position == 4) {
            return PersonFragment.newInstance(R.string.producao, id_person);
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if (position == 0) {
            return context.getString(R.string.tvshow);
        }
        if (position == 1) {
            return context.getString(R.string.filme);
        }
        if (position == 2) {
            return context.getString(R.string.person);
        }
        if (position == 3) {
            return context.getString(R.string.imagem_person);
        }
        if (position == 4) {
            return context.getString(R.string.producao);
        }

        return null;
    }

    @Override
    public int getCount() {
        return 5;
    }
}
