package activity;

import android.os.Bundle;

import br.com.icaro.filme.R;
import fragment.FilmesFragment;
import utils.Constantes;

public class MainActivity extends BaseActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(getIntent()
                .getIntExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing)));
        if (savedInstanceState == null) {
            FilmesFragment filmesFragment = new FilmesFragment();
            filmesFragment.setArguments(getIntent().getExtras());
            setCheckable(getIntent().getIntExtra(Constantes.ABA, 0));
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, filmesFragment)
                    .commit();
        }
    }
}
