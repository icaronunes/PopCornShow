package utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import domian.FilmeService;
import info.movito.themoviedbapi.model.config.Timezone;


/**
 * Created by icaro on 24/06/16.
 */

public class UtilsFilme {

    private static final String TAG = UtilsFilme.class.getName();


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

    public static boolean verificaLancamento(Date air_date) {
        boolean data;
        //Arrumar. Ta esquisito.
        Date myDate = Calendar.getInstance().getTime();
        if (air_date.before(myDate)) {
            data = true;
        } else if (air_date.after(myDate))
            data = false;
        else
            data = true;
        return data;
    }

    public static boolean verificaDataProximaLancamento(Date air_date) {


        Calendar calendar = Calendar.getInstance();
        calendar.setTime(air_date);
        Calendar hoje = Calendar.getInstance();
        hoje.setTime(Calendar.getInstance().getTime());
      //  Log.d(TAG, "" + calendar.get(Calendar.DATE));
      //  Log.d(TAG, "" + hoje.get(Calendar.DATE));

        if (calendar.after(hoje)) {
            return false;
        } else {
            if (calendar.get(Calendar.YEAR) == hoje.get(Calendar.YEAR)) {
                    if (calendar.get(Calendar.WEEK_OF_YEAR) == hoje.get(Calendar.WEEK_OF_YEAR)) {
                       // Log.d(TAG, "calendar " + calendar.get(Calendar.WEEK_OF_YEAR));
                      //  Log.d(TAG, "hoje " + hoje.get(Calendar.WEEK_OF_YEAR));
                        return true;
                    }
            }
            return false;
        }
    }

//    public static  boolean verificaDataProximaLancamento(Date air_date) {
//        boolean data;
//        //Arrumar. Ta esquisito.
//        Date myDate = Calendar.getInstance().getTime();
//        Log.d(TAG, "Hoje " + myDate);
//        Log.d(TAG, "Emissao " + air_date);
//        myDate.setMonth(myDate.getMonth() + 1);
//        Log.d(TAG, "Depois " + myDate);
//        if (air_date.before(myDate)) {
//            data = true;
//        } else if (air_date.after(myDate))
//            data = false;
//        else
//            data = true;
//        return data;
//    }

    public static void writeBytes(File file, byte[] bytes) {
        try {
            FileOutputStream stream = new FileOutputStream(file);
            stream.write(bytes);
            stream.flush();
            stream.close();
        } catch (IOException e) {
           // Log.e(TAG, e.getMessage(), e);
            FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
            FirebaseCrash.report(e);
        }

    }

    public static File salvaImagemMemoriaCache(Context context, ImageView imageView, String endereco) {
        //Usar metodo do BaseActivity
        File file = context.getExternalCacheDir();

        if (!file.exists()) {
            file.mkdir();
          //  Log.e("salvarArqNaMemoriaIn", "Directory created");
        }
        File dir = new File(file, endereco);

        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            UtilsFilme.writeBitmap(dir, bitmap);
        }
        return dir;
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
            FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
            FirebaseCrash.report(e);
           // Log.e(TAG, e.getMessage(), e);
        }
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
            FirebaseCrash.logcat(Log.ERROR, TAG, "NPE caught");
            FirebaseCrash.report(e);
        }

        return false;

    }

    public static Timezone getTimezone() {
        for (Timezone timezone : FilmeService.getTimeZone()) {
            if (timezone.getCountry().equals(Locale.getDefault().getCountry())) {
              //  Log.d("Timezone", timezone.getCountry());
                return timezone;
            }
        }
        return null;
    }


    public static int loadPalette(View view) {

        ImageView imageView = (ImageView) view;
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            Palette.Builder builder = new Palette.Builder(bitmap);
            Palette palette = builder.generate();
            for (Palette.Swatch swatch : palette.getSwatches()) {
                return swatch.getRgb();
            }
        }
        return 0;
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
