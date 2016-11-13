package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import adapter.EpsodioAdapter;
import br.com.icaro.filme.R;
import domian.UserSeasons;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioActivity extends BaseActivity {

    int tvshow_id, posicao, color;
    ViewPager viewPager;
    TabLayout tabLayout;
    String nome_temporada;
    TvSeason tvSeason;
    FragmentManager fragmentManager;
    UserSeasons seasons;
    boolean seguindo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epsodios);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        viewPager = (ViewPager) findViewById(R.id.viewpager_epsodio);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_epsodio);
        setExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new EpsodioAdapter(this, fragmentManager, tvSeason, nome_temporada, tvshow_id, color, seguindo, seasons));
        viewPager.setCurrentItem(posicao);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(color);

    }

    private void setExtras() {

        if (getIntent().getAction() == null) {
            tvshow_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
            posicao = getIntent().getIntExtra(Constantes.POSICAO, 0);
            color = getIntent().getIntExtra(Constantes.COLOR_TOP, 0);
            tvSeason = (TvSeason) getIntent().getSerializableExtra(Constantes.TVSEASONS);
            nome_temporada = getIntent().getStringExtra(Constantes.NOME);
            seasons = (UserSeasons) getIntent().getSerializableExtra(Constantes.USER);
            seguindo = getIntent().getBooleanExtra(Constantes.SEGUINDO, false);
            getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada );

        } else {
            tvshow_id = Integer.parseInt(getIntent().getStringExtra(Constantes.TVSHOW_ID));
            posicao = Integer.parseInt(getIntent().getStringExtra(Constantes.POSICAO));
            color = Integer.parseInt(getIntent().getStringExtra(Constantes.COLOR_TOP));
            tvSeason = (TvSeason) getIntent().getSerializableExtra(Constantes.TVSEASONS);
            nome_temporada = getIntent().getStringExtra(Constantes.NOME);
            seguindo = getIntent().getBooleanExtra(Constantes.SEGUINDO, false);
            getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada );
        }
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

}
