package utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by icaro on 31/07/16.
 */

public class Prefs {

    public static String LOGIN_PASS = "login_e_pass";
    public static String LOGIN = "login";
    public static String PASS = "pass";

    public static void setString(Context context,String chave, String valor, String PREF_ID ){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(chave, valor);
        //Log.d("Prefs", "setString "+ valor );
        editor.commit();

    }

    public static void apagarLoginSenha(Context context, String PREF_ID ){
        SharedPreferences pref = context.getSharedPreferences(PREF_ID, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
       // Log.d("Prefs", "apagarLoginSenha" );
        editor.commit();
        //Log.d("Prefs", getString(context, Prefs.LOGIN, PREF_ID));
        //Log.d("Prefs", getString(context, Prefs.PASS, PREF_ID));

    }

    public static String getString(Context context, String chave, String PREF_ID){
        SharedPreferences preferences = context.getSharedPreferences(PREF_ID, 0);
        String valor = preferences.getString(chave, "");
       // Log.d("Prefs", "getString "+ valor );
        return valor;
    }



}
