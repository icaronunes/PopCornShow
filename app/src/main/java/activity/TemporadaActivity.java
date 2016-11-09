package activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

import adapter.TemporadaAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import domian.UserSeasons;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

/**
 * Created by icaro on 26/08/16.
 */
public class TemporadaActivity extends BaseActivity {

    int temporada_id;
    String nome, nome_temporada;
    int serie_id, color;
    TvSeason tvSeason;
    RecyclerView recyclerView;
    private boolean seguindo;
    UserSeasons seasons;
    public static final String TAG = TemporadaActivity.class.getName();

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ValueEventListener postListener;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporada_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        temporada_id = getIntent().getIntExtra(Constantes.TEMPORADA_ID, 0);
        serie_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
        color = getIntent().getIntExtra(Constantes.COLOR_TOP, 0);
        nome_temporada = getIntent().getStringExtra(Constantes.NOME);
        getSupportActionBar().setTitle(nome_temporada);
        nome = getIntent().getStringExtra(Constantes.NOME_TVSHOW);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_temporada);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        mAuth = FirebaseAuth.getInstance();
        myRef =  FirebaseDatabase.getInstance().getReference("users");

        new TMDVAsync().execute();

//        postListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//
//                if (dataSnapshot.exists()) {
//                    Log.d(TAG, "key listener: " + dataSnapshot.getKey());
//                    seasons = dataSnapshot.getValue(UserSeasons.class);
//                    recyclerView
//                            .setAdapter(new TemporadaAdapter(TemporadaActivity
//                                    .this, tvSeason, serie_id, nome, color, nome_temporada, seasons, seguindo));
//                    Log.d(TAG, "true");
//                    Log.d(TAG, tvSeason.getName());
//                } else {
//                    recyclerView
//                            .setAdapter(new TemporadaAdapter(TemporadaActivity
//                                    .this, tvSeason, serie_id, nome, color, nome_temporada, seasons, seguindo));
//                    Log.d(TAG, "key listener: " + dataSnapshot.getKey());
//                    Log.d(TAG, "False");
//
//
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                // Getting Post failed, log a message
//                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
//                // ...
//            }
//        };
//        myRef.child(mAuth.getCurrentUser().getUid())
//                .child(String.valueOf(serie_id))
//                .child("seasons")
//                .child(String.valueOf(temporada_id)).addValueEventListener(postListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // myRef.removeEventListener(postListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TemporadaActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            if (idioma_padrao) {
                tvSeason = FilmeService.getTmdbTvSeasons()
                        .getSeason(serie_id, temporada_id, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry() + ",en,null");
                Log.w(TAG, tvSeason.getName());
                return null;
            }else {
                tvSeason = FilmeService.getTmdbTvSeasons()
                        .getSeason(serie_id, temporada_id, ",en,null"); //????
                Log.w(TAG, tvSeason.getName());
                return null;
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


                myRef.child(mAuth.getCurrentUser().getUid())
                        .child(String.valueOf(serie_id))
                        .child("seasons")
                        .child(String.valueOf(temporada_id))
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value
                                        if (dataSnapshot.exists()) {
                                            seguindo = true;
                                            Log.w(TAG, "seguindo - true");
                                            seasons = dataSnapshot.getValue(UserSeasons.class);
                                            recyclerView
                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                                            tvSeason, serie_id, nome, color, nome_temporada, seasons ,seguindo));
                                        } else {
                                            Log.d(TAG, "onDataChange " + "Não seguindo.");
                                            seguindo = false;
                                            recyclerView
                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                                            tvSeason, serie_id, nome, color, nome_temporada, seasons ,seguindo));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });

            //recyclerView.addItemDecoration(new DividerItemDecoration(TemporadaActivity.this, DividerItemDecoration.VERTICAL_LIST));
            //recyclerView.setAdapter(new SampleAdapter(TemporadaActivity.this, recyclerView, tvSeason, serie_id, nome, color, nome_temporada));
            //recyclerView
            //        .setAdapter(new TemporadaAdapter(TemporadaActivity.this, tvSeason, serie_id, nome, color, nome_temporada, seasons ,seguindo));
        }
    }
}
