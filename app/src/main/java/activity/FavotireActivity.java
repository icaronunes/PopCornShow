package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import adapter.FavotireAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;
import utils.Prefs;

/**
 * Created by icaro on 01/08/16.
 */

public class FavotireActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage favoritos, watchlist, rated;
    ProgressBar progressBar;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent().getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.favorite)));
        setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(FavotireActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        FilmeApplication.getInstance().getBus().unregister(this);
    }

    private FavotireAdapter.FavoriteOnClickListener onclickFavorito() {
        return new FavotireAdapter.FavoriteOnClickListener() {
            @Override
            public void onClickCoracao(final View view, final int posicao, final boolean addOrRemove) {
                Log.d("FavotireActivity", "ENTROU Coracao");
            }

            @Override
            public void onClickEstrela(final View view, final int posicao, final boolean addOrRemove) {
                Log.d("FavotireActivity", "ENTROU Estrela");
            }

            @Override
            public void onClickRelogio(final View view, final int posicao, final boolean addOrRemove) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String user = Prefs.getString(FavotireActivity.this, Prefs.LOGIN, Prefs.LOGIN_PASS);
                        String pass = Prefs.getString(FavotireActivity.this, Prefs.PASS, Prefs.LOGIN_PASS);
                        Log.d("ResponseStatus", "run " + favoritos.getResults().get(posicao).getTitle());
                        Log.d("ResponseStatus", "Valor boolean " + addOrRemove);
                        ResponseStatus status = FilmeService.addOrRemoverWatchList(user, pass, favoritos.getResults().get(posicao).getId(),
                                addOrRemove);
                        Log.d("ResponseStatus", status.toString());
                        // Log.d("ResponseStatus", "" + status.toString());
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ImageView imageView = (ImageView) view;
                                if (!addOrRemove) {
                                    Log.d("ResponseStatus", "RELOGIO");
                                    imageView.setImageResource(R.drawable.relogio);


                                } else {
                                    Log.d("ResponseStatus", "ICON AGENDA");
                                    imageView.setImageResource(R.drawable.icon_agenda);

                                }

                            }
                        });
                    }

                }).start();
               // recyclerView.getAdapter().notifyDataSetChanged();
            }
        };
    }

//    @Subscribe
//    public void teste(int position) {
//        Log.d("teste", "Posicao "+position);
//        favoritos.getResults().remove(favoritos.getResults().get(position));
//        Log.d("teste", "tamanho "+favoritos.getResults().size());
//        recyclerView.getAdapter().notifyDataSetChanged();
//        recyclerView.setAdapter(new FavotireAdapter(FavotireActivity.this,
//                favoritos.getResults(), rated.getResults(),
//                onclickFavorito()));
//
//    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String user = Prefs.getString(FavotireActivity.this, Prefs.LOGIN, Prefs.LOGIN_PASS);
            String pass = Prefs.getString(FavotireActivity.this, Prefs.PASS, Prefs.LOGIN_PASS);
            //watchlist = FilmeService.getWatchList(user, pass, 1);
            //Log.d("FavoriteActivity", "watch " + watchlist.getTotalResults());
            //favoritos = FilmeService.getFavorite(user, pass);
           // Log.d("FavoriteActivity", "favorito " + favoritos.getTotalResults());
            rated = FilmeService.getRated(user, pass, 1);
            Log.d("FavoriteActivity", "rated " + rated.getTotalResults());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            Log.d("FavoriteActivity", "TMDVAsync.PosEx");
//            recyclerView.setAdapter(new FavotireAdapter(FavotireActivity.this,
//                    favoritos.getResults(), rated.getResults(),
//                    onclickFavorito()));

            recyclerView.setAdapter(new FavotireAdapter(FavotireActivity.this,
                    FilmeApplication.getInstance().getFavorite().getResults(), rated.getResults(),
                    onclickFavorito()));

        }
    }

}