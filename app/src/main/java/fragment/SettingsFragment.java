package fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.onesignal.OneSignal;

import activity.SettingsActivity;
import br.com.icaro.filme.R;
import utils.LogoffDialog;

/**
 * Created by icaro on 07/09/16.
 */
public class SettingsFragment extends PreferenceFragment {
    private final String TAG = this.getClass().getName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            LogoffDialog emailPrefs = (LogoffDialog) findPreference("pref_logoff");
           // Log.d("SettingsFragment", "true" );
            emailPrefs.setEnabled(true);
        } else {
            LogoffDialog emailPrefs = (LogoffDialog) findPreference("pref_logoff");
           // Log.d("SettingsFragment", "false");
            emailPrefs.setEnabled(false);
        }

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean notificacao = sharedPref.getBoolean(SettingsActivity.PREF_NOTIFICACAO, true);

        if (notificacao) {
            CheckBoxPreference boxPreference = (CheckBoxPreference) findPreference("pref_notificacao");
            boxPreference.setChecked(true);
        } else {
            CheckBoxPreference boxPreference = (CheckBoxPreference) findPreference("pref_notificacao");
            boxPreference.setChecked(false);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean notificacao = sharedPref.getBoolean(SettingsActivity.PREF_NOTIFICACAO, true);

        if (notificacao){
            Log.d(TAG, "True");
            OneSignal.setSubscription(true);
        } else {
            OneSignal.setSubscription(false);
            Log.d(TAG, "False");
        }

    }
}
