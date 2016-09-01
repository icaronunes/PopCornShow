package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import adapter.EpsodioAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

import static br.com.icaro.filme.R.string.nome;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioActivity extends BaseActivity {

    int tvshow_id,  posicao, color;
    ViewPager viewPager;
    TabLayout tabLayout;
    String nome_serie = null;
    TvSeason tvSeason;
    FragmentManager fragmentManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epsodios);
        setUpToolBar();

        viewPager = (ViewPager) findViewById(R.id.viewpager_epsodio);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_epsodio);

        tvshow_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
        posicao = getIntent().getIntExtra(Constantes.POSICAO, 0);
        nome_serie = getIntent().getStringExtra(Constantes.NOME_TVSHOW);
        color = getIntent().getIntExtra(Constantes.COLOR_TOP, 0);
        tvSeason = (TvSeason) getIntent().getSerializableExtra(Constantes.TVSEASONS);
        getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : getString(R.string.sem_titulo));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        fragmentManager = getSupportFragmentManager();
        viewPager.setOffscreenPageLimit(3);
        viewPager.setAdapter(new EpsodioAdapter(this, fragmentManager, tvSeason, nome_serie, tvshow_id, color));
        viewPager.setCurrentItem(posicao);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(color);

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
