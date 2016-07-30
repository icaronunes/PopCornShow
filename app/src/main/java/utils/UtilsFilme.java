package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by icaro on 24/06/16.
 */

public class UtilsFilme {

    private static final String TAG = "UtilsFilme";

    public static void toast(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    /* Checks if external storage is available for read and write */
    public static boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public static boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public static void writeBytes(File file, byte[] bytes) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.flush();
            stream.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }

    }

    public static void writeBitmap(File file, Bitmap bitmap) {
        try {
            if (!file.exists()) {
                file.createNewFile();
                Log.e("salvarArqNaMemoriaIn", "Arquivo Criado");
            }
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.close();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public static Context getContext() {
        return getContext();
    }

    public static boolean isNetWorkAvailable(Context context) {
        try {
            ConnectivityManager connectivityManager =
                    (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                return false;
            } else {

                NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
                if (networkInfos != null) {
                    for (int i = 0; i < networkInfos.length; i++) {
                        if (networkInfos[i].isConnected()) {
                            return true;
                        }
                    }
                }
            }


        } catch (SecurityException e) {
            Log.d("UtilsFilme", "isNetWorkAvailable: " + e);
        }
        return false;

    }

    public static String getBaseUrlImagem(int tamanho) {

        switch (tamanho) {
            case 1: {
                return "http://image.tmdb.org/t/p/w92/";

            }
            case 2: {
                return "http://image.tmdb.org/t/p/w154/";

            }
            case 3: {
                return "http://image.tmdb.org/t/p/w185/";

            }
            case 4: {
                return "http://image.tmdb.org/t/p/w342/";

            }
            case 5: {
                return "http://image.tmdb.org/t/p/w500/";

            }
            case 6: {
                return "http://image.tmdb.org/t/p/w780/";

            }
            case 7: {
                return "http://image.tmdb.org/t/p/original/";

            }
            default:
                return null;
        }

    }
}
