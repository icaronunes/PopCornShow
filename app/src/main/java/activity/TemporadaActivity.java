package activity;

import android.content.Intent;
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
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    public static final String TAG = TemporadaActivity.class.getName();

    int temporada_id;
    private String nome_temporada;
    int serie_id, color;
    private TvSeason tvSeason;
    private RecyclerView recyclerView;
    private boolean seguindo;
    private UserSeasons seasons;
    private int position;

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
        getExtras();
        getSupportActionBar().setTitle("");
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

    }

    public void getExtras() {

        if (getIntent().getAction() == null){
            temporada_id = getIntent().getIntExtra(Constantes.TEMPORADA_ID, 0);
            serie_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
            nome_temporada = getIntent().getStringExtra(Constantes.NOME);
            color = getIntent().getIntExtra(Constantes.COLOR_TOP, 0);

        } else {
            temporada_id = Integer.parseInt(getIntent().getStringExtra(Constantes.TEMPORADA_ID));
            serie_id = Integer.parseInt(getIntent().getStringExtra(Constantes.TVSHOW_ID));
            nome_temporada = getIntent().getStringExtra(Constantes.NOME);
            color = Integer.parseInt(getIntent().getStringExtra(Constantes.COLOR_TOP));
        }

    }

    private TemporadaAdapter.TemporadaOnClickListener onClickListener(){
        return new TemporadaAdapter.TemporadaOnClickListener() {

            @Override
            public void onClickVerTemporada(View view, int position) {
               TemporadaActivity.this.position = position;
                if (seasons != null) {
                    if (seasons.getUserEps().get(position).isAssistido()) {
                        Log.d(TAG, "visto");
                        String id = String.valueOf(serie_id);

                        Toast.makeText(TemporadaActivity.this, R.string.marcado_nao_assistido, Toast.LENGTH_SHORT).show();

                        String user = mAuth.getCurrentUser().getUid();

                        Map<String, Object> childUpdates = new HashMap<String, Object>();

                        childUpdates.put("/"+user+"/"+id+"/seasons/"+tvSeason.getSeasonNumber()+"/userEps/"+position+"/assistido", false);
                        childUpdates.put("/"+user+"/"+id+"/seasons/"+tvSeason.getSeasonNumber()+"/visto/", false);

                        myRef.updateChildren(childUpdates);
                        Log.d(TAG, "desvisto");

                    } else {
                        Log.d(TAG, "não visto");
                        String id = String.valueOf(serie_id);
                        myRef.child(mAuth.getCurrentUser().getUid())
                                .child(id)
                                .child("seasons")
                                .child(String.valueOf(tvSeason.getSeasonNumber()))
                                .child("userEps")
                                .child(String.valueOf(position))
                                .child("assistido")
                                .setValue(true);
                        Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onClickTemporada(View view, int position) {
                Intent intent = new Intent(TemporadaActivity.this, EpsodioActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, serie_id);
                intent.putExtra(Constantes.TVSEASON_ID, tvSeason.getId());
                intent.putExtra(Constantes.EPSODIO_ID, tvSeason.getEpisodes().get(position).getId());
                intent.putExtra(Constantes.POSICAO, position);
                intent.putExtra(Constantes.TVSEASONS, tvSeason);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.NOME, nome_temporada);
                intent.putExtra(Constantes.USER, seasons );
                intent.putExtra(Constantes.SEGUINDO, seguindo);
                startActivity(intent);

                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(TemporadaActivity.this);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TemporadaAdapter.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, tvSeason.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeason.getName());
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, EpsodioActivity.class.getName());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        };
    }



    private void setListener(){

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && seguindo) {
                    Log.d(TAG, "key listener: " + dataSnapshot.getKey());
                    seasons = dataSnapshot.getValue(UserSeasons.class);
                    recyclerView
                            .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                    tvSeason, seasons ,seguindo,
                                    onClickListener() ));
                    Log.d(TAG, "true");
                    Log.d(TAG, "assistido " + seasons.getUserEps().get(position).isAssistido());
                    Log.d(TAG, tvSeason.getName());

                } else {
                    Log.d(TAG, "false");
                    Log.d(TAG, "nao assistido " + seasons.getUserEps().get(position).isAssistido());
                    seasons = dataSnapshot.getValue(UserSeasons.class);
                    recyclerView
                            .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                    tvSeason, seasons ,seguindo,
                                    onClickListener() ));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.child(mAuth.getCurrentUser().getUid())
                .child(String.valueOf(serie_id))
                .child("seasons")
                .child(String.valueOf(temporada_id)).addValueEventListener(postListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myRef.child(mAuth.getCurrentUser().getUid())
                .child(String.valueOf(serie_id))
                .child("seasons")
                .child(String.valueOf(temporada_id)).removeEventListener(postListener);
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
        return true;
    }



    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TemporadaActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            if (idioma_padrao) {
                tvSeason = FilmeService.getTmdbTvSeasons()
                        .getSeason(serie_id, temporada_id, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry() + ",en,null");
                Log.d("TemporadaActivity", tvSeason.getName());
                return null;
            }else {
                tvSeason = FilmeService.getTmdbTvSeasons()
                        .getSeason(serie_id, temporada_id, ",en,null"); //????
                return null;
            }

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada );

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
                                                            tvSeason, seasons ,seguindo,
                                                            onClickListener() ));
                                        } else {
                                            Log.d(TAG, "onDataChange " + "Não seguindo.");
                                            seguindo = false;
                                            recyclerView
                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                                            tvSeason, seasons ,seguindo,
                                                            onClickListener() ));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
            setListener();
        }
    }
}
