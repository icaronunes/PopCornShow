package activity;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import adapter.TemporadaAdapter;
import adapter.TemporadaFoldinAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domain.Api;
import domain.EpisodesItem;
import domain.FilmeService;
import domain.TvSeasons;
import domain.UserEp;
import domain.UserSeasons;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import utils.Constantes;
import utils.UtilsApp;

import static java.lang.String.valueOf;

/**
 * Created by icaro on 26/08/16.
 */
@Keep
public class TemporadaActivity extends BaseActivity {

    private final String TAG = TemporadaActivity.class.getName();

    private int temporada_id, temporada_position;
    private String nome_temporada;
    private int serie_id, color;
    private TvSeasons tvSeason;
    private RecyclerView recyclerView;
    private boolean seguindo;
    private UserSeasons seasons;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private ValueEventListener postListener;
    private CompositeSubscription subscription;


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
        myRef = FirebaseDatabase.getInstance().getReference("users");
        subscription = new CompositeSubscription();


        if (UtilsApp.isNetWorkAvailable(this)) {
            //new TMDVAsync().execute();
            getDados();
        } else {
            snack();
        }

    }


    protected void snack() {
        Snackbar.make(recyclerView, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsApp.isNetWorkAvailable(getBaseContext())) {
                            //new TMDVAsync().execute();
                            getDados();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    public void getExtras() {

        if (getIntent().getAction() == null) {
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

    private TemporadaAdapter.TemporadaOnClickListener onClickListener() {
        return new TemporadaAdapter.TemporadaOnClickListener() {

            @Override
            public void onClickVerTemporada(View view, final int position) {

                if (seasons != null) {
                    if (seasons.getUserEps().get(position).isAssistido()) {
                        //  Log.d(TAG, "FALSE");
                        String id = String.valueOf(serie_id);

                        Toast.makeText(TemporadaActivity.this, R.string.marcado_nao_assistido, Toast.LENGTH_SHORT).show();

                        String user = mAuth.getCurrentUser().getUid();

                        Map<String, Object> childUpdates = new HashMap<String, Object>();

                        childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/userEps/" + position + "/assistido", false);
                        childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/visto/", false);

                        myRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError == null) {
                                    databaseReference
                                            .child(user).child("seguindo")
                                            .child(id).child("seasons")
                                            .child(String.valueOf(temporada_position))
                                            .child("userEps").child(String.valueOf(position))
                                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot.exists()) {
                                                        UserEp userEp = dataSnapshot.getValue(UserEp.class);
                                                        ((TemporadaFoldinAdapter) recyclerView.getAdapter()).notificarMudanca(userEp, position);
                                                    } else {
                                                        Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(DatabaseError databaseError) {

                                                }
                                            });

                                } else {
                                    Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    } else {

                        if (isAssistidoAnteriores(position)) {
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
                                                childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/userEps/" + i + "/assistido", true);
                                            }

                                            if (position == seasons.getUserEps().size() - 1) {
                                                childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/visto/", true);
                                            } else {
                                                childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/visto/", TemporadaTodaAssistida(position));
                                            }

                                            myRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        databaseReference.child(user).child("seguindo")
                                                                .child(id).child("seasons")
                                                                .child(String.valueOf(temporada_position))
                                                                .child("userEps")
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            for (int i = 0; i <= position; i++) {
                                                                                UserEp userEp = dataSnapshot.child(String.valueOf(i)).getValue(UserEp.class);
                                                                                ((TemporadaFoldinAdapter) recyclerView.getAdapter()).notificarMudanca(userEp, i);
                                                                            }
                                                                        } else {
                                                                            Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            String id = String.valueOf(serie_id);

                                            String user = mAuth.getCurrentUser().getUid();

                                            Map<String, Object> childUpdates = new HashMap<String, Object>();

                                            childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/userEps/" + position + "/assistido", true);
                                            childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/visto/", TemporadaTodaAssistida(position));

                                            myRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                                @Override
                                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                                    if (databaseError == null) {
                                                        databaseReference.child(user).child("seguindo")
                                                                .child(id).child("seasons")
                                                                .child(String.valueOf(temporada_position))
                                                                .child("userEps").child(String.valueOf(position))
                                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                                    @Override
                                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                                        if (dataSnapshot.exists()) {
                                                                            UserEp userEp = dataSnapshot.getValue(UserEp.class);
                                                                            ((TemporadaFoldinAdapter) recyclerView.getAdapter()).notificarMudanca(userEp, position);
                                                                        } else {
                                                                            Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onCancelled(DatabaseError databaseError) {

                                                                    }
                                                                });
                                                    } else {
                                                        Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });

                                            Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .create();

                            dialog.show();
                        } else {

                            String id = String.valueOf(serie_id);

                            String user = mAuth.getCurrentUser().getUid();

                            Map<String, Object> childUpdates = new HashMap<String, Object>();

                            childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/userEps/" + position + "/assistido", true);
                            childUpdates.put("/" + user + "/seguindo/" + id + "/seasons/" + temporada_position + "/visto/", TemporadaTodaAssistida(position));

                            myRef.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        databaseReference.child(user).child("seguindo")
                                                .child(id).child("seasons")
                                                .child(String.valueOf(temporada_position))
                                                .child("userEps").child(String.valueOf(position))
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot.exists()) {
                                                            UserEp userEp = dataSnapshot.getValue(UserEp.class);
                                                            ((TemporadaFoldinAdapter) recyclerView.getAdapter()).notificarMudanca(userEp, position);
                                                        } else {
                                                            Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(TemporadaActivity.this, R.string.marcado_assistido, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

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
                intent.putExtra(Constantes.INSTANCE.getUSER(), seasons);
                intent.putExtra(Constantes.INSTANCE.getSEGUINDO(), seguindo);
                startActivity(intent);

            }

            @Override
            public void onClickTemporadaNota(View view, EpisodesItem epsodio, int position, UserEp userEp) {
                Date date = null;

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                try {
                    date = sdf.parse(epsodio.getAirDate() != null ? epsodio.getAirDate() : null);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (UtilsApp.verificaLancamento(date) && mAuth.getCurrentUser() != null && seguindo) {


                    DatabaseReference databaseReference = FirebaseDatabase.getInstance()
                            .getReference("users")
                            .child(mAuth.getCurrentUser().getUid())
                            .child("seguindo")
                            .child(valueOf(serie_id))
                            .child("seasons")
                            .child(valueOf(temporada_position));


                    final Dialog alertDialog = new Dialog(TemporadaActivity.this);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.adialog_custom_rated);

                    Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                    Button nao_visto = (Button) alertDialog.findViewById(R.id.cancel_rated);

                    if (userEp != null) {
                        if (!userEp.isAssistido()) {
                            nao_visto.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        nao_visto.setVisibility(View.INVISIBLE);
                    }

                    TextView title = (TextView) alertDialog.findViewById(R.id.rating_title);
                    title.setText(epsodio.getName() != null ? epsodio.getName() : "");
                    final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                    ratingBar.setRating(userEp.getNota());
                    int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
                    int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                    alertDialog.getWindow().setLayout(width, height);
                    alertDialog.show();

                    nao_visto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (seguindo) {

                                Map<String, Object> childUpdates = new HashMap<String, Object>();

                                childUpdates.put("/userEps/" + position + "/assistido", false);
                                childUpdates.put("/visto/", false);
                                childUpdates.put("/userEps/" + position + "/nota", 0);
                                databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                    @Override
                                    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                        if (databaseError == null) {
                                            databaseReference
                                                    .child("userEps")
                                                    .child(String.valueOf(position))
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {

                                                            UserEp userEp = dataSnapshot
                                                                    .getValue(UserEp.class);
                                                            if (userEp != null) {
                                                                ((TemporadaFoldinAdapter) recyclerView.getAdapter()).notificarMudanca(userEp, position);
                                                            } else {
                                                                Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                                            }

                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        } else {
                                            Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                            }
                            alertDialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Map<String, Object> childUpdates = new HashMap<String, Object>();

                            childUpdates.put("/userEps" + "/" + position + "/assistido", true);
                            childUpdates.put("/visto", TemporadaTodaAssistida(position));
                            childUpdates.put("/userEps/" + position + "/nota", ratingBar.getRating());
                            databaseReference.updateChildren(childUpdates, new DatabaseReference.CompletionListener() {
                                @Override
                                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                    if (databaseError == null) {
                                        databaseReference
                                                .child("userEps")
                                                .child(String.valueOf(position))
                                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        UserEp userEp = dataSnapshot.getValue(UserEp.class);
                                                        if (userEp != null) {
                                                            ((TemporadaFoldinAdapter) recyclerView.getAdapter()).notificarMudanca(userEp, position);
                                                            setNotaIMDB(position, (int) ratingBar.getRating());
                                                        } else {
                                                            Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                                        }


                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                    } else {
                                        Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                            alertDialog.dismiss();
                        }
                    });

                }
            }
        };
    }

    private void setNotaIMDB(int position, int ratingBar) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FilmeService
                        .ratedTvshowEpsodioGuest(serie_id, seasons
                                .getSeasonNumber(), position, ratingBar, getApplicationContext());
            }
        }).start();
    }

    private boolean TemporadaTodaAssistida(int position) {

        for (UserEp userEp : seasons.getUserEps()) {
            if (!seasons.getUserEps().get(position).equals(userEp)) {
                if (!userEp.isAssistido()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isAssistidoAnteriores(int position) {

        for (int i = 0; i < position; i++) {
            if (!seasons.getUserEps().get(i).isAssistido()) {
                return true;
            }
        }
        return false;
    }


    private void setListener() {

        postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists() && seguindo) {
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
                            .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this,
                                    tvSeason, seasons, seguindo,
                                    onClickListener()));
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
        subscription.clear();
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


    private void getDados() {
        new Api(this).getTvSeasons(serie_id, temporada_id, temporada_position)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<TvSeasons>() {
                    @Override
                    public void onCompleted() {

                        if (tvSeason == null) {
                            return;
                        }

                        getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada);

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
                                                                .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this, tvSeason, seasons, seguindo, onClickListener()));
//                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
//                                                            tvSeason, seasons, seguindo,
//                                                            onClickListener()));
                                                    } else {
                                                        //   Log.d(TAG, "onDataChange " + "Não seguindo.");
                                                        seguindo = false;
                                                        recyclerView
                                                                .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this, tvSeason, null, seguindo, onClickListener()));
                                                        //.setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                                        //        tvSeason, seasons, seguindo,
                                                        //        onClickListener()));
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
                                    .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this,
                                            tvSeason, seasons, seguindo,
                                            onClickListener()));
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        Crashlytics.logException(e);
                        Toast.makeText(TemporadaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNext(TvSeasons tvSeasons) {
                        tvSeason = tvSeasons;
                    }
                });

    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            boolean idioma_padrao = false;
            try {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TemporadaActivity.this);
                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            } catch (Exception e) {
                Crashlytics.logException(e);
            }
            try {
                if (idioma_padrao) {
                  //  tvSeason = FilmeService.getTmdbTvSeasons()
                 //           .getSeason(serie_id, temporada_id, getLocale(), TmdbTvSeasons.SeasonMethod.credits);

                    return null;
                } else {
                //    tvSeason = FilmeService.getTmdbTvSeasons()
                //            .getSeason(serie_id, temporada_id, "en", TmdbTvSeasons.SeasonMethod.credits);
                    return null;
                }
            } catch (Exception e) {

                Crashlytics.logException(e);
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

            if (tvSeason == null) {
                return;
            }

            getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada);

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
                                                    .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this, tvSeason, seasons, seguindo, onClickListener()));
//                                                    .setAdapter(new TemporadaAdapter(TemporadaActivity.this,
//                                                            tvSeason, seasons, seguindo,
//                                                            onClickListener()));
                                        } else {
                                            //   Log.d(TAG, "onDataChange " + "Não seguindo.");
                                            seguindo = false;
                                            recyclerView
                                                    .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this, tvSeason, null, seguindo, onClickListener()));
                                            //.setAdapter(new TemporadaAdapter(TemporadaActivity.this,
                                            //        tvSeason, seasons, seguindo,
                                            //        onClickListener()));
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
                        .setAdapter(new TemporadaFoldinAdapter(TemporadaActivity.this,
                                tvSeason, seasons, seguindo,
                                onClickListener()));
            }

        }
    }
}
