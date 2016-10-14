package activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import br.com.icaro.filme.R;
import fragment.SettingsFragment;

public class SettingsActivity extends BaseActivity {

    public static final String PREF_IDIOMA_PADRAO = "pref_idioma_padrao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_acitivity);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.configuracoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(R.id.fragment_settings, new SettingsFragment(), "TRE")
                .commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}