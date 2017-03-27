package onsignal;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.TaskStackBuilder;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.onesignal.OSNotificationAction;
import com.onesignal.OSNotificationOpenResult;
import com.onesignal.OneSignal;

import org.json.JSONException;
import org.json.JSONObject;

import activity.CrewsActivity;
import activity.ElencoActivity;
import activity.FilmeActivity;
import activity.FilmesActivity;
import activity.FotoPersonActivity;
import activity.ListaGenericaActivity;
import activity.MainActivity;
import activity.PersonActivity;
import activity.ProdutoraActivity;
import activity.ReviewsActivity;
import activity.SimilaresActivity;
import activity.Site;
import activity.TemporadaActivity;
import activity.TrailerActivity;
import activity.TvShowActivity;
import activity.TvShowsActivity;
import applicaton.FilmeApplication;
import domain.FilmeDB;
import domain.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static activity.BaseActivity.getLocale;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.alternative_titles;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.credits;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.releases;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.similar;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.videos;

/**
 * Created by icaro on 16/10/16.
 */

public class CustomNotificationOpenedHandler implements OneSignal.NotificationOpenedHandler {
    private static final String TAG = CustomNotificationOpenedHandler.class.getName();
    private JSONObject jsonData;

    // This fires when a notification is opened by tapping on it.
    @Override
    public void notificationOpened(OSNotificationOpenResult result) {
        Context context = FilmeApplication.getInstance().getBaseContext();
        jsonData = result.notification.payload.additionalData;
        OSNotificationAction.ActionType actionType = result.action.type;


        if (actionType == OSNotificationAction.ActionType.ActionTaken) {
            if ("yes".equals(result.action.actionID)) {
                isButton();
                return;
            }

            if ("no".equals(result.action.actionID)) {
                //
                return;
            }

            if ("talvez".equals(result.action.actionID)) {
                return;
            }
        }

        if (jsonData != null) {

            try {
                JSONObject object = jsonData;
                String action = (String) object.get("action");
                    /*Filme Activity */
                if (action.equals("FilmeActivity")) {
                    Intent intent = new Intent(context, FilmeActivity.class);

                    if (object.has("color"))
                        intent.putExtra(Constantes.COLOR_TOP, object.getInt("color"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.FILME_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(FilmeActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }
                /*TvShow Activity */
                if (action.equals("TvshowActivity")) {
                    Intent intent = new Intent(context, TvShowActivity.class);
                    if (object.has("color"))
                        intent.putExtra(Constantes.COLOR_TOP, object.getInt("color"));

                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME_TVSHOW, object.getString("nome"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.TVSHOW_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(TvShowActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }
                /*Person Activity */
                if (action.equals("PersonActivity")) {
                    Intent intent = new Intent(context, PersonActivity.class);

                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME_PERSON, object.getString("nome"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.PERSON_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(PersonActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("FilmesActivity")) {
                    Intent intent = new Intent(context, FilmesActivity.class);
                    // if (object.has("aba")) {    só funciona para NO CINEMA - ARRUMAR
                    // intent.putExtra(Constantes.ABA, object.getInt("id"));
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(MainActivity.class);
                    stackBuilder.addNextIntent(intent);
                    stackBuilder.startActivities();
                    // }
                }

                if (action.equals("ListaGenericaActivity")) {
                    Intent intent = new Intent(context, ListaGenericaActivity.class);
                    // Log.d("ListaGenericaActivity", "ListaGenericaActivity");
                    if (object.has("nome"))
                        intent.putExtra(Constantes.LISTA_GENERICA, object.getString("nome"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.LISTA_ID, object.getString("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(ListaGenericaActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("TrailerActivity")) {
                    Intent intent = new Intent(context, TrailerActivity.class);

                    if (object.has("sinopse"))
                        intent.putExtra(Constantes.SINOPSE, object.getString("sinopse"));

                    if (object.has("youtube_key")) {
                        intent.putExtra(Constantes.YOU_TUBE_KEY, object.getString("youtube_key"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(TrailerActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("ReviewsActivity")) {
                    Intent intent = new Intent(context, ReviewsActivity.class);

                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME_FILME, object.getString("nome"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.FILME_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(ReviewsActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("ElencoActivity")) {
                    Intent intent = new Intent(context, ElencoActivity.class);

                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME, object.getString("nome"));

                    if (object.has("mediatype"))
                        intent.putExtra(Constantes.MEDIATYPE, object.getString("mediatype"));

                    if (object.has("tvseason"))
                        intent.putExtra(Constantes.TVSEASONS, object.getString("tvseason"));

                    if (object.has("id") && object.has("mediatype")) {
                        intent.putExtra(Constantes.ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        intent.putExtra("notification", false);
                        stackBuilder.addParentStack(PersonActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("CrewsActivity")) {
                    Intent intent = new Intent(context, CrewsActivity.class);

                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME, object.getString("nome"));

                    if (object.has("mediatype"))
                        intent.putExtra(Constantes.MEDIATYPE, object.getString("mediatype"));

                    if (object.has("tvseason"))
                        intent.putExtra(Constantes.TVSEASONS, object.getString("tvseason"));

                    if (object.has("id") && object.has("mediatype")) {
                        intent.putExtra(Constantes.ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        intent.putExtra("notification", false);
                        stackBuilder.addParentStack(CrewsActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("SiteActivity")) {
                    Intent intent = new Intent(context, Site.class);

                    if (object.has("url")) {
                        intent.putExtra(Constantes.SITE, object.getString("url"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(Site.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("ProdutoraActivity")) {
                    Intent intent = new Intent(context, ProdutoraActivity.class);

                    if (object.has("id")) {
                        intent.putExtra(Constantes.PRODUTORA_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(ProdutoraActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("SimilaresActivity")) {
                    Intent intent = new Intent(context, SimilaresActivity.class);
                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME_FILME, object.getString("nome"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.FILME_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(SimilaresActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("FotoPersonActivity")) {
                    Intent intent = new Intent(context, FotoPersonActivity.class);

                    if (object.has("nome"))
                        intent.putExtra(Constantes.NOME_PERSON, object.getString("nome"));

                    if (object.has("position"))
                        intent.putExtra(Constantes.POSICAO, object.getInt("position"));

                    if (object.has("id")) {
                        intent.putExtra(Constantes.PERSON_ID, object.getInt("id"));
                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(FotoPersonActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }

                if (action.equals("TvShowsActivity")) {
                    Intent intent = new Intent(context, TvShowsActivity.class);

                    // if (object.has("aba")) {    só funciona para NO CINEM  // intent.putExtra(Constantes.ABA, object.getInt("id"));
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addParentStack(TvShowsActivity.class);
                    stackBuilder.addNextIntent(intent);
                    stackBuilder.startActivities();

                }

                if (action.equals("TemporadaActivity")) {
                    Intent intent = new Intent(context, TemporadaActivity.class);

                    if (object.has("temporada_id") && object.has("tvshow_id")) {
                        intent.putExtra(Constantes.TVSHOW_ID, object.getInt("tvshow_id"));
                        intent.putExtra(Constantes.TEMPORADA_ID, object.getString("temporada_id"));
                        intent.putExtra(Constantes.NOME, object.getString("nome"));
                        intent.putExtra(Constantes.COLOR_TOP, object.getString("color"));

                        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                        stackBuilder.addParentStack(TemporadaActivity.class);
                        stackBuilder.addNextIntent(intent);
                        stackBuilder.startActivities();
                    }
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Intent intent = new Intent(context, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }

    }

    public void isButton() {
        try {
            String action = (String) jsonData.get("action");

            if (action.equals("FilmeActivity")) {
                final FirebaseDatabase database = FirebaseDatabase.getInstance();
                final FirebaseAuth mAuth = FirebaseAuth.getInstance();

                final int id = jsonData.getInt("id");


                new Runnable() {
                    @Override
                    public void run() {
                        TmdbMovies movies = FilmeService.getTmdbMovies();
                        MovieDb movieDb = movies.getMovie(id, getLocale()
                                        //.toLanguageTag() não funciona na API 14
                                        + ",en,null"
                                , credits, releases, videos, similar, alternative_titles, images);
                       // Log.d(TAG, "run: "+movieDb.getTitle());

                        FilmeDB filmeDB = new FilmeDB();
                        filmeDB.setIdImdb(movieDb.getImdbID());
                        filmeDB.setId(movieDb.getId());
                        filmeDB.setTitle(movieDb.getTitle());
                        filmeDB.setPoster(movieDb.getPosterPath());

                        DatabaseReference myWatch = database.getReference("users").child(mAuth.getCurrentUser()
                                .getUid()).child("watch")
                                .child("movie");

                        myWatch.child(String.valueOf(id)).setValue(filmeDB);

                      //  Log.d(TAG, "run: "+movieDb.getTitle());
                    }
                }.run();

            }

//            if (action.equals("TvshowActivity")) {
//                FirebaseDatabase database = FirebaseDatabase.getInstance();
//                FirebaseAuth mAuth = FirebaseAuth.getInstance();
//                String id_filme = jsonData.getString("id");
//                DatabaseReference myWatch = database.getReference("users").child(mAuth.getCurrentUser()
//                        .getUid()).child("watch")
//                        .child("tvshow");
//
//                myWatch.child(String.valueOf(id_filme)).setValue(null);
//
//            }

        }catch (Exception e){
           // Log.d(TAG, "isButton: "+e.getMessage());
           // Log.d(TAG, "isButton: "+e.toString());
            FirebaseCrash.report(e);
        }
    }
}
// The following can be used to open an Activity of your choice.
// Replace - getApplicationContext() - with any Android Context.
// Intent intent = new Intent(getApplicationContext(), YourActivity.class);
// intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT | Intent.FLAG_ACTIVITY_NEW_TASK);
// startActivity(intent);

// Add the following to your AndroidManifest.xml to prevent the launching of your main Activity
//   if you are calling startActivity above.

/*
        <application ...>
          <meta-data android:name="com.onesignal.NotificationOpened.DEFAULT" android:value="DISABLE" />
        </application>

 */


