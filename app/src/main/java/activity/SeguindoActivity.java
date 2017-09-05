package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import adapter.SeguindoAdapater;
import br.com.icaro.filme.R;
import domain.UserTvshow;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.tv.TvSeason;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Config;
import utils.UtilsApp;

import static utils.UtilsApp.setEp;
import static utils.UtilsApp.setUserTvShow;

/**
 * Created by icaro on 25/11/16.
 */
public class SeguindoActivity extends BaseActivity {

    private final String TAG = SeguindoActivity.class.getName();
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private ProgressBar progressBar;
    private LinearLayout linearLayout;
    private DatabaseReference seguindoDataBase;
    private ValueEventListener eventListener;
    private List<UserTvshow> userTvshows;

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

//        AdView adview = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                .build();
//        adview.loadAd(adRequest);

        if (UtilsApp.isNetWorkAvailable(this)) {

            iniciarFirebases();
            setEventListenerSeguindo();
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
                        if (UtilsApp.isNetWorkAvailable(getBaseContext())) {
                            //text_elenco_no_internet.setVisibility(View.GONE);
                            iniciarFirebases();
                            setEventListenerSeguindo();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("deprecation")
    private void setupViewPagerTabs() {

        viewPager.setOffscreenPageLimit(1);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(getResources().getColor(R.color.accent));
        viewPager.setAdapter(new SeguindoAdapater(SeguindoActivity.this, getSupportFragmentManager(),
                userTvshows));
    }

    private void setEventListenerSeguindo() {
        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userTvshows = new ArrayList<>();
                if (dataSnapshot.exists()) {

                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserTvshow userTvshow = snapshot.getValue(UserTvshow.class);
                        atualizarRealDate(userTvshow);
                        userTvshows.add(userTvshow);
                    }
                    setupViewPagerTabs();
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        seguindoDataBase.addListenerForSingleValueEvent(eventListener);
        //Chamando apenas uma vez, necessario? não poderia deixar o firebases atualizar?
    }

    private void atualizarRealDate(final UserTvshow userTvshowOld) {
        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    TmdbTvSeasons tvSeasons = new TmdbApi(Config.TMDB_API_KEY).getTvSeasons();
                    TvSeries series = new TmdbApi(Config.TMDB_API_KEY).getTvSeries()
                            .getSeries(userTvshowOld.getId(), "en", TmdbTV.TvMethod.external_ids);
                    if (userTvshowOld.getNumberOfEpisodes() < series.getNumberOfEpisodes()) {
                        UserTvshow userTvshow = setUserTvShow(series);

                        for (int i = 0; i < series.getSeasons().size(); i++) {
                            TvSeason tvS = series.getSeasons().get(i);
                            TvSeason tvSeason = tvSeasons.getSeason(series.getId(), tvS.getSeasonNumber(), "en", TmdbTvSeasons.SeasonMethod.external_ids); //?
                            userTvshow.getSeasons().get(i).setUserEps(setEp(tvSeason));
                            // Atualiza os eps em userTvShow
                        }
                        if (userTvshowOld.getSeasons() != null && userTvshow.getSeasons() != null) {
                            for (int i = 0; i < userTvshowOld.getSeasons().size(); i++) {
                                userTvshow.getSeasons().get(i).setId(userTvshowOld.getSeasons().get(i).getId());
                                userTvshow.getSeasons().get(i).setSeasonNumber(userTvshowOld.getSeasons().get(i).getSeasonNumber());
                                userTvshow.getSeasons().get(i).setVisto(userTvshowOld.getSeasons().get(i).isVisto());
                                //Atualiza somente os campos do temporada em userTvShow
                            }
                        }

                        for (int i = 0; i < userTvshowOld.getNumberOfSeasons(); i++) {
                            //Log.d(TAG, "Numero de eps - " + userTvshow.getSeasons().get(i).getUserEps().size());
                            if (userTvshow.getSeasons().get(i).getUserEps() != null && userTvshowOld.getSeasons().get(i).getUserEps() != null) {
                                if (userTvshow.getSeasons().get(i).getUserEps().size() > userTvshowOld.getSeasons().get(i).getUserEps().size())
                                    userTvshow.getSeasons().get(i).setVisto(false);
                                //  Se huver novos ep. coloca temporada com não 'vista'
                            }
                            if (userTvshowOld.getSeasons().get(i).getUserEps() != null)
                                for (int i1 = 0; i1 < userTvshowOld.getSeasons().get(i).getUserEps().size(); i1++) {
                                    if (i1 < userTvshowOld.getSeasons().get(i)
                                            .getUserEps().size() && i1 < userTvshow.getSeasons().get(i).getUserEps().size() )
                                        if (userTvshow.getSeasons().get(i) != null && userTvshowOld.getSeasons().get(i) != null)
                                        userTvshow.getSeasons().get(i).getUserEps().set(i1, userTvshowOld.getSeasons().get(i).getUserEps().get(i1));

                                    //coloca as informações antigas na nova versão dos dados.
                                }
                        }
                        seguindoDataBase
                                .child(String.valueOf(series.getId()))
                                .setValue(userTvshow)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    Toast.makeText(SeguindoActivity.this, R.string.season_updated, Toast.LENGTH_SHORT).show();
                                                }
                                            });
//
                                        }
                                    }
                                });
                    }
                }
            }).start();
        } catch (Exception e) {
            FirebaseCrash.report(e);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(SeguindoActivity.this, R.string.ops_seguir_novamente, Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (eventListener != null) {
            seguindoDataBase.removeEventListener(eventListener);
        }
    }

}
