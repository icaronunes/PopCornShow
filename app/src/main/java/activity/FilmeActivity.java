package activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Dialog;
import android.app.ProgressDialog;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.icaro.filme.R;
import domian.FilmeDB;
import domian.FilmeService;
import fragment.FilmeInfoFragment;
import fragment.ImagemTopFilmeScrollFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.UtilsFilme;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.alternative_titles;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.credits;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.releases;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.similar;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.videos;


public class FilmeActivity extends BaseActivity {

    private static final String TAG = FilmeActivity.class.getName();

    ViewPager viewPager;
    int color_fundo;
    FloatingActionButton menu_item_favorite, menu_item_watchlist, menu_item_rated;
    FloatingActionMenu fab;
    private int id_filme;
    private ProgressBar progressBar;
    private MovieDb movieDb = null;
    private boolean addFavorite = true;
    private boolean addWatch = true;
    private boolean addRated = true;
    private MovieResultsPage similarMovies;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle bundle;
    private FirebaseAuth mAuth;
    private FirebaseDatabase database;

    private DatabaseReference myWatch;
    private DatabaseReference myFavorite;
    private DatabaseReference myRated;

    private ValueEventListener valueEventWatch;
    private ValueEventListener valueEventRated;
    private ValueEventListener valueEventFavorite;

    private float numero_rated;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(" ");
        getExtras();
        Log.d("color", "Cor do fab " + color_fundo);
        menu_item_favorite = (FloatingActionButton) findViewById(R.id.menu_item_favorite);
        menu_item_watchlist = (FloatingActionButton) findViewById(R.id.menu_item_watchlist);
        menu_item_rated = (FloatingActionButton) findViewById(R.id.menu_item_rated);
        fab = (FloatingActionMenu) findViewById(R.id.fab_menu_filme);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        viewPager = (ViewPager) findViewById(R.id.top_img_viewpager);
        viewPager.setBackgroundColor(color_fundo);
        viewPager.setOffscreenPageLimit(3);

        iniciarFirebases();

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ViewPager");
                bundle.putInt(FirebaseAnalytics.Param.TRANSACTION_ID, viewPager.getCurrentItem());
                FirebaseAnalytics.getInstance(FilmeActivity.this).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        if (UtilsFilme.isNetWorkAvailable(this)) {
            new TMDVAsync().execute();
        } else {
            snack();
        }
    }

    private void setEventListenerWatch() {

        valueEventWatch = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child(String.valueOf(id_filme)).exists()) {
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
                if (dataSnapshot.child(String.valueOf(id_filme)).exists()) {
                    addRated = true;
                    Log.d(TAG, "False");
                    Log.d(TAG, "nota " + dataSnapshot.child(String.valueOf(id_filme)).child("nota"));
                    if (dataSnapshot.child(String.valueOf(id_filme)).child("nota").exists()) {
                        String nota = String.valueOf(dataSnapshot.child(String.valueOf(id_filme)).child("nota").getValue());
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
                if (dataSnapshot.child(String.valueOf(id_filme)).exists()) {
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

    private void iniciarFirebases() {

        mAuth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        if (mAuth.getCurrentUser() != null) {

            myWatch = database.getReference("users").child(mAuth.getCurrentUser()
                    .getUid()).child("watch")
                    .child("movie");

            myFavorite = database.getReference("users").child(mAuth.getCurrentUser()
                    .getUid()).child("favorites")
                    .child("movie");

            myRated = database.getReference("users").child(mAuth.getCurrentUser()
                    .getUid()).child("rated")
                    .child("movie");
        }
    }

    private void getExtras() {
        if (getIntent().getAction() == null) {
            id_filme = (int) getIntent().getIntExtra(Constantes.FILME_ID, 0);
            color_fundo = getIntent().getIntExtra(Constantes.COLOR_TOP, R.color.transparent);
        } else {
            id_filme = Integer.parseInt(getIntent().getStringExtra(Constantes.FILME_ID));
            color_fundo = Integer.parseInt(getIntent().getStringExtra(Constantes.COLOR_TOP));

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
        if (movieDb != null) {
            if (item.getItemId() == R.id.share) {
                File file = salvaImagemMemoriaCache(FilmeActivity.this, movieDb.getPosterPath());
                if (file != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    final String appPackageName = FilmeActivity.this.getPackageName();
                    intent.putExtra(Intent.EXTRA_TEXT, movieDb.getTitle() + "  -  " + "https://play.google.com/store/apps/details?id=" + appPackageName);
                    intent.setType("image/*");
                    intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                    startActivity(Intent.createChooser(intent, getResources().getString(R.string.compartilhar_filme)));

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_MainActivity:menu_drav_home");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                } else {
                    Toast.makeText(FilmeActivity.this, getResources().getString(R.string.erro_na_gravacao_imagem), Toast.LENGTH_SHORT).show();
                }
            }
        } else {
            Toast.makeText(FilmeActivity.this, getResources().getString(R.string.erro_ainda_sem_imagem), Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    public View.OnClickListener RatedFilme() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(movieDb.getReleaseDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!UtilsFilme.verificaLancamento(date)) {
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Tentativa de Rated fora da data de lançamento");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    Toast.makeText(FilmeActivity.this, getString(R.string.filme_nao_lancado), Toast.LENGTH_SHORT).show();
                } else {

                    final Dialog alertDialog = new Dialog(FilmeActivity.this);
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.adialog_custom_rated);

                    Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                    Button no = (Button) alertDialog.findViewById(R.id.cancel_rated);
                    TextView title = (TextView) alertDialog.findViewById(R.id.rating_title);
                    title.setText(movieDb.getTitle());
                    final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                    ratingBar.setRating(numero_rated);
                    int width = getResources().getDimensionPixelSize(R.dimen.popup_width); //Criar os Dimen do layout do login - 300dp - 300dp ??
                    int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                    alertDialog.getWindow().setLayout(width, height);
                    alertDialog.show();

                    if (addRated) {
                        no.setVisibility(View.VISIBLE);
                    } else {
                        no.setVisibility(View.GONE);
                    }

                    no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Apagou Rated");
                            myRated.child(String.valueOf(id_filme)).setValue(null)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(FilmeActivity.this,
                                                    getResources().getText(R.string.remover_rated), Toast.LENGTH_SHORT).show();
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

                            final ProgressDialog progressDialog = new ProgressDialog(FilmeActivity.this,
                                    android.R.style.Theme_Material_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage(getResources().getString(R.string.salvando));
                            progressDialog.show();

                            if (UtilsFilme.isNetWorkAvailable(FilmeActivity.this)) {

                                if (ratingBar.getRating() == 0) {
                                    progressDialog.dismiss();
                                    alertDialog.dismiss();
                                    return;
                                }

                                FilmeDB filmeDB = new FilmeDB();
                                filmeDB.setId(movieDb.getId());
                                filmeDB.setIdImdb(movieDb.getImdbID());
                                filmeDB.setTitle(movieDb.getTitle());
                                filmeDB.setNota((int) ratingBar.getRating());
                                filmeDB.setPoster(movieDb.getPosterPath());

                                myRated.child(String.valueOf(id_filme)).setValue(filmeDB)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(FilmeActivity.this, getResources().getString(R.string.filme_rated), Toast.LENGTH_SHORT)
                                                        .show();
                                                bundle = new Bundle();
                                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getResources()
                                                        .getString(R.string.filme_rated));
                                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                fab.close(true);
                                            }
                                        });
                            }
                            progressDialog.dismiss();
                            alertDialog.dismiss();
                        }
                    });

                }
            }
        };
    }

    private void setColorFab(int color) {
        fab.setMenuButtonColorNormal(color);
        menu_item_favorite.setColorNormal(color);
        menu_item_watchlist.setColorNormal(color);
        menu_item_rated.setColorNormal(color);
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
                animator.setDuration(1600);
                animator.start();


                Date date = null;
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = sdf.parse(movieDb.getReleaseDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (!UtilsFilme.verificaLancamento(date)) {
                    Toast.makeText(FilmeActivity.this, R.string.filme_nao_lancado, Toast.LENGTH_SHORT).show();
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Favorite - Filme ainda não foi lançado.");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else {

                    if (addFavorite) {
                        Log.d(TAG, "Apagou Favorite");
                        myFavorite.child(String.valueOf(id_filme)).setValue(null)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FilmeActivity.this, getString(R.string.filme_remove_favorite), Toast.LENGTH_SHORT).show();
                                        bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_add_favorite));
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                        fab.close(true);
                                    }
                                });
                    } else {

                        FilmeDB filmeDB = new FilmeDB();
                        filmeDB.setId(movieDb.getId());
                        filmeDB.setIdImdb(movieDb.getImdbID());
                        filmeDB.setTitle(movieDb.getTitle());
                        filmeDB.setPoster(movieDb.getPosterPath());

                        myFavorite.child(String.valueOf(id_filme)).setValue(filmeDB)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(FilmeActivity.this, getString(R.string.filme_add_favorite), Toast.LENGTH_SHORT)
                                                .show();
                                        bundle = new Bundle();
                                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_remove_favorite));
                                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                        fab.close(true);
                                    }
                                });
                    }
                }
            }
        };
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
                animator.setDuration(1650);
                animator.start();

                if (addWatch) {

                    myWatch.child(String.valueOf(id_filme)).setValue(null)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(FilmeActivity.this, getString(R.string.filme_remove), Toast.LENGTH_SHORT).show();
                                    bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_remove));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                    fab.close(true);
                                }
                            });


                } else {

                    FilmeDB filmeDB = new FilmeDB();
                    filmeDB.setIdImdb(movieDb.getImdbID());
                    filmeDB.setId(movieDb.getId());
                    filmeDB.setTitle(movieDb.getTitle());
                    filmeDB.setPoster(movieDb.getPosterPath());

                    myWatch.child(String.valueOf(id_filme)).setValue(filmeDB)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(FilmeActivity.this, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                            .show();
                                    bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_add_watchlist));
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                    fab.close(true);
                                }
                            });

                }
            }
        };
    }

    private void setTitle(String title) {
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    private void setFragmentInfo() {

        FilmeInfoFragment filmeFrag = new FilmeInfoFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.FILME, movieDb);
        bundle.putSerializable(Constantes.SIMILARES, similarMovies);
        filmeFrag.setArguments(bundle);

        if (!isFinishing() && !isDestroyed()) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.filme_container, filmeFrag, null)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (valueEventWatch != null) {
            myWatch.removeEventListener(valueEventWatch);
        }
        if (valueEventRated != null) {
            myRated.removeEventListener(valueEventRated);
        }
        if (valueEventFavorite != null) {
            myFavorite.removeEventListener(valueEventFavorite);
        }
    }

    private class ImagemTopFragment extends FragmentPagerAdapter {

        public ImagemTopFragment(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (movieDb.getImages(ArtworkType.BACKDROP) != null) {
                if (position == 0) {
                    return new ImagemTopFilmeScrollFragment().newInstance(movieDb.getBackdropPath());
                }
                Log.d("FilmeActivity", "getItem: ->  " + movieDb.getImages(ArtworkType.BACKDROP).get(position).getFilePath());
                return new ImagemTopFilmeScrollFragment().newInstance(movieDb.getImages(ArtworkType.BACKDROP).get(position).getFilePath());
            }
            return null;
        }


        @Override
        public int getCount() {
            if (movieDb.getImages(ArtworkType.BACKDROP) != null) {

                int tamanho = movieDb.getImages(ArtworkType.BACKDROP).size();
                Log.d("FilmeActivity", "getCount: ->  " + tamanho);
                return tamanho > 0 ? tamanho : 1;
            }
            return 0;
        }
    }

    private class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {//
            if (UtilsFilme.isNetWorkAvailable(FilmeActivity.this)) {
                TmdbMovies movies = FilmeService.getTmdbMovies();
                Log.d("FilmeActivity", "Filme ID - " + id_filme);

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FilmeActivity.this);
                boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
                if (idioma_padrao) {
                    movieDb = movies.getMovie(id_filme, getLocale()
                                    //.toLanguageTag() não funciona na API 14
                                    + ",en,null"
                            , credits, releases, videos, reviews, similar, alternative_titles, images);
                    movieDb.getVideos().addAll(movies.getMovie(id_filme, "en", videos).getVideos());
                    movieDb.getReviews().addAll(movies.getMovie(id_filme, "en", reviews).getReviews());

                } else {
                    Log.d("FilmeActivity", "False - " + id_filme);
                    movieDb = movies.getMovie(id_filme, "en,null"
                            , credits, releases, videos, reviews, similar, alternative_titles, images);
                }
                similarMovies = movies.getSimilarMovies(movieDb.getId(), null, 1);
                return movieDb;
            }
            return null;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            if (movieDb != null) {
                setTitle(movieDb.getTitle());
                viewPager.setAdapter(new ImagemTopFragment(getSupportFragmentManager()));
                progressBar.setVisibility(View.INVISIBLE);

                setFragmentInfo();

                if (mAuth.getCurrentUser() != null) { // Arrumar

                    setEventListenerFavorite();
                    setEventListenerRated();
                    setEventListenerWatch();

                    Log.d("FAB", "FAB " + color_fundo);
                    fab.setAlpha(1);
                    setColorFab(color_fundo);
                    menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
                    menu_item_rated.setOnClickListener(RatedFilme());
                    menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
                } else {
                    fab.setAlpha(0);
                }

            }
        }
    }

}