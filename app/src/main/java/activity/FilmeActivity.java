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
import com.google.firebase.analytics.FirebaseAnalytics;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.FilmeInfoFragment;
import fragment.ImagemTopFilmeScrollFragment;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
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

    ViewPager viewPager;
    int color_fundo;
    FloatingActionButton menu_item_favorite, menu_item_watchlist, menu_item_rated;
    FloatingActionMenu fab;
    private int id_filme;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressBar progressBar;
    private MovieDb movieDb;
    private boolean addFavorite = true;
    private boolean addWatch = true; // Retirar quando metodo de saber, estiver pronto
    private MovieResultsPage similarMovies;
    private FirebaseAnalytics mFirebaseAnalytics;
    private Bundle bundle;

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
        menu_item_favorite = (FloatingActionButton) findViewById(R.id.menu_item_favorite);
        menu_item_watchlist = (FloatingActionButton) findViewById(R.id.menu_item_watchlist);
        menu_item_rated = (FloatingActionButton) findViewById(R.id.menu_item_rated);
        fab = (FloatingActionMenu) findViewById(R.id.fab_menu_filme);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        viewPager = (ViewPager) findViewById(R.id.top_img_viewpager);
        viewPager.setBackgroundColor(color_fundo);
        viewPager.setOffscreenPageLimit(3);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ViewPager");
                bundle.putInt(FirebaseAnalytics.Param.TRANSACTION_ID, viewPager.getCurrentItem());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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

    private void getExtras() {
        if (getIntent().getAction() == null) {
            id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
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
        if (item.getItemId() == R.id.share) {
            File file = salvaImagemMemoriaCache(getContext(), movieDb.getPosterPath());
            if (file != null) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_TEXT, movieDb.getTitle());
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                startActivity(Intent.createChooser(intent, getResources().getString(R.string.compartilhar_filme)));
                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_MainActivity:menu_drav_home");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            } else {
                Toast.makeText(getContext(), getResources().getString(R.string.erro_na_gravacao_imagem), Toast.LENGTH_SHORT).show();
            }
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
                    final Dialog alertDialog = new Dialog(getContext());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.adialog_custom_rated);

                    Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                    TextView title = (TextView) alertDialog.findViewById(R.id.rating_title);
                    title.setText(movieDb.getTitle());
                    final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                    int width = getResources().getDimensionPixelSize(R.dimen.popup_width); //Criar os Dimen do layout do login - 300dp - 300dp ??
                    int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                    alertDialog.getWindow().setLayout(width, height);

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Adialog Rated");
                            final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                                    android.R.style.Theme_Material_Dialog);
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Salvando...");
                            progressDialog.show();

                            new Thread() {
                                boolean status = false;

                                @Override
                                public void run() {
                                    if (!isDestroyed()) {
                                        if (UtilsFilme.isNetWorkAvailable(getContext())) {
                                            status = FilmeService.setRatedMovie(id_filme, ratingBar.getRating());
                                            try {
                                                Thread.sleep(1000);
                                                if (!isDestroyed()) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Log.d("Status", "" + status);
                                                            if (status) {
                                                                Toast.makeText(getContext(), getResources().getString(R.string.filme_rated), Toast.LENGTH_SHORT)
                                                                        .show();
                                                                bundle = new Bundle();
                                                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getResources()
                                                                        .getString(R.string.filme_rated));
                                                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                                fab.close(true);
                                                            } else {
                                                                Toast.makeText(getContext(), getString(R.string.falha_rated), Toast.LENGTH_SHORT)
                                                                        .show();
                                                                bundle = new Bundle();
                                                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getResources()
                                                                        .getString(R.string.falha_rated));
                                                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                                fab.close(true);
                                                            }
                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }.start();

                            alertDialog.dismiss();
                        }

                    });
                    alertDialog.show();
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

