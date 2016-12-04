package activity;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

import adapter.PersonAdapter;
import br.com.icaro.filme.R;
import utils.Constantes;
import utils.UtilsFilme;

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
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getExtras();
        viewPager = (ViewPager) findViewById(R.id.viewPager_person);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(nome);

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Activity " + this.getClass());
        bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "TabLayout " + "Perfil");
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

        if (UtilsFilme.isNetWorkAvailable(getContext())) {

            setupViewPagerTabs();
        } else {
            snack();
        }

    }

    private void getExtras() {
        if (getIntent().getAction() == null){
            nome = getIntent().getStringExtra(Constantes.NOME_PERSON);
            id_person = getIntent().getIntExtra(Constantes.PERSON_ID, 0);
        } else {
            nome = getIntent().getStringExtra(Constantes.NOME_PERSON);
            id_person = Integer.parseInt(getIntent().getStringExtra(Constantes.PERSON_ID));
        }
    }

    protected void snack() {
        Snackbar.make(viewPager, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getContext())) {
                            setupViewPagerTabs();
                        } else {
                            snack();
                        }
                    }
                }).show();
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

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new PersonAdapter(getContext(), getSupportFragmentManager(), id_person));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setCurrentItem(2);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
               // Log.d(TAG, tab.getText().toString());
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
