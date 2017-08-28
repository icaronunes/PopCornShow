package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Keep;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

import adapter.TemporadaAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.UserEp;
import domain.UserSeasons;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 26/08/16.
 */
@Keep
public class TemporadaActivity extends BaseActivity {

    private final String TAG = TemporadaActivity.class.getName();

    private int temporada_id, temporada_position, positionep;
    private String nome_temporada;
    private int serie_id, color;
    private TvSeason tvSeason;
    private RecyclerView recyclerView;
    private boolean seguindo;
    private UserSeasons seasons;
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

//        AdView adview = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                .build();
//        adview.loadAd(adRequest);

        mAuth = FirebaseAuth.getInstance();
        myRef =  FirebaseDatabase.getInstance().getReference("users");


        if (UtilsFilme.isNetWorkAvailable(this)) {
            new TMDVAsync().execute();
        } else {
            snack();
        }

    }

    protected void snack() {
        Snackbar.make(recyclerView, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            new TMDVAsync().execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    public void getExtras() {

        if (getIntent().getAction() == null){
            temporada_id = getIntent().getIntExtra(Constantes.INSTANCE.getTEMPORADA_ID(), 0);
            temporada_position = getIntent().getIntExtra(Constantes.INSTANCE.getTEMPORADA_POSITION(), 0);
            serie_id = getIntent().getIntExtra(Constantes.INSTANCE.getTVSHOW_ID(), 0);
            nome_temporada = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
            color = getIntent().getIntExtra(Constantes.INSTANCE.getCOLOR_TOP(), getResources().getColor(R.color.red));


        } else {
            temporada_id = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getTEMPORADA_ID()));
            temporada_position = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getTEMPORADA_POSITION()));
            //Criar campo no signal
            serie_id = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getTVSHOW_ID()));
            nome_temporada = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
            color = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getCOLOR_TOP()));
        }

    }

    private TemporadaAdapter.TemporadaOnClickListener onClickListener(){
        return new TemporadaAdapter.TemporadaOnClickListener() {

            @Override
            public void onClickVerTemporada(View view, final int position) {
                positionep = position;
                if (seasons != null) {
                    if (seasons.getUserEps().get(position).isAssistido()) {
                      //  Log.d(TAG, "FALSE");
                        String id = String.valueOf(serie_id);

                        Toast.makeText(TemporadaActivity.this, R.string.marcado_nao_assistido, Toast.LENGTH_SHORT).show();

                        String user = mAuth.getCurrentUser().getUid();

                        Map<String, Object> childUpdates = new HashMap<String, Object>();

                        childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/userEps/"+position+"/assistido", false);
                        childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/visto/", false);

                        myRef.updateChildren(childUpdates);
                    //    Log.d(TAG, "desvisto");

                    } else {
                      //  Log.d(TAG, "TRUE");
                            if (isAssistidoAnteriores(position)){
                                AlertDialog dialog = new AlertDialog.Builder(TemporadaActivity.this)
                                        .setTitle(R.string.title_marcar_ep_anteriores)
                                        .setMessage(R.string.msg_marcar_ep_anteriores)
                                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String id = String.valueOf(serie_id);

                                                String user = mAuth.getCurrentUser().getUid();

                                                Map<String, Object> childUpdates = new HashMap<String, Object>();
                                                for (int i = 0; i <= position; i++) {
                                                    childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/userEps/"+i+"/assistido", true);
                                                }

                                                if (position == seasons.getUserEps().size()-1){
                                                    childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/visto/",  true);
                                                } else {
                                                    childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/visto/",  TemporadaTodaAssistida(position));
                                                }

                                                myRef.updateChildren(childUpdates);

                                                Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                String id = String.valueOf(serie_id);

                                                String user = mAuth.getCurrentUser().getUid();

                                                Map<String, Object> childUpdates = new HashMap<String, Object>();

                                                childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/userEps/"+position+"/assistido", true);
                                                childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/visto/",  TemporadaTodaAssistida(position));

                                                myRef.updateChildren(childUpdates);

                                                Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                            }
                                        })
                                        .create();

                                dialog.show();
                            } else {

                                String id = String.valueOf(serie_id);

                                String user = mAuth.getCurrentUser().getUid();

                                Map<String, Object> childUpdates = new HashMap<String, Object>();

                                childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/userEps/"+position+"/assistido", true);
                                childUpdates.put("/"+user+"/seguindo/"+id+"/seasons/"+temporada_position+"/visto/",  TemporadaTodaAssistida(position));

                                myRef.updateChildren(childUpdates);

                                Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();

                            }
                        }
                }
            }

            @Override
            public void onClickTemporada(View view, int position) {
                Intent intent = new Intent(TemporadaActivity.this, EpsodioActivity.class);
                intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), serie_id);
                intent.putExtra(Constantes.INSTANCE.getTVSEASON_ID(), tvSeason.getId());
                intent.putExtra(Constantes.INSTANCE.getEPSODIO_ID(), tvSeason.getEpisodes().get(position).getId());
                intent.putExtra(Constantes.INSTANCE.getPOSICAO(), position);
                intent.putExtra(Constantes.INSTANCE.getTEMPORADA_POSITION(), temporada_position);
                intent.putExtra(Constantes.INSTANCE.getTVSEASONS(), tvSeason);
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                intent.putExtra(Constantes.INSTANCE.getNOME(), nome_temporada);
                intent.putExtra(Constantes.INSTANCE.getUSER(), seasons );
                intent.putExtra(Constantes.INSTANCE.getSEGUINDO(), seguindo);
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

    private boolean TemporadaTodaAssistida(int position) {
       // Log.d(TAG, "TemporadaTodaAssistida");
        for (UserEp userEp : seasons.getUserEps()) {
            if (!seasons.getUserEps().get(position).equals(userEp)) {
                if (!userEp.isAssistido()) {
              //      Log.d(TAG, "TemporadaTodaAssistida - false");
                    return false;
                }
            }
        }
        //Log.d(TAG, "TemporadaTodaAssistida - true");
        return true;
    }

    private boolean isAssistidoAnteriores(int position) {

        for (int i = 0; i < position; i++) {
            if (!seasons.getUserEps().get(i).isAssistido()){
                return true;
            }
        }
        return false;
    }


    private void setListener(){

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && seguindo) {
                  //  Log.d(TAG, "key listener: " + dataSnapshot.getKey());
                    seasons = dataSnapshot.getValue(UserSeasons.class);

//                    if (recyclerView.isShown()) {
//                        recyclerView.getAdapter().notifyItemChanged(positionep);
//
//                    } else {
//                        recyclerView
//                                .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
//                                        tvSeason, seasons, seguindo,
//                                        onClickListener()));
//                    }
                    FilmeApplication.getInstance().getBus().post(seasons);
                    recyclerView.getAdapter().notifyDataSetChanged();

                } else {

                    recyclerView
                            .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                    tvSeason, seasons ,seguindo,
                                    onClickListener() ));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
               // Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // ...
            }
        };
        myRef.child(mAuth.getCurrentUser().getUid())
                .child("seguindo")
                .child(String.valueOf(serie_id))
                .child("seasons")
                .child(String.valueOf(temporada_position)).addValueEventListener(postListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (postListener != null) {
            myRef.child(mAuth.getCurrentUser().getUid())
                    .child("seguindo")
                    .child(String.valueOf(serie_id))
                    .child("seasons")
                    .child(String.valueOf(temporada_position)).removeEventListener(postListener);
        }
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
            boolean idioma_padrao = false;
            try {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TemporadaActivity.this);
                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            } catch (Exception e){
                FirebaseCrash.report(e);
            }
            try {
                if (idioma_padrao) {
                    tvSeason = FilmeService.getTmdbTvSeasons()
                            .getSeason(serie_id, temporada_id, getLocale());

                    return null;
                } else {
                    tvSeason = FilmeService.getTmdbTvSeasons()
                            .getSeason(serie_id, temporada_id, "en"); //????
                    return null;
                }
            } catch (Exception e ){

                FirebaseCrash.report(e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (tvSeason == null) {return; }

            getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada );

            if (mAuth.getCurrentUser() != null) {
                myRef.child(mAuth.getCurrentUser().getUid())
                        .child("seguindo")
                        .child(String.valueOf(serie_id))
                        .child("seasons")
                        .child(String.valueOf(temporada_position))
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value
                                        if (dataSnapshot.exists()) {
                                            seguindo = true;
                                          //  Log.w(TAG, "seguindo - true");
                                            seasons = dataSnapshot.getValue(UserSeasons.class);
                                            recyclerView
                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                                            tvSeason, seasons, seguindo,
                                                            onClickListener()));
                                        } else {
                                         //   Log.d(TAG, "onDataChange " + "Não seguindo.");
                                            seguindo = false;
                                            recyclerView
                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                                            tvSeason, seasons, seguindo,
                                                            onClickListener()));
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                      //  Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
                setListener();
            } else {
                recyclerView
                        .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                tvSeason, seasons, seguindo,
                                onClickListener()));
            }

        }
    }
}
