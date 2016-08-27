package activity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.squareup.picasso.Picasso;

import java.util.List;

import adapter.TvShowAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import info.movito.themoviedbapi.model.tv.TvSeason;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

import static domian.FilmeService.getTmdbTvSeasons;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.credits;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.images;
import static info.movito.themoviedbapi.TmdbTV.TvMethod.videos;


public class TvShowActivity extends BaseActivity {

    int id_tvshow = 1396;
    String nome;
    int color_top;
    ViewPager viewPager;
    ImageView imageView;
    TvSeries series;
    CollapsingToolbarLayout layout;
    FloatingActionButton menu_item_favorite, menu_item_watchlist, menu_item_rated;
    FloatingActionMenu fab;
    private boolean addFavorite = true;
    private boolean addWatch = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tvserie_activity);
        setUpToolBar();
        setupNavDrawer();
        nome = getIntent().getStringExtra(Constantes.NOME_TVSHOW);
        color_top = getIntent().getIntExtra(Constantes.COLOR_TOP, 0);
        id_tvshow = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
        menu_item_favorite = (FloatingActionButton) findViewById(R.id.menu_item_favorite);
        menu_item_watchlist = (FloatingActionButton) findViewById(R.id.menu_item_watchlist);
        menu_item_rated = (FloatingActionButton) findViewById(R.id.menu_item_rated);
        fab = (FloatingActionMenu) findViewById(R.id.fab_menu_filme);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        imageView = (ImageView) findViewById(R.id.img_top_tvshow);
        layout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        new TMDVAsync().execute();

        if (FilmeApplication.getInstance().isLogado()) { // Arrumar
            Log.d("FAB", "FAB");
            setColorFab(color_top);
            menu_item_favorite.setOnClickListener(addOrRemoveFavorite());
            menu_item_watchlist.setOnClickListener(addOrRemoveWatch());
            menu_item_rated.setOnClickListener(RatedFilme());
        } else {
            fab.setAlpha(0);
        }
    }

    private void setCoordinator() {
        layout.setBackgroundColor(color_top);
        layout.setTitle(series.getName());
    }

    private View.OnClickListener addOrRemoveWatch() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final ResponseStatus[] status = new ResponseStatus[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        status[0] = FilmeService.addOrRemoverWatchList(id_tvshow, addWatch, TmdbAccount.MediaType.TV);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (status[0].getStatusCode()) {
                                    case 1: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.filme_add_watchlist), Toast.LENGTH_SHORT)
                                                .show();
                                        addWatch = !addWatch;
                                        fab.close(true);
                                        break;
                                    }
                                    case 12: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.filme_re_add), Toast.LENGTH_SHORT).show();
                                        addWatch = !addWatch;
                                        fab.close(true);
                                        break;
                                    }
                                    case 13: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.filme_remove_watchlist), Toast.LENGTH_SHORT).show();
                                        addWatch = !addWatch;
                                        fab.close(true);
                                    }
                                    default: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.erro_watch), Toast.LENGTH_SHORT).show();
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

    private View.OnClickListener addOrRemoveFavorite() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ResponseStatus[] status = new ResponseStatus[1];
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        status[0] = FilmeService.addOrRemoverFavorite(id_tvshow, addFavorite, TmdbAccount.MediaType.TV);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                switch (status[0].getStatusCode()) {
                                    case 1: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.filme_add_favorite), Toast.LENGTH_SHORT)
                                                .show();
                                        addFavorite = !addFavorite;
                                        fab.close(true);
                                        break;
                                    }
                                    case 12: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.filme_re_add), Toast.LENGTH_SHORT).show();
                                        addFavorite = !addFavorite;
                                        fab.close(true);
                                        fab.close(true);
                                        break;
                                    }
                                    case 13: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.filme_remove_favorite), Toast.LENGTH_SHORT).show();
                                        addFavorite = !addFavorite;
                                        fab.close(true);
                                    }
                                    default: {
                                        Toast.makeText(TvShowActivity.this, getString(R.string.erro_watch), Toast.LENGTH_SHORT).show();
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

    private View.OnClickListener RatedFilme() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog alertDialog = new Dialog(TvShowActivity.this);
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
                        final ProgressDialog progressDialog = new ProgressDialog(TvShowActivity.this,
                                android.R.style.Theme_Material_Dialog);
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Salvando...");
                        progressDialog.show();

                        new Thread() {
                            boolean status = false;

                            @Override
                            public void run() {
                                if (UtilsFilme.isNetWorkAvailable(TvShowActivity.this)) {
                                    status = FilmeService.setRatedTvShow(id_tvshow, ratingBar.getRating() * 2);
                                    try {
                                        Thread.sleep(150);
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Log.d("Status", "" + status);
                                                if (status) {
                                                    Toast.makeText(TvShowActivity.this,
                                                            getString(R.string.filme_rated), Toast.LENGTH_SHORT)
                                                            .show();
                                                    fab.close(true);
                                                } else {
                                                    Toast.makeText(TvShowActivity.this,
                                                            getString(R.string.falha_rated), Toast.LENGTH_SHORT)
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

    private void setupViewPagerTabs() {
        viewPager = (ViewPager) findViewById(R.id.viewPager_tvshow);
        viewPager.setOffscreenPageLimit(1);
        viewPager.setAdapter(new TvShowAdapter(this, getSupportFragmentManager(), series));
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout);
        viewPager.setCurrentItem(0);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setImageTop() {
        if (series.getBackdropPath() != null && series.getBackdropPath().length() > 5)
            Picasso.with(TvShowActivity.this)
                    .load(UtilsFilme.getBaseUrlImagem(5) + series.getBackdropPath())
                    .error(R.drawable.top_empty)
                    .into(imageView);

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(imageView, "y", -100, 0)
                .setDuration(1000);
        animatorSet.playTogether(alphaStar);
        animatorSet.start();
    }

    private void setColorFab(int color) {
        fab.setMenuButtonColorNormal(color);
        menu_item_favorite.setColorNormal(color);
        menu_item_watchlist.setColorNormal(color);
        menu_item_rated.setColorNormal(color);
    }


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("TMDVASync", "AsynTask");
            series = FilmeService.getTmdbTvShow()
                    .getSeries(id_tvshow, getString(R.string.IDIOMAS), images, credits, videos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setCoordinator();
            setImageTop();
            setupViewPagerTabs();
        }
    }

}
