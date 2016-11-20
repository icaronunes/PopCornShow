package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
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

import adapter.WatchListAdapter;
import br.com.icaro.filme.R;
import domian.FilmeDB;
import domian.TvshowDB;
import utils.UtilsFilme;

public class WatchListActivity extends BaseActivity {

    private static final String TAG = WatchListActivity.class.getName();
    ViewPager viewPager;
    TabLayout tabLayout;
    LinearLayout linearLayout;
    ProgressBar progressBar;
    List<FilmeDB> movieDbs = new ArrayList<>();
    List<TvshowDB> tvSeries = new ArrayList<>();
    private DatabaseReference favoriteMovie, favoriteTv;
    private ValueEventListener valueEventFavoriteMovie;
    private ValueEventListener valueEventFavoriteTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.quero_assistir);
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
        iniciarFirebases();

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

        favoriteMovie = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("watch")
                .child("movie");

        favoriteTv = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("watch")
                .child("tvshow");
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
        viewPager.setAdapter(new WatchListAdapter(WatchListActivity.this, getSupportFragmentManager(),
                tvSeries, movieDbs));
    }

    private void setEventListenerFavorite() {
        valueEventFavoriteMovie = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        movieDbs.add(snapshot.getValue(FilmeDB.class));
                        Log.d(TAG, snapshot.getValue(FilmeDB.class).getTitle());
                    }
                }
                setEventListenerFavoriteTv();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        favoriteMovie.addListenerForSingleValueEvent(valueEventFavoriteMovie);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
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

    private void setEventListenerFavoriteTv() {
        valueEventFavoriteTv = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        tvSeries.add(snapshot.getValue(TvshowDB.class));
                        Log.d(TAG, snapshot.getValue(TvshowDB.class).getTitle());
                    }
                }

                setupViewPagerTabs();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        favoriteTv.addListenerForSingleValueEvent(valueEventFavoriteTv);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
    }

}

