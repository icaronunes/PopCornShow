package activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.analytics.FirebaseAnalytics;

import adapter.PersonAdapter;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;

import static com.google.android.gms.analytics.internal.zzy.t;

public class PersonActivity extends BaseActivity {

    int id_person;
    String nome;
    ViewPager viewPager;
    FirebaseAnalytics firebaseAnalytics;
    final static String TAG = "PersonActivity";

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
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity " + this.getClass());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TabLayout " + "Perfil");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

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
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new PersonAdapter(getContext(), getSupportFragmentManager(), id_person));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, tab.getText().toString());
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TabLayout " + tab.getText());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }
}
