package pessoa.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import br.com.icaro.filme.R;
import domain.person.Person;
import pessoa.fragment.PersonFragment;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonAdapter extends FragmentPagerAdapter {
    private final Person person;
    private Context context;

    public PersonAdapter(Context context, FragmentManager supportFragmentManager, Person person) {
        super(supportFragmentManager);
        this.context = context;
        this.person = person;
    }


    @Override
    public Fragment getItem(int position) {

        if (position == 0) {
            return PersonFragment.Companion.newInstance(R.string.tvshow, person);
        }
        if (position == 1) {
            return PersonFragment.Companion.newInstance(R.string.filme, person);
        }
        if (position == 2) {
            return PersonFragment.Companion.newInstance(R.string.person, person);
        }
        if (position == 3) {
            return PersonFragment.Companion.newInstance(R.string.imagem_person, person);
        }
        if (position == 4) {
            return PersonFragment.Companion.newInstance(R.string.producao, person);
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
