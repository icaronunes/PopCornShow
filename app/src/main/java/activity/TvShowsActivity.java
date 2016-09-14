package activity;

/**
 * Created by icaro on 14/09/16.
 */

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;

import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import fragment.TvShowsFragment;
import utils.Constantes;
import utils.Prefs;


public class TvShowsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filmes);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent()
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing)));
        if (savedInstanceState == null) {
            TvShowsFragment tvShowsFragment = new TvShowsFragment();
            tvShowsFragment.setArguments(getIntent().getExtras());
            setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, tvShowsFragment)
                    .commit();
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

            case android.R.id.home: {
                finish();
                break;
            }
            case R.id.apagar:
                Prefs.apagar(TvShowsActivity.this, Prefs.LOGIN_PASS);
                FilmeApplication.getInstance().setLogado(false);
                startActivity(new Intent(TvShowsActivity.this, MainActivity.class));
                break;
            case R.id.serie: {
                Intent intent = new Intent(this, TvShowActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, 62560);
                intent.putExtra(Constantes.NOME_TVSHOW, "Breaking Bad: A Química do Mal");
                intent.putExtra(Constantes.COLOR_TOP, -14663350);
                startActivity(intent);
                break;
            }
            case R.id.filme: {
                Intent intent = new Intent(this, FilmeActivity.class);
                intent.putExtra(Constantes.FILME_ID, 76341);
                intent.putExtra(Constantes.NOME_FILME, "Mad Max: Estrada da Fúria");
                intent.putExtra(Constantes.COLOR_TOP, -14663350);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

}

