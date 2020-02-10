package pessoa.adapter;

import android.content.Context;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import br.com.icaro.filme.R;
import domain.person.Person;
import fragment.FragmentError;
import pessoa.fragment.PersonFragment;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonAdapter extends FragmentPagerAdapter {
    private Context context;
    private Boolean error;

    public PersonAdapter(Context context, FragmentManager supportFragmentManager, Boolean error) {
        super(supportFragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        this.context = context;
        this.error = error;
    }

    @Override
    public Fragment getItem(int position) {
        if(error) return new FragmentError();

        if (position == 0) {
            return PersonFragment.Companion.newInstance(R.string.tvshow);
        }
        if (position == 1) {
            return PersonFragment.Companion.newInstance(R.string.filme);
        }
        if (position == 2) {
            return PersonFragment.Companion.newInstance(R.string.person);
        }
        if (position == 3) {
            return PersonFragment.Companion.newInstance(R.string.imagem_person);
        }
        if (position == 4) {
            return PersonFragment.Companion.newInstance(R.string.producao);
        }

        return null;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        if(error) return "Error :(";

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
        } else {
            return "Error";
        }
    }

    @Override
    public int getCount() {
        if(error) return 1;
        return 5;
    }
}
