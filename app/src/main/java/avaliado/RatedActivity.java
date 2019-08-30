package avaliado;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

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
import domain.FilmeDB;
import domain.TvshowDB;
import utils.UtilsApp;

public class RatedActivity extends BaseActivity {

    private ViewPager viewPager;
    private TabLayout tabLayout;
    private List<FilmeDB> movieDbs = new ArrayList<>();
    private List<TvshowDB> tvSeries = new ArrayList<>();
    private LinearLayout linearLayout;
    private ProgressBar progressBar;

    private DatabaseReference ratedMovie, ratedTv;
    private ValueEventListener valueEventFavoriteMovie;
    private ValueEventListener valueEventFavoriteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_list);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.avaliados);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = findViewById(R.id.viewpage_usuario);
        tabLayout = findViewById(R.id.tabLayout);
        progressBar = findViewById(R.id.progress);
        linearLayout = findViewById(R.id.linear_usuario_list);

        if (UtilsApp.isNetWorkAvailable(this)){

            iniciarFirebases();
            setEventListenerFavorite();
        } else {
            snack();
        }
    }

    private void iniciarFirebases() {

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        ratedMovie = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("rated")
                .child("movie");

        ratedTv = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("rated")
                .child("tvshow");
    }

    protected void snack() {
        Snackbar.make(linearLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsApp.isNetWorkAvailable(getBaseContext())) {
                            setEventListenerFavorite();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    private void setEventListenerFavorite() {
        valueEventFavoriteMovie = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        movieDbs.add(snapshot.getValue(FilmeDB.class));
                       // Log.d(TAG, snapshot.getValue(FilmeDB.class).getTitle());
                    }
                }
                setEventListenerFavoriteTv();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ratedMovie.addListenerForSingleValueEvent(valueEventFavoriteMovie);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
    }

    private void setEventListenerFavoriteTv() {
        valueEventFavoriteTv = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        tvSeries.add(snapshot.getValue(TvshowDB.class));
                       // Log.d(TAG, snapshot.getValue(TvshowDB.class).getTitle());
                    }
                }

                setupViewPagerTabs();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        ratedTv.addListenerForSingleValueEvent(valueEventFavoriteTv);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
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
        tabLayout.setSelectedTabIndicatorColor(getBaseContext().getResources().getColor(R.color.accent));
        viewPager.setAdapter(new RatedAdapater(RatedActivity.this, getSupportFragmentManager(),
                tvSeries, movieDbs));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventFavoriteMovie != null){
            ratedMovie.removeEventListener(valueEventFavoriteMovie);
        }
        if (valueEventFavoriteTv != null) {
            ratedTv.removeEventListener(valueEventFavoriteTv);
        }
    }
}
