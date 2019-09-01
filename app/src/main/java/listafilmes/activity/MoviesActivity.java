package listafilmes.activity;

import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

import activity.BaseActivity;
import br.com.icaro.filme.R;
import listafilmes.fragment.MoviesFragment;
import utils.Constantes;

public class MoviesActivity extends BaseActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_main);
		setUpToolBar();
		setupNavDrawer();
		getExtras();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		if (savedInstanceState == null) {
			MoviesFragment filmesFragment = new MoviesFragment();
			filmesFragment.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
					.beginTransaction()
					.add(R.id.container_list_main, filmesFragment)
					.commit();
		}
	}

	private void getExtras() {
		if (getIntent().getAction() == null) {
			getSupportActionBar().setTitle(getString(getIntent()
					.getIntExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.now_playing)));
		} else {
			getSupportActionBar().setTitle(getString(Integer.parseInt(getIntent()
					.getStringExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO()))));
		}
	}
}
