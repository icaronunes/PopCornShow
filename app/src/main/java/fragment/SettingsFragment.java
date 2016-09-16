package fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.util.Log;

import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import utils.LogoffDialog;

/**
 * Created by icaro on 07/09/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        Log.d("SettingsFragment", "onCreate");
        addPreferencesFromResource(R.xml.preferences);

    }

    @Override
    public void onStart() {
        super.onStart();
        if (FilmeApplication.getInstance().isLogado()) {
            LogoffDialog emailPrefs = (LogoffDialog) findPreference("pref_logoff");
            Log.d("SettingsFragment", "true" );
            emailPrefs.setEnabled(true);
        } else {
            LogoffDialog emailPrefs = (LogoffDialog) findPreference("pref_logoff");
            Log.d("SettingsFragment", "false");
            emailPrefs.setEnabled(false);
        }
    }

}
