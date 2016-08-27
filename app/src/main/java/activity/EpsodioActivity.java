package activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;

import adapter.EpsodioAdapter;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioActivity extends BaseActivity {

    int tvshow_id, tvseason_id, epsodio_id, posicao;
    ViewPager viewPager;
    TabLayout tabLayout;
    TvSeason tvSeason;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epsodios);
        setUpToolBar();

        viewPager = (ViewPager) findViewById(R.id.viewpager_epsodio);
        tabLayout = (TabLayout) findViewById(R.id.tabLayout_epsodio);

        tvshow_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
        tvseason_id = getIntent().getIntExtra(Constantes.TVSEASON_ID, 0);
        epsodio_id = getIntent().getIntExtra(Constantes.EPSODIO_ID, 0);
        posicao = getIntent().getIntExtra(Constantes.POSICAO, 0);
        tvSeason = (TvSeason) getIntent().getSerializableExtra("teste");
        getSupportActionBar().setTitle(tvSeason.getName());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new EpsodioAdapter(this, getSupportFragmentManager(), tvSeason));
        viewPager.setCurrentItem(posicao);
        tabLayout.setupWithViewPager(viewPager);
    }

}
