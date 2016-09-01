package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;

import adapter.ListUserAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TmdbLists;
import info.movito.themoviedbapi.model.MovieList;
import utils.Constantes;

/**
 * Created by icaro on 14/08/16.
 */
public class ListaUserActivity extends BaseActivity {

    RecyclerView recyclerView;
    MovieList lists;
    ProgressBar progressBar;
    String list_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constantes.LISTA_NOME));
        list_id = getIntent().getStringExtra(Constantes.LISTA_ID);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(ListaUserActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
       new TMDVAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("ListaUserActivity", "" + lists);
            lists = FilmeService.getTmdbList().getList(getIntent().getStringExtra(Constantes.LISTA_ID));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new ListUserAdapter(ListaUserActivity.this,
                    lists != null ? lists.getItems() : null));
        }
    }
}
