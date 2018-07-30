package utils

import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import java.util.*

/**
 * Created by icaro on 03/09/17.
 */
fun getIdiomaEscolhido(context: Context ) : String {

    val sharedPref = PreferenceManager.getDefaultSharedPreferences(context)
    val idioma_padrao = sharedPref.getBoolean("pref_idioma_padrao", true)
    return if (idioma_padrao) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Locale.getDefault().toLanguageTag()
        } else Locale.getDefault().language + "-" + Locale.getDefault().country
    } else {
        "en"
    }
}