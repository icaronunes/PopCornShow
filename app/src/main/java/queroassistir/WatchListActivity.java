package queroassistir;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import activity.BaseActivity;
import br.com.icaro.filme.R;
import domain.MovieDb;
import domain.TvshowDB;
import utils.UtilsApp;

public class WatchListActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private LinearLayout linearLayout;
    private List<MovieDb> movieDbs = new ArrayList<>();
    private List<TvshowDB> tvSeries = new ArrayList<>();
    private DatabaseReference favoriteMovie, favoriteTv;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_list);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.quero_assistir);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.viewpage_usuario);
        tabLayout = findViewById(R.id.tabLayout);
        linearLayout = findViewById(R.id.linear_usuario_list);

        if (UtilsApp.INSTANCE.isNetWorkAvailable(this)){
            iniciarFirebases();
          //  setEventListenerFavorite();
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

        setEventListenerFavorite();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("deprecation")
    private void setupViewPagerTabs() {

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        viewPager.setAdapter(new WatchListAdapter(WatchListActivity.this, getSupportFragmentManager(),
                tvSeries, movieDbs));
    }

    private void setEventListenerFavorite() {
        ValueEventListener valueEventFavoriteMovie = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        movieDbs.add(snapshot.getValue(MovieDb.class));
                        // Log.d(TAG, snapshot.getValue(FilmeDB.class).getTitle());
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
                        if (UtilsApp.INSTANCE.isNetWorkAvailable(getBaseContext())) {
                            //text_elenco_no_internet.setVisibility(View.GONE);
                            setEventListenerFavorite();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    private void setEventListenerFavoriteTv() {
        ValueEventListener valueEventFavoriteTv = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        tvSeries.add(snapshot.getValue(TvshowDB.class));
                        // Log.d(TAG, snapshot.getValue(TvshowDB.class).getTitle());
                    }
                }

                setupViewPagerTabs();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        favoriteTv.addListenerForSingleValueEvent(valueEventFavoriteTv);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
    }

}

