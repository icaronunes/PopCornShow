package fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import br.com.icaro.filme.R;

/**
 * Created by icaro on 07/09/16.
 */
public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
