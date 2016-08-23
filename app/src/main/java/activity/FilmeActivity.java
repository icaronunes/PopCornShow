package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.FilmeBottonFragment;
import fragment.ImagemTopScrollFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;
import utils.Prefs;
import utils.UtilsFilme;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;

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
    private boolean addWatch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(" "); // Recebendo as vezes titulo em ingles. Necessario esperar nova busca. Resolver!
        color_fundo = getIntent().getIntExtra(Constantes.COLOR_TOP, R.color.transparent);
        menu_item_favorite = (FloatingActionButton) findViewById(R.id.menu_item_favorite);
        menu_item_watchlist = (FloatingActionButton) findViewById(R.id.menu_item_watchlist);
        menu_item_rated = (FloatingActionButton) findViewById(R.id.menu_item_rated);
        fab = (FloatingActionMenu) findViewById(R.id.fab_menu_filme);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        viewPager = (ViewPager) findViewById(R.id.top_img_viewpager);
        viewPager.setBackgroundColor(color_fundo);

        if (savedInstanceState == null) {
            FilmeBottonFragment filmeFrag = new FilmeBottonFragment();
            Bundle bundle = new Bundle(); //Tentar pegar nome que esta no bundle / Posso pasar o bundle direto?
            bundle.putInt(Constantes.FILME_ID, getIntent().getExtras().getInt(Constantes.FILME_ID));
            filmeFrag.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.filme_container, filmeFrag, null)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)// ????????
                    .commit();
        }

        if (FilmeApplication.getInstance().isLogado()) {
            Log.d("FAB", "FAB");
            setColorFab(color_fundo);
            menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
            menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
            menu_item_rated.setOnClickListener(RatedFilme());
        } else {
            fab.setAlpha(0);
        }

    }

    private View.OnClickListener RatedFilme() {
        return new View.OnClickListener() {
            @Override public void onClick(View view) {
                final Dialog alertDialog = new Dialog(getContext());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.adialog_custom_rated);

                Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
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
                                if (UtilsFilme.isNetWorkAvailable(getContext())) {
                                    status = FilmeService.setRatedMovie(id_filme, ratingBar.getRating() * 2);
                                    try {
                                        Thread.sleep(1150);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("Status", "" + status);
                                                if (status) {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.filme_rated), Toast.LENGTH_SHORT)
                                                            .show();
                                                    fab.close(true);
                                                } else {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.falha_rated), Toast.LENGTH_SHORT)
                                                            .show();
                                                    fab.close(true);
                                                }
                                                progressDialog.dismiss();
                                            }
                                        });
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }.start();

                        alertDialog.dismiss();
                    }

                });
                alertDialog.show();
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
                final ResponseStatus[] status = new ResponseStatus[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String user = Prefs.getString(getContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
                        String pass = Prefs.getString(getContext(), Prefs.PASS, Prefs.LOGIN_PASS);
                        status[0] = FilmeService.addOrRemoverFavorite(user, pass, id_filme, addFavorite);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (status[0].getStatusCode()) {
                                    case 1: {
                                        Toast.makeText(getContext(), getString(R.string.filme_add_favorite), Toast.LENGTH_SHORT)
                                                .show();
                                        addFavorite = !addFavorite;
                                        fab.close(true);
                                        break;
                                    }
                                    case 12: {
                                        Toast.makeText(getContext(), getString(R.string.filme_re_add), Toast.LENGTH_SHORT).show();
                                        addFavorite = !addFavorite;
                                        fab.close(true);
                                        fab.close(true);
                                        break;
                                    }
                                    case 13: {
                                        Toast.makeText(getContext(), getString(R.string.filme_remove_favorite), Toast.LENGTH_SHORT).show();
                                        addFavorite = !addFavorite;
                                        fab.close(true);
                                    }
                                    default: {
                                        Toast.makeText(getContext(), getString(R.string.erro_watch), Toast.LENGTH_SHORT).show();
                                        fab.close(true);
                                    }
                                }
                            }
                        });
                    }
                }).start();
            }
        };
    }

    private View.OnClickListener addOrRemoveWatch() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ResponseStatus[] status = new ResponseStatus[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String user = Prefs.getString(getContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
                        String pass = Prefs.getString(getContext(), Prefs.PASS, Prefs.LOGIN_PASS);
                        status[0] = FilmeService.addOrRemoverWatchList(user, pass, id_filme, addWatch);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (status[0].getStatusCode()) {
                                    case 1: {
                                        Toast.makeText(getContext(), getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                                .show();
                                        addWatch = !addWatch;
                                        fab.close(true);
                                        break;
                                    }
                                    case 12: {
                                        Toast.makeText(getContext(), getString(R.string.filme_re_add), Toast.LENGTH_SHORT).show();
                                        addWatch = !addWatch;
                                        fab.close(true);
                                        break;
                                    }
                                    case 13: {
                                        Toast.makeText(getContext(), getString(R.string.filme_remove_watchlist), Toast.LENGTH_SHORT).show();
                                        addWatch = !addWatch;
                                        fab.close(true);
                                    }
                                    default: {
                                        Toast.makeText(getContext(), getString(R.string.erro_watch), Toast.LENGTH_SHORT).show();
                                        fab.close(true);
                                    }
                                }
                            }
                        });
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

    @Override
    protected void onStart() {
        super.onStart();
        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
        new TMDVAsync().execute();
    }

    private class ImagemTopFragment extends FragmentPagerAdapter {

        public ImagemTopFragment(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if (movieDb.getImages(ArtworkType.BACKDROP) != null) {
                if (position == 0) {
                    return new ImagemTopScrollFragment().newInstance(movieDb.getBackdropPath());
                }
                Log.d("FilmeActivity", "getItem: ->  " + movieDb.getImages(ArtworkType.BACKDROP).get(position).getFilePath());
                return new ImagemTopScrollFragment().newInstance(movieDb.getImages(ArtworkType.BACKDROP).get(position).getFilePath());
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
            Log.d("FilmeActivity", "doInBackground: -> ID " + id_filme);
            movieDb = movies.getMovie(id_filme, getResources().getString(R.string.IDIOMAS)
                    , images);
            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            setTitle(movieDb.getTitle());
            viewPager.setAdapter(new ImagemTopFragment(getSupportFragmentManager()));
            progressBar.setVisibility(View.INVISIBLE);
            fab.setVisibility(View.VISIBLE);
        }
    }

}