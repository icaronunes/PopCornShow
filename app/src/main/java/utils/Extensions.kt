package utils

import activity.SettingsActivity
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import java.util.*

/**
 * Created by icaro on 03/09/17.
 */
fun getIdiomaEscolhido(context: Context?): String {

    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    val idioma = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true)
    return if (idioma) {
        UtilsApp.getLocale()
    } else {
        "en"
    }
}
