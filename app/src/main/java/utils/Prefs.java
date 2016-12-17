package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by icaro on 31/07/16.
 */

public class Prefs {

    public static final String LOGIN_PASS = "login_e_pass";
    public static final String LOGIN = "login";
    public static final String PASS = "pass";

    public static String getString(Context context, String chave, String PREF_ID){
        SharedPreferences preferences = context.getSharedPreferences(PREF_ID, 0);
        String valor = preferences.getString(chave, "");
       // Log.d("Prefs", "getString "+ valor );
        return valor;
    }

}
