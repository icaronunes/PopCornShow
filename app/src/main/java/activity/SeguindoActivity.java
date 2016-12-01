package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.SeguindoAdapater;
import br.com.icaro.filme.R;
import domian.UserTvshow;
import utils.UtilsFilme;

/**
 * Created by icaro on 25/11/16.
 */
public class SeguindoActivity extends BaseActivity{

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private DatabaseReference seguindoDataBase;
    private ValueEventListener eventListener;
    private List<UserTvshow> userTvshows = new ArrayList<>();
    private final String TAG = SeguindoActivity.class.getName();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.seguindo);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpage_usuario);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        linearLayout = (LinearLayout) findViewById(R.id.linear_usuario_list);

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        if (UtilsFilme.isNetWorkAvailable(this)){

            iniciarFirebases();
            setEventListenerFavorite();
        } else {
            snack();
        }
    }

    private void iniciarFirebases() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        seguindoDataBase = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("seguindo");

    }

    protected void snack() {
        Snackbar.make(linearLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            //text_elenco_no_internet.setVisibility(View.GONE);
                            setEventListenerFavorite();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupViewPagerTabs() {

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        viewPager.setAdapter(new SeguindoAdapater(SeguindoActivity.this, getSupportFragmentManager(),
                userTvshows));
    }

    private void setEventListenerFavorite() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTvshows = new ArrayList<>();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        userTvshows.add(snapshot.getValue(UserTvshow.class));
                        Log.d(TAG, snapshot.getValue(UserTvshow.class).getNome());
                    }
                }
                setupViewPagerTabs();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        seguindoDataBase.addListenerForSingleValueEvent(eventListener);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null){
            seguindoDataBase.removeEventListener(eventListener);
        }
    }

}
