package activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import adapter.TvShowAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

import static info.movito.themoviedbapi.TmdbTV.TvMethod.credits;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.external_ids;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.images;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.videos;


public class TvShowActivity extends BaseActivity {

    private static final String TAG = TvShowActivity.class.getName();
    int id_tvshow;
    String nome;
    int color_top;
    ViewPager viewPager;
    ImageView imageView;
    TvSeries series = null;
    CollapsingToolbarLayout layout;
    FloatingActionButton menu_item_favorite, menu_item_watchlist, menu_item_rated;
    FloatingActionMenu fab;
    FirebaseAnalytics firebaseAnalytics;
    private boolean addFavorite = true;
    private boolean addWatch = true;
    private boolean addRated = true;
    private boolean seguindo;
    private ValueEventListener valueEventWatch;
    private ValueEventListener valueEventRated;
    private ValueEventListener valueEventFavorite;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference myFavorite;
    private DatabaseReference myWatch;
    private DatabaseReference myRated;
    private FirebaseDatabase database;
    private float numero_rated;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvserie_activity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        setupNavDrawer();
        getExtras();
        layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        layout.setBackgroundColor(color_top);
        Log.d("color", "Cor do fab " + color_top);
        viewPager = (ViewPager) findViewById(R.id.viewPager_tvshow);
        menu_item_favorite = (FloatingActionButton) findViewById(R.id.menu_item_favorite);
        menu_item_watchlist = (FloatingActionButton) findViewById(R.id.menu_item_watchlist);
        menu_item_rated = (FloatingActionButton) findViewById(R.id.menu_item_rated);
        fab = (FloatingActionMenu) findViewById(R.id.fab_menu_filme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.img_top_tvshow);
        setColorFab(color_top);

