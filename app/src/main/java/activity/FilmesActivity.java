package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import br.com.icaro.filme.R;
import fragment.FilmesFragment;
import utils.Constantes;

public class FilmesActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        setupNavDrawer();
        getExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        if (savedInstanceState == null) {
            FilmesFragment filmesFragment = new FilmesFragment();
            filmesFragment.setArguments(getIntent().getExtras());
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, filmesFragment)
                    .commit();
        }

    }

    private void getExtras() {
        if (getIntent().getAction() == null) {
            getSupportActionBar().setTitle(getString(getIntent()
                    .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing)));
        } else {
            getSupportActionBar().setTitle(getString(Integer.parseInt(getIntent()
                    .getStringExtra(Constantes.NAV_DRAW_ESCOLIDO))));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Procura Filme");
        searchView.setEnabled(false);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:{
                finish();
                break;
            }

        }
        return super.onOptionsItemSelected(item);
    }

}