//    public void setAnimacao() {
//        AnimatorSet animatorSet = new AnimatorSet();
//        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(img_star, "alpha", 0, 1)
//                .setDuration(2000);
//        ObjectAnimator alphaMedia = ObjectAnimator.ofFloat(voto_media, "alpha", 0, 1)
//                .setDuration(2300);
//        ObjectAnimator alphaBuget = ObjectAnimator.ofFloat(img_budget, "alpha", 0, 1)
//                .setDuration(2500);
//        ObjectAnimator alphaReviews = ObjectAnimator.ofFloat(icon_reviews, "alpha", 0, 1)
//                .setDuration(2800);
//        ObjectAnimator alphaSite = ObjectAnimator.ofFloat(icon_site, "alpha", 0, 1)
//                .setDuration(3000);
//        ObjectAnimator alphaCollecton = ObjectAnimator.ofFloat(icon_collection, "alpha", 0, 1)
//                .setDuration(3300);
//        animatorSet.playTogether(alphaStar, alphaBuget, alphaMedia, alphaReviews, alphaSite, alphaCollecton);
//        animatorSet.playSequentially();
//        animatorSet.start();
//    }


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
                    final ResponseStatus[] status = new ResponseStatus[1];
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (!isDestroyed()) {
                                status[0] = FilmeService.addOrRemoverFavorite(id_filme, addFavorite, TmdbAccount.MediaType.MOVIE);
                                if (!isDestroyed()) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            switch (status[0].getStatusCode()) {
                                                case 1: {
                                                    Toast.makeText(getContext(), getString(R.string.filme_add_favorite), Toast.LENGTH_SHORT)
                                                            .show();
                                                    bundle = new Bundle();
                                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_add_favorite));
                                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                    addFavorite = !addFavorite;
                                                    fab.close(true);
                                                    break;
                                                }
                                                case 12: {
                                                    Toast.makeText(getContext(), getString(R.string.filme_re_add), Toast.LENGTH_SHORT).show();
                                                    bundle = new Bundle();
                                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_re_add));
                                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                    addFavorite = !addFavorite;
                                                    fab.close(true);
                                                    break;
                                                }
                                                case 13: {
                                                    Toast.makeText(getContext(), getString(R.string.filme_remove_favorite), Toast.LENGTH_SHORT).show();
                                                    bundle = new Bundle();
                                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_remove_favorite));
                                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                    addFavorite = !addFavorite;
                                                    fab.close(true);
                                                }
                                                default: {
                                                    Toast.makeText(getContext(), getString(R.string.erro_add_or_remove), Toast.LENGTH_SHORT).show();
                                                    bundle = new Bundle();
                                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.erro_add_or_remove));
                                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                                    fab.close(true);
                                                }
                                            }
                                        }
                                    });
                                }
                            }
                        }
                    }).start();
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

                final ResponseStatus[] status = new ResponseStatus[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        status[0] = FilmeService.addOrRemoverWatchList(id_filme, addWatch, TmdbAccount.MediaType.MOVIE);
                        if (!isDestroyed()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    switch (status[0].getStatusCode()) {
                                        case 1: {
                                            Toast.makeText(getContext(), getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                                    .show();
                                            bundle = new Bundle();
                                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_add_watchlist));
                                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                            addWatch = !addWatch;
                                            fab.close(true);
                                            break;
                                        }
                                        case 12: {
                                            Toast.makeText(getContext(), getString(R.string.filme_re_add), Toast.LENGTH_SHORT).show();
                                            bundle = new Bundle();
                                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_re_add));
                                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                            addWatch = !addWatch;
                                            fab.close(true);
                                            break;
                                        }
                                        case 13: {
                                            Toast.makeText(getContext(), getString(R.string.filme_remove_watchlist), Toast.LENGTH_SHORT).show();
                                            bundle = new Bundle();
                                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.filme_remove_watchlist));
                                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                            addWatch = !addWatch;
                                            fab.close(true);
                                        }
                                        default: {
                                            Toast.makeText(getContext(), getString(R.string.erro_add_or_remove), Toast.LENGTH_SHORT).show();
                                            bundle = new Bundle();
                                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, getString(R.string.erro_add_or_remove));
                                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                            fab.close(true);
                                        }
                                    }
                                }
                            });
                        }
                    }
                }).start();
            }
        };
    }

    private Context getContext() {
        return this;
    }

    private void setTitle(String title) {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    private void setFragmentInfo() {
        Log.d("FilmeActivity", Locale.getDefault().getLanguage());
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
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("FilmeActivity", "Filme ID - " + id_filme);
//
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(FilmeActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            if (idioma_padrao) {
                movieDb = movies.getMovie(id_filme, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
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

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            setTitle(movieDb.getTitle());
            viewPager.setAdapter(new ImagemTopFragment(getSupportFragmentManager()));
            progressBar.setVisibility(View.INVISIBLE);
            setFragmentInfo();

            if (true){//FilmeApplication.getInstance().isLogado()) { // Arrumar
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