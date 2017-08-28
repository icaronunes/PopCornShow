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
import domain.UserSeasons;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioActivity extends BaseActivity {

    private int tvshow_id, posicao, color, temporada_position;
    private String nome_temporada;
    private TvSeason tvSeason;
    private UserSeasons seasons;
    private boolean seguindo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_epsodios);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager_epsodio);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabLayout_epsodio);
        setExtras();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fragmentManager = getSupportFragmentManager();
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(new EpsodioAdapter(fragmentManager, tvSeason,
                nome_temporada, tvshow_id, color, seguindo, seasons, temporada_position));
        viewPager.setCurrentItem(posicao);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setSelectedTabIndicatorColor(color);
    }

    private void setExtras() {

        if (getIntent().getAction() == null) {
            tvshow_id = getIntent().getIntExtra(Constantes.INSTANCE.getTVSHOW_ID(), 0);
            posicao = getIntent().getIntExtra(Constantes.INSTANCE.getPOSICAO(), 0);
            color = getIntent().getIntExtra(Constantes.INSTANCE.getCOLOR_TOP(), 0);
            tvSeason = (TvSeason) getIntent().getSerializableExtra(Constantes.INSTANCE.getTVSEASONS());
            nome_temporada = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
            temporada_position = getIntent().getIntExtra(Constantes.INSTANCE.getTEMPORADA_POSITION(), 0);
            //colocar no Signal
            seasons = (UserSeasons) getIntent().getSerializableExtra(Constantes.INSTANCE.getUSER());
            seguindo = getIntent().getBooleanExtra(Constantes.INSTANCE.getSEGUINDO(), false);
            getSupportActionBar().setTitle(!tvSeason.getName().isEmpty() ? tvSeason.getName() : nome_temporada );

        } else {
            tvshow_id = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getTVSHOW_ID()));
            posicao = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getPOSICAO()));
            color = Integer.parseInt(getIntent().getStringExtra(Constantes.INSTANCE.getCOLOR_TOP()));
            tvSeason = (TvSeason) getIntent().getSerializableExtra(Constantes.INSTANCE.getTVSEASONS());
            temporada_position = getIntent().getIntExtra(Constantes.INSTANCE.getTEMPORADA_POSITION(), 0);
            nome_temporada = getIntent().getStringExtra(Constantes.INSTANCE.getNOME());
            seguindo = getIntent().getBooleanExtra(Constantes.INSTANCE.getSEGUINDO(), false);
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
