package activity;

import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.crash.FirebaseCrash;

import java.util.Collections;

import adapter.ListUserAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.Lista;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 04/10/16.
 */
public class OscarActivity  extends BaseActivity{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String list_id;
    private Lista lista;
    private LinearLayout linearLayout;
    private String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.oscar);
        list_id = getIntent().getStringExtra(Constantes.LISTA_ID);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        linearLayout = (LinearLayout) findViewById(R.id.linear_lista);
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
        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
            new TMDVAsync().execute();
        } else {
            snack();
        }
    }

    protected void snack() {
        Snackbar.make(linearLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getBaseContext())) {
                            new TMDVAsync().execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
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
                try {
                    lista = FilmeService.getLista(list_id);
                    //Metodos criados. Tudo gambiara. Precisa arrumar
                    if (lista != null) {
                        Collections.sort(lista.getItems());
                    }
                } catch (Exception e){
                    FirebaseCrash.report(e);
                   // Log.d(TAG, e.getMessage());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(OscarActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
            recyclerView.setAdapter(new ListUserAdapter(OscarActivity.this,
                    lista != null ? lista.items : null));
        }
    }
}
