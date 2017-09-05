package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import br.com.icaro.filme.R;
import domain.ListaJava;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 04/10/16.
 */
public class ListaGenericaActivity  extends BaseActivity{

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private String list_id;
    private String old = "";
    private Map<String, String> map = new HashMap<>();
    private ListaJava listaJava = null;
    private SwipeRefreshLayout swipeRefreshLayout;
    private String TAG = this.getClass().getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lista);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getIntent().getStringExtra(Constantes.INSTANCE.getLISTA_GENERICA()));
        list_id = getIntent().getStringExtra(Constantes.INSTANCE.getLISTA_ID());
        map = (Map<String, String>) getIntent().getSerializableExtra(Constantes.INSTANCE.getBUNDLE());
        progressBar = (ProgressBar) findViewById(R.id.progress);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeToRefresh);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_favorite);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        if (map == null){
            swipeRefreshLayout.setEnabled(false);
        }
        swipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark, R.color.accent);
        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());

//        AdView adview = (AdView) findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .build();
//        adview.loadAd(adRequest);

    }

    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String numero = String.valueOf(new Random().nextInt(10));
                list_id = map.get("id"+numero);
                if (old.equals(list_id)){
                    if (list_id.equals("11060")) {
                        list_id = "10"; //Top 50 Grossing Films of All Time (Worldwide)
                    } else {
                        list_id = "11060"; // PopCorn Favorites
                    }
                }

                if(UtilsApp.isNetWorkAvailable(ListaGenericaActivity.this)) {
                 //   new TMDVAsync().execute();
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(UtilsApp.isNetWorkAvailable(this)) {
        //    new TMDVAsync().execute();
        }
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

//    private class TMDVAsync extends AsyncTask<Void, Void, Void> {
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//           // Log.d(TAG, list_id);
//        }
//
//        @Override
//        protected Void doInBackground(Void... voids) {
//            //if (listaJava == null) {
//                try {
//                    listaJava = null;
//                    listaJava = FilmeService.getLista(list_id);
//                    //Metodos criados. Tudo gambiara. Precisa arrumar - 11060
//                    if (listaJava.getItems() != null) {
//                        Collections.sort(listaJava.getItems());
//                    }
//                } catch (Exception e){
//                    FirebaseCrash.report(e);
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            Toast.makeText(ListaGenericaActivity.this, R.string.ops, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//                }
//            //}
//
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//                if (listaJava != null) {
//                    swipeRefreshLayout.setRefreshing(false);
//                    getSupportActionBar().setTitle(listaJava.getName());
//                    progressBar.setVisibility(View.GONE);
//                    recyclerView.setAdapter(new ListUserAdapter(ListaGenericaActivity.this,
//                            listaJava != null ? listaJava.items : null));
//                    old = list_id;
//                }
//        }
//    }
}
