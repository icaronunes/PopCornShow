package activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import adapter.WatchAdapter;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;
import utils.UtilsFilme;

public class WatchListActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieResultsPage watchlist;
    ProgressBar progressBar;
    boolean apagar = true;
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
            public void onClickLong(View view, int posicao) {
                Log.d("onBusAtualizarLista", "onClickLong - " + posicao);
                Log.d("onBusAtualizarLista", "onClickLong - " + watchlist.getResults().get(posicao).toString());
                final int id = watchlist.getResults().get(posicao).getId();
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

    public void AtualizarListaFilme(final int posicao) {
        Snackbar.make(recyclerView, getResources().getString(R.string.excluir_filme), 3000)
                .setCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                if (posicao <= watchlist.getResults().size() && apagar) {
                    watchlist.getResults().remove(watchlist.getResults().get(posicao));

                    recyclerView.setAdapter(new WatchAdapter(WatchListActivity.this,
                            watchlist != null ? watchlist.getResults() : null, onclickListerne()));
                }
                recyclerView.getAdapter().notifyDataSetChanged();
                Log.d("AtualizarListaFilme", "Entrou");
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

    @Override
    protected void onResume() {
        super.onResume();
        new TMDVAsync().execute();
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
