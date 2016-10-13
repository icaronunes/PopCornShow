package activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Collections;

import adapter.ListUserAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import domian.Lista;
import utils.Constantes;

/**
 * Created by icaro on 04/10/16.
 */
public class ListaGenericaActivity  extends BaseActivity{

    RecyclerView recyclerView;
    ProgressBar progressBar;
    String list_id;
    Lista lista;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (getIntent().getAction() != null) {
            getSupportActionBar().setTitle(getIntent().getStringExtra(Constantes.LISTA_GENERICA));
            list_id = getIntent().getStringExtra(Constantes.LISTA_ID);
        }else {
            startActivity(new Intent(this, MainActivity.class));
        }
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        adview.loadAd(adRequest);

    }

    @Override
    protected void onResume() {
        super.onResume();
        new TMDVAsync().execute();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            if (lista == null) {
                lista = FilmeService.getLista(list_id);
                //Metodos criados. Tudo gambiara. Precisa arrumar
                if (lista.getItems() != null) {
                    Collections.sort(lista.getItems());
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new ListUserAdapter(ListaGenericaActivity.this,
                    lista != null ? lista.items : null));
        }
    }
}
