package activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import adapter.WatchAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.id.watchlist;

public class WatchListActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage watchlist;
    ProgressBar progressBar;
    ResponseStatus status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent()
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.quero_assistir)));
        setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(WatchListActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

    }

    private WatchAdapter.WatchListOnClickListener onclickListerne() {
        return new WatchAdapter.WatchListOnClickListener() {
            @Override
            public void onClick(View view, int posicao) {
                Intent intent = new Intent(WatchListActivity.this, FilmeActivity.class);
                ImageView imageView = (ImageView) view;
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.FILME_ID, watchlist.getResults().get(posicao).getId());
                intent.putExtra(Constantes.NOME_FILME, watchlist.getResults().get(posicao).getTitle());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int posicao) {
                Log.d("onBusAtualizarLista", "onClickLong - " + posicao);
                Log.d("onBusAtualizarLista", "onClickLong - " + watchlist.getResults().get(posicao).toString());
                final int id = watchlist.getResults().get(posicao).getId();
                final String user = FilmeApplication.getInstance().getUser();
                final String pass = FilmeApplication.getInstance().getPass();
                new AlertDialog.Builder(WatchListActivity.this)
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(watchlist.getResults().get(posicao).getTitle())
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
                                        status = FilmeService.addOrRemoverWatchList(id, false, TmdbAccount.MediaType.MOVIE);
                                        //Arrumar para utilizar em filme e tvshow

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    watchlist.getResults().remove(watchlist.getResults().get(posicao));
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


    private class TMDVAsync extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... voids) {
            watchlist = FilmeService.getWatchListTotal();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            if (watchlist != null) {
                recyclerView.setAdapter(new WatchAdapter(WatchListActivity.this,
                        watchlist != null ? watchlist.getResults() : null, onclickListerne()));
            }
        }
    }

}
