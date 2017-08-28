package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.widget.ProgressBar;

import br.com.icaro.filme.R;
import domain.ListaJava;
import utils.Constantes;

/**
 * Created by icaro on 14/08/16.
 */
public class ListaUserActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private ListaJava listaJava;
    private String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constantes.INSTANCE.getLISTA_NOME()));
        progressBar = (ProgressBar) findViewById(R.id.progress);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(ListaUserActivity.this, 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
       // new TMDVAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

//    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            try {
//                listaJava = FilmeService.getLista(getIntent().getStringExtra(Constantes.INSTANCE.getLISTA_ID()));
//                //Metodos criados. Tudo gambiara. Precisa arrumar
//            } catch (Exception e) {
//                FirebaseCrash.report(e);
//               // Log.d(TAG, e.getMessage());
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(ListaUserActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            progressBar.setVisibility(View.GONE);
//            recyclerView.setAdapter(new ListUserAdapter(ListaUserActivity.this,
//                    listaJava != null ? listaJava.items : null));
//        }
//    }
}
