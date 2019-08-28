package configuracao;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import activity.BaseActivity;
import br.com.icaro.filme.R;

public class SettingsActivity extends BaseActivity {

    public static final String PREF_IDIOMA_PADRAO = "pref_idioma_padrao";
    public static final String PREF_NOTIFICACAO = "pref_notificacao";
    public static final String PREF_SAVE_CONEXAO = "pref_save_conexao";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_acitivity);
        setUpToolBar();
        getSupportActionBar().setTitle(R.string.configuracoes);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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