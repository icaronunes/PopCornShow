package activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.graphics.Palette;
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

/**
 * Created by icaro on 01/08/16.
 */

public class FavotireActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage favoritos;
    ProgressBar progressBar;
    ResponseStatus status;
    boolean apagar = true;

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
        new TMDVAsync().execute();
    }

    public void AtualizarListaFilme(final int posicao) {
        Snackbar.make(recyclerView, getResources().getString(R.string.excluir_filme), 3000).setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (posicao <= favoritos.getResults().size() && apagar) {
                    favoritos.getResults().remove(favoritos.getResults().get(posicao));

                    recyclerView.setAdapter(new FavotireAdapter(FavotireActivity.this,
                            favoritos != null ? favoritos.getResults() : null, onclickListerne()));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.d("onBusAtualizarLista", "Entrou");
            }

            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
            }
        }).setAction(getResources().getString(R.string.no), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apagar = !apagar;

            }
        }).show();

    }

    private FavotireAdapter.FavoriteOnClickListener onclickListerne() {
        return new FavotireAdapter.FavoriteOnClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                Intent intent = new Intent(FavotireActivity.this, FilmeActivity.class);

                ImageView imageView = (ImageView) view;
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    Palette.Builder builder = new Palette.Builder(bitmap);
                    Palette palette = builder.generate();
                    for (Palette.Swatch swatch : palette.getSwatches()) {
                        intent.putExtra(Constantes.COLOR_TOP, swatch.getRgb());
                    }
                }
                intent.putExtra(Constantes.FILME_ID, favoritos.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, favoritos.getResults().get(position).getTitle());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int posicao) {
                Log.d("onBusAtualizarLista", "onClickLong - " + posicao);
                Log.d("onBusAtualizarLista", "onClickLong - " + favoritos.getResults().get(posicao).toString());
                final int id = favoritos.getResults().get(posicao).getId();
                final String user = FilmeApplication.getInstance().getUser();
                final String pass = FilmeApplication.getInstance().getPass();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        status = FilmeService.addOrRemoverFavorite(user, pass, id, false);
                    }
                }).start();

                AtualizarListaFilme(posicao);
            }
        };
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            favoritos = FilmeService.getTotalFavorite();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new FavotireAdapter(FavotireActivity.this,
                    favoritos != null ? favoritos.getResults() : null, onclickListerne()));
        }
    }

}