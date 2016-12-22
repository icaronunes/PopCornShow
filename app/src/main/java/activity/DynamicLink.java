package activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.firebase.analytics.FirebaseAnalytics;

import utils.Constantes;

/**
 * Created by icaro on 16/12/16.
 */

public class DynamicLink extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    public static final String TAG = DynamicLink.class.getName();
    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpDynamicLinks();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void setUpDynamicLinks() {
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(AppInvite.API)
                .build();

        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(@NonNull AppInviteInvitationResult result) {
                                if (result.getStatus().isSuccess()) {
                                    // Extract deep link from Intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    Log.d(TAG, deepLink);

                                    // Handle the deep link. For example, open the linked
                                    // content, or apply promotional credit to the user's
                                    // account.
                                    chamarIntent(deepLink);
                                    // ...
                                } else {
                                    Log.d(TAG, "getInvitation: no deep link found.");
                                    startActivity(new Intent(DynamicLink.this, MainActivity.class));
                                }
                            }
                        });
    }

    private void chamarIntent(String deepLink) {
        String action = null;
        int id = 0;
        String color = null;
        String nome = null;
        //https://br.com.icaro.filme/al=https
        Uri  uri = Uri.parse(Uri.decode(deepLink));

        for (String s : uri.getQueryParameterNames()) {

            if (s.equalsIgnoreCase("action")){
                action = uri.getQueryParameter(s);
            }

            if (s.equalsIgnoreCase("id")){
                id = Integer.parseInt(uri.getQueryParameter(s));
            }

            if (s.equalsIgnoreCase("nome")){
                nome = uri.getQueryParameter(s);
            }

        }

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(id));
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, action);
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


        if (action == null || id == 0) {
            startActivity(new Intent(DynamicLink.this, MainActivity.class));
            finish();
        } else {

            if (action.equals("FA")) {
                Intent intent = new Intent(DynamicLink.this, FilmeActivity.class);

                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.FILME_ID, id);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(DynamicLink.this);
                stackBuilder.addParentStack(FilmeActivity.class);
                stackBuilder.addNextIntent(intent);
                stackBuilder.startActivities();
                finish();
            }

            if (action.equals("TA")) {
                Intent intent = new Intent(DynamicLink.this, TvShowActivity.class);

                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.NOME_TVSHOW, nome);
                intent.putExtra(Constantes.TVSHOW_ID, id);
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(DynamicLink.this);
                stackBuilder.addParentStack(TvShowActivity.class);
                stackBuilder.addNextIntent(intent);
                stackBuilder.startActivities();
                finish();
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