        iniciarFirebases();


        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();
        } else {
            snack();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void setEventListenerWatch() {

        valueEventWatch = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(String.valueOf(id_tvshow)).exists()) {
                    addWatch = true;
                    Log.d(TAG, "False");
                    menu_item_watchlist.setLabelText(getResources().getString(R.string.remover_watch));
                } else {
                    addWatch = false;
                    Log.d(TAG, "True");
                    menu_item_watchlist.setLabelText(getResources().getString(R.string.adicionar_watch));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myWatch.addValueEventListener(valueEventWatch);

    }

    private void setEventListenerRated() {
        valueEventRated = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(String.valueOf(id_tvshow)).exists()) {
                    addRated = true;
                    Log.d(TAG, "False");
                    Log.d(TAG, "nota " + dataSnapshot.child(String.valueOf(id_tvshow)).child("nota"));
                    if (dataSnapshot.child(String.valueOf(id_tvshow)).child("nota").exists()) {
                        String nota = String.valueOf(dataSnapshot.child(String.valueOf(id_tvshow)).child("nota").getValue());
                        numero_rated = Float.parseFloat(nota);
                        menu_item_rated.setLabelText(getResources().getString(R.string.remover_rated));
                        if (numero_rated == 0) {
                            menu_item_rated.setLabelText(getResources().getString(R.string.adicionar_rated));
                        }
                    }

                } else {
                    addRated = false;
                    numero_rated = 0;
                    menu_item_rated.setLabelText(getResources().getString(R.string.adicionar_rated));
                    Log.d(TAG, "True");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myRated.addValueEventListener(valueEventRated);

    }

    private void setEventListenerFavorite() {
        valueEventFavorite = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(String.valueOf(id_tvshow)).exists()) {
                    addFavorite = true;
                    Log.d(TAG, "True");
                    menu_item_favorite.setLabelText(getResources().getString(R.string.remover_favorite));
                } else {
                    addFavorite = false;
                    Log.d(TAG, "False");
                    menu_item_favorite.setLabelText(getResources().getString(R.string.adicionar_favorite));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        myFavorite.addValueEventListener(valueEventFavorite);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myWatch.removeEventListener(valueEventWatch);
        myRated.removeEventListener(valueEventRated);
        myFavorite.removeEventListener(valueEventFavorite);
    }

    private void iniciarFirebases() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        myWatch = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("watch")
                .child("tvshow");

        myFavorite = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("favorites")
                .child("tvshow");

        myRated = database.getReference("users").child(mAuth.getCurrentUser()
                .getUid()).child("rated")
                .child("tvshow");

    }

    private void getExtras() {
        if (getIntent().getAction() == null) {
            nome = getIntent().getStringExtra(Constantes.NOME_TVSHOW);// usado????????
            //nome = "BBT";
            color_top = getIntent().getIntExtra(Constantes.COLOR_TOP, R.color.colorFAB);
            //color_top = -13565;
            id_tvshow = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
            //id_tvshow = 1418;
        } else {
            nome = getIntent().getStringExtra(Constantes.NOME_TVSHOW);// usado????????
            //nome = "BBT";
            color_top = Integer.parseInt(getIntent().getStringExtra(Constantes.COLOR_TOP));
            //color_top = -13565;
            id_tvshow = Integer.parseInt(getIntent().getStringExtra(Constantes.TVSHOW_ID));
            //id_tvshow = 1418;
        }
    }

    protected void snack() {
        Snackbar.make(viewPager, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_share, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Procura Filme");
        searchView.setEnabled(false);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.share) {
            File file = null;
            if (series != null) {
                file = salvaImagemMemoriaCache(this, series.getPosterPath());

                if (file != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    //  intent.putExtra(Intent.EXTRA_SUBJECT, series.getOverview());
                    final String appPackageName = getPackageName();
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_TEXT, series.getName() + "  -  " + "https://play.google.com/store/apps/details?id=" + appPackageName);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.compartilhar_tvshow)));
                } else {
                    Toast.makeText(this, getResources().getString(R.string.erro_na_gravacao_imagem),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.erro_ainda_sem_imagem), Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void setCoordinator() {
        layout.setTitle(series.getName());
    }

    private View.OnClickListener addOrRemoveWatch() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PropertyValuesHolder anim1 = PropertyValuesHolder.ofFloat("scaleX", 1f, 0f);
                PropertyValuesHolder anim2 = PropertyValuesHolder.ofFloat("scaley", 1f, 0f);
                PropertyValuesHolder anim3 = PropertyValuesHolder.ofFloat("scaleX", 0.5f, 1f);
                PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaley", 0.5f, 1f);
                ObjectAnimator animator = ObjectAnimator
                        .ofPropertyValuesHolder(menu_item_watchlist, anim1, anim2, anim3, anim4);
                animator.setDuration(1700);
                animator.start();

                if (addWatch) {
                    Log.d(TAG, "Apagou Watch");
                    myWatch.child(String.valueOf(series.getId())).setValue(null)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    Toast.makeText(TvShowActivity.this, getString(R.string.tvshow_watch_remove), Toast.LENGTH_SHORT)
                                            .show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_remove));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                }
                            });

                } else {
                    Log.d(TAG, "Gravou Watch");
                    myWatch.child(String.valueOf(series.getId())).setValue(series.getExternalIds())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(TvShowActivity.this, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                            .show();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_add_watchlist));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                }
                            });
                }
                fab.close(true);
            }
        };
    }

    private View.OnClickListener addOrRemoveFavorite() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PropertyValuesHolder anim1 = PropertyValuesHolder.ofFloat("scaleX", 1f, 0.2f);
                PropertyValuesHolder anim2 = PropertyValuesHolder.ofFloat("scaley", 1f, 0.2f);
                PropertyValuesHolder anim3 = PropertyValuesHolder.ofFloat("scaleX", 0f, 1f);
                PropertyValuesHolder anim4 = PropertyValuesHolder.ofFloat("scaley", 0f, 1f);
                ObjectAnimator animator = ObjectAnimator
                        .ofPropertyValuesHolder(menu_item_favorite, anim1, anim2, anim3, anim4);
                animator.setDuration(1700);
                animator.start();

                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(series.getFirstAirDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!UtilsFilme.verificaLancamento(date)) {
                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(TvShowActivity.this);
                    Toast.makeText(TvShowActivity.this, R.string.tvshow_nao_lancado, Toast.LENGTH_SHORT).show();
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Favorite - Tvshow ainda não foi lançado.");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else {

                    if (addFavorite) {
                        Log.d(TAG, "Apagou Favorite");
                        myFavorite.child(String.valueOf(id_tvshow)).setValue(null)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.tvshow_remove_favorite), Toast.LENGTH_SHORT).show();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.tvshow_remove_favorite));
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                    }
                                });

                    } else {
                        Log.d(TAG, "Gravou Favorite");
                        myFavorite.child(String.valueOf(id_tvshow)).setValue(series.getExternalIds())
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.tvshow_add_favorite), Toast.LENGTH_SHORT)
                                                .show();
                                        Bundle bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.tvshow_add_favorite));
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                                        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                    }
                                });
                    }

                    fab.close(true);
                }
            }
        };
    }

    private View.OnClickListener RatedFilme() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(series.getFirstAirDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!UtilsFilme.verificaLancamento(date)) {
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Tentativa de Rated fora da data de lançamento");
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    Toast.makeText(TvShowActivity.this, getString(R.string.tvshow_nao_lancado), Toast.LENGTH_SHORT).show();

                } else {

                    final Dialog alertDialog = new Dialog(TvShowActivity.this);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.adialog_custom_rated);

                    Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                    Button no = (Button) alertDialog.findViewById(R.id.cancel_rated);


                    TextView title = (TextView) alertDialog.findViewById(R.id.rating_title);
                    title.setText(series.getName());
                    final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                    ratingBar.setRating(numero_rated);
                    int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
                    int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                    alertDialog.getWindow().setLayout(width, height);

                    if (addRated) {
                        no.setVisibility(View.VISIBLE);
                    } else {
                        no.setVisibility(View.GONE);
                    }

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Apagou Rated");
                            myRated.child(String.valueOf(id_tvshow)).setValue(null)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(TvShowActivity.this,
                                                    getResources().getText(R.string.tvshow_remove_rated), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            alertDialog.dismiss();
                            fab.close(true);
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Adialog Rated");

                            if (UtilsFilme.isNetWorkAvailable(TvShowActivity.this)) {

                                Log.d(TAG, "Gravou Rated");

                                myRated.child(String.valueOf(id_tvshow)).setValue(series.getExternalIds());

                                myRated.child(String.valueOf(id_tvshow)).child("nota").setValue(ratingBar.getRating())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(TvShowActivity.this,
                                                        getString(R.string.tvshow_rated), Toast.LENGTH_SHORT)
                                                        .show();
                                                Bundle bundle = new Bundle();
                                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,
                                                        getString(R.string.tvshow_rated));
                                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                            }
                                        });
                            }
                            alertDialog.dismiss();
                            fab.close(true);
                        }
                    });
                    alertDialog.show();
                }
            }
        };
    }

    private void setupViewPagerTabs() {

        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new TvShowAdapter(this, getSupportFragmentManager(), series, color_top, seguindo));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(color_top);

    }

    private void setImageTop() {

        Picasso.with(TvShowActivity.this)
                .load(UtilsFilme.getBaseUrlImagem(5) + series.getBackdropPath())
                .error(R.drawable.top_empty)
                .into(imageView);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator animator = ObjectAnimator.ofFloat(imageView, "x", -100, 0)
                .setDuration(1000);
        animatorSet.playTogether(animator);
        animatorSet.start();
    }

    private void setColorFab(int color) {
        fab.setMenuButtonColorNormal(color);
        menu_item_favorite.setColorNormal(color);
        menu_item_watchlist.setColorNormal(color);
        menu_item_rated.setColorNormal(color);
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("TvShow Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TvShowActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);

            if (idioma_padrao) {
                TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
                Log.d("FilmeActivity", "true ");
                series = tmdbTv
                        .getSeries(id_tvshow, Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry()
                                        //.toLanguageTag() não funciona na API 14
                                        + ",en,null"
                                , images, credits, videos, external_ids);
                Log.d("FilmeActivity", Locale.getDefault().getLanguage());
                series.getVideos().addAll(tmdbTv.getSeries(id_tvshow, null, videos).getVideos());
                series.getImages().setPosters(tmdbTv.getSeries(id_tvshow, null, images).getImages().getPosters());

            } else {
                Log.d("FilmeActivity", "false ");
                series = FilmeService.getTmdbTvShow()
                        .getSeries(id_tvshow, null, images, credits, videos, external_ids);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (mAuth.getCurrentUser().getUid() != null && series != null) {

                myRef.child(mAuth.getCurrentUser().getUid()).child("seguindo").child(String.valueOf(series.getId()))
                        .addListenerForSingleValueEvent(
                                new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        // Get user value
                                        if (dataSnapshot.exists()) {
                                            seguindo = true;
                                        } else {

                                            Log.d(TAG, "onDataChange " + "Não seguindo.");
                                        }
                                        setupViewPagerTabs();
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {
                                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                                    }
                                });
            } else {
                seguindo = false;
                setupViewPagerTabs();
            }

            setCoordinator();
            setImageTop();
            setEventListenerWatch();
            setEventListenerFavorite();
            setEventListenerRated();

            if (FilmeApplication.getInstance().isLogado()) { // Arrumar
                Log.d("FAB", "FAB " + color_top);
                Date date = null;
                fab.setAlpha(1);
                if (series.getFirstAirDate() != null) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        date = sdf.parse(series.getFirstAirDate());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    if (UtilsFilme.verificaLancamento(date)) {
                        menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
                        menu_item_rated.setOnClickListener(RatedFilme());
                    }
                    menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
                } else {
                    menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
                    menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
                    menu_item_rated.setOnClickListener(RatedFilme());
                }
            } else {
                fab.setAlpha(0);
            }
        }
    }


}
