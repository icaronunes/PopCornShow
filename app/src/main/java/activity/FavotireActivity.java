package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import adapter.FavotireAdapter;
import adapter.WatchAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;

import static android.R.attr.id;
import static br.com.icaro.filme.R.id.watchlist;

/**
 * Created by icaro on 01/08/16.
 */

public class FavotireActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage favoritos;
    ProgressBar progressBar;
    ResponseStatus status;

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private FavotireAdapter.FavotireOnClickListener onclickListerne() {
        return new FavotireAdapter.FavotireOnClickListener() {
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
                new AlertDialog.Builder(FavotireActivity.this)
                        .setIcon(R.drawable.icon_coracao_redondo)
                        .setTitle(favoritos.getResults().get(posicao).getTitle())
                        .setMessage(getResources().getString(R.string.excluir_filme))
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        status = FilmeService.addOrRemoverFavorite(user, pass, id, false);

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    favoritos.getResults().remove(favoritos.getResults().get(posicao));
                                                    recyclerView.getAdapter().notifyItemRemoved(posicao);
                                                    recyclerView.getAdapter().notifyItemChanged(posicao);
                                                }
                                            }
                                        });
                                    }
                                }).start();
                            }
                        }).show();
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