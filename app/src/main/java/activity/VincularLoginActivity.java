package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import br.com.icaro.filme.R;

/**
 * Created by icaro on 24/11/16.
 */
public class VincularLoginActivity extends BaseActivity implements GoogleApiClient.OnConnectionFailedListener{


    private static final int RC_SIGN_IN = 1;
    private final String TAG = VincularLoginActivity.class.getName();
    private FirebaseAuth mAuth;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mAuthProgressDialog;
    private CallbackManager mCallbackManager;

    public VincularLoginActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getBaseContext());
        FacebookSdk.sdkInitialize(getBaseContext());
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        //stateListener = getAuthStateListener();

        setGoogle();
        setFacebook();

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with PopCorn...");
        mAuthProgressDialog.setCancelable(false);

        TextView criar_login = (TextView) findViewById(R.id.vincular_login);
        criar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(VincularLoginActivity.this)
                        .setView(R.layout.criar_login)
                        .create();
                dialog.show();

                Button cancel = (Button) dialog.findViewById(R.id.bt_new_login_cancel);
                final TextInputLayout login = (TextInputLayout) dialog.findViewById(R.id.vincular_login);
                final TextInputLayout senha = (TextInputLayout) dialog.findViewById(R.id.criar_pass);
                final TextInputLayout repetirSenha = (TextInputLayout) dialog.findViewById(R.id.criar_repetir_pass);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                Button ok = (Button) dialog.findViewById(R.id.bt_new_login_ok);
                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (validarParametros(login, senha, repetirSenha)) {
                            criarLoginEmail(login.getEditText().getText().toString(), senha.getEditText().getText().toString() );
                            dialog.dismiss();
                        }
                    }
                });
            }
        });

    }

    private boolean validarParametros(TextInputLayout login, TextInputLayout senha, TextInputLayout repetirSenha) {
        String Slogin, Ssenha, Srepetir;
        Slogin = login.getEditText().getText().toString();
        Ssenha = senha.getEditText().getText().toString();
        Srepetir = repetirSenha.getEditText().getText().toString();

        if (Slogin.contains("@") && Slogin.contains(".")){
            if (Ssenha.equals(Srepetir)){
                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("deprecation")
    private void setGoogle() {
        //GOOGLE
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestIdToken(getString(R.string.web_client_id))
                .build();


        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Customize sign-in button. The sign-in button can be displayed in
        // multiple sizes and color schemes. It can also be contextually
        // rendered based on the requested scopes. For example. a red button may
        // be displayed when Google+ scopes are requested, but a white button
        // may be displayed when only basic profile is requested. Try adding the
        // Scopes.PLUS_LOGIN scope to the GoogleSignInOptions to see the
        // difference.
        SignInButton signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setColorScheme(SignInButton.SIZE_STANDARD);
        signInButton.setScopes(gso.getScopeArray());
        signInButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    private void setFacebook() {
        mCallbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
              //  Log.d(TAG, "facebook:onSuccess: " + loginResult.getAccessToken());
                accessFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
               // Log.d(TAG, "facebook:onCancel ");
            }

            @Override
            public void onError(FacebookException error) {
               // Log.d(TAG, "facebook:onError ", error);
            }
        });
    }

    private void accessFacebook(AccessToken accessToken) {
        accessLoginData("facebook", accessToken.getToken());
    }

    public void onclick(View view) {
        switch (view.getId()) {

            case R.id.facebook: {
               // Log.d(TAG, "Facebook");
                LogarFacebook();
                break;
            }
            case R.id.logar: {
                //LogarComEmail();
                break;
            }
        }
    }


    private void LogarFacebook() {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "email")
                );
    }


    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (user != null) {
                    //startActivity(new Intent(VincularLoginActivity.this, MainActivity.class));
                    //finish();
                } else {
                 //   Log.d(TAG, "não logou... ");
                }
            }
        };

        return callback;
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
               // Log.d(TAG, account.getDisplayName());
                accessGoogle(account.getIdToken());
            } else {
               // Log.d(TAG, "Falha no login Google");
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void accessGoogle(String token) {
        accessLoginData("google", token);
    }

    private void accessLoginData(String provider, String... tokens) {
        mAuthProgressDialog.show();
        if (tokens != null
                && tokens.length > 0
                && tokens[0] != null) {

            AuthCredential credential = FacebookAuthProvider.getCredential(tokens[0]);

            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential(tokens[0], null) : credential;
            mAuth.getCurrentUser().linkWithCredential(credential)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                          //  Log.d(TAG, "linkWithCredential:onComplete:" + task.isSuccessful());
                            if (task.isSuccessful()) {
                          //      Log.d(TAG, "linkWithCredential:onComplete: " + "Logins vinculados");
                                finish();
                                startActivity(new Intent(VincularLoginActivity.this, MainActivity.class));
                                mAuthProgressDialog.dismiss();
                            }
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(VincularLoginActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                                mAuthProgressDialog.dismiss();
                            }
                        }
                    });
        } else {
            mAuth.signOut();
        }

    }


    public void criarLoginEmail(String email, String pass) {
      //  Log.d(TAG, "createUserWithEmail:Email: " + email);
        mAuthProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       // Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(VincularLoginActivity.this, "Login Falhou", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(VincularLoginActivity.this, "Login", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
              //  Log.d(TAG, "getMessage : " + e.getMessage());
                Toast.makeText(VincularLoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        mAuthProgressDialog.hide();
    }


    @Override
    protected void onStart() {
        super.onStart();
       // mAuth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAuth != null) {
          //  mAuth.removeAuthStateListener(stateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
       // Log.d(TAG, "falhou:" + connectionResult.getErrorMessage());
    }

    public void onclickMain(View view) {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                       // Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        Toast.makeText(VincularLoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        if (!task.isSuccessful()) {
                          //  Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(VincularLoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

}
