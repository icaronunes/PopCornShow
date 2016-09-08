package activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import adapter.PersonAdapter;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;

import static com.google.android.gms.analytics.internal.zzy.t;

public class PersonActivity extends BaseActivity {

    int id_person;
    String nome;
    ViewPager viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person);
        setUpToolBar();
        nome = getIntent().getStringExtra(Constantes.NOME_PERSON);
        id_person = getIntent().getIntExtra(Constantes.PERSON_ID, 0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(nome);
        setupViewPagerTabs();

    }

    private Context getContext() {
        return this;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    private void setupViewPagerTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager_person);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(new PersonAdapter(getContext(), getSupportFragmentManager(), id_person));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
    }
}
