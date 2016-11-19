package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
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

import adapter.FavoriteAdapater;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.UtilsFilme;

public class FavoriteActivity extends BaseActivity {

    private static final String TAG = FavoriteActivity.class.getName();
    ViewPager viewPager;
    TabLayout tabLayout;
    List<MovieDb> movieDbs = new ArrayList<>();
    List<TvSeries> tvSeries = new ArrayList<>();
    ProgressBar progressBar;
    LinearLayout linearLayout;
    List<String> listMovie = new ArrayList<>();
    List<String> listTv = new ArrayList<>();

    private DatabaseReference favoriteMovie, favoriteTv;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private ValueEventListener valueEventFavoriteMovie;
    private ValueEventListener valueEventFavoriteTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usuario_list);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.favorite);
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
        setEventListenerFavorite();

    }

    private void iniciarFirebases() {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        favoriteMovie = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("favorites")
                .child("movie");

        favoriteTv = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("favorites")
                .child("tvshow");
    }

    protected void snack() {
        Snackbar.make(linearLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            //text_elenco_no_internet.setVisibility(View.GONE);
                            new FavoriteAsync().execute();
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
        viewPager.setAdapter(new FavoriteAdapater(FavoriteActivity.this, getSupportFragmentManager(),
                movieDbs, tvSeries));

    }

    private void setEventListenerFavorite() {
        valueEventFavoriteMovie = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "Movie " + snapshot.getKey() );
                        if (snapshot.exists()) {
                            String movie = snapshot.getKey();
                            listMovie.add(movie);
                        }
                    }
                }
                setEventListenerFavoriteTv();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        favoriteMovie.addValueEventListener(valueEventFavoriteMovie);
    }

    private void setEventListenerFavoriteTv() {
        valueEventFavoriteTv = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Log.d(TAG, "TV " + snapshot.getKey() );
                        if (snapshot.exists()) {
                            String tv = snapshot.getKey();
                            listTv.add(tv);
                        }
                    }
                }

                if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                    new FavoriteAsync().execute();
                } else {
                    snack();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        favoriteTv.addValueEventListener(valueEventFavoriteTv);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (favoriteMovie != null){
            favoriteMovie.removeEventListener(valueEventFavoriteMovie);
        }
        if (favoriteTv != null) {
            favoriteTv.removeEventListener(valueEventFavoriteTv);
        }
    }

    private class FavoriteAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            for (String id : listMovie) {

               movieDbs.add(FilmeService.getTmdbMovies().getMovie(Integer.parseInt(id), "en", null));
            }

            for (MovieDb movieDb : movieDbs) {
                Log.d(TAG, movieDb.getTitle());
            }

            for (String id : listTv) {

                tvSeries.add(FilmeService.getTmdbTvShow().getSeries(Integer.parseInt(id), "en", null));
            }

            for (TvSeries movieDb : tvSeries) {
                Log.d(TAG, movieDb.getName());
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupViewPagerTabs();
            Log.d(TAG, "Progress");
            progressBar.setVisibility(View.GONE);
        }
    }

}
