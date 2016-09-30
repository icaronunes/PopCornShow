package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import adapter.ListaUserAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import utils.Constantes;


/**
 * Created by icaro on 14/08/16.
 */
public class ListUserActivity extends BaseActivity {

    ListView listView;
    TmdbAccount.MovieListResultsPage lists;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lista_user);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Listas...");
        listView = (ListView) findViewById(R.id.listview_lista_user);

        new TMDVAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            lists = FilmeService.getListAccount("en", 1);
            //METODO NAO CARREGA INFORMAÇÕES DOS FILMES DA LISTA

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            listView.setAdapter(new ListaUserAdapter(ListUserActivity.this, lists));

                Log.d("LISTA", ""+lists.getResults().size());
                Log.d("LISTA", ""+lists.getResults().get(0).getItems());

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    Intent intent = new Intent(ListUserActivity.this, ListaUserActivity.class);
                    intent.putExtra(Constantes.LISTA_ID, lists.getResults().get(i).getId());
                    intent.putExtra(Constantes.LISTA_NOME, lists.getResults().get(i).getName());
                    Log.d("ListUserActivity", lists.getResults().get(i).getId());
                    startActivity(intent);
                }
            });
        }
    }
}