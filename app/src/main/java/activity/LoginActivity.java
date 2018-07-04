package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
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
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.Arrays;

import br.com.icaro.filme.R;
import utils.UtilsApp;

/**
 * Created by icaro on 06/11/16.
 */


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private final String TAG = this.getClass().getName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private EditText email, pass;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mAuthProgressDialog;
    private CallbackManager mCallbackManager;
    private FirebaseAnalytics mFirebaseAnalytics;

    public LoginActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        FirebaseApp.initializeApp(getBaseContext());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getBaseContext());
        setContentView(R.layout.activity_login);
        UtilsApp.hideSoftKeyboard(this);
        email = (EditText) findViewById(R.id.login);
        pass = (EditText) findViewById(R.id.pass);
        TextView recuperar = (TextView) findViewById(R.id.recuperar_senha);

        stateListener = getAuthStateListener();

        hideSoftKeyboard();

       // setGoogle();
        setFacebook();

        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with PopCorn...");
        mAuthProgressDialog.setCancelable(false);

        TextView criar_login = (TextView) findViewById(R.id.vincular_login);
        criar_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
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

        recuperar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final AlertDialog dialog = new AlertDialog.Builder(LoginActivity.this)
                        .setView(R.layout.recuperar_senha_layout)
                        .create();
                dialog.show();

                Button ok = (Button) dialog.findViewById(R.id.bt_recuperar_senha);
                Button cancel = (Button) dialog.findViewById(R.id.bt_recuperar_cancel);


                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
                    }
                });

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TextInputLayout editText = (TextInputLayout) dialog.findViewById(R.id.ed_email_recuperar);
                        final String email = editText.getEditText().getText().toString();
                       // Log.d(TAG, email);
                        if (email.contains("@") && email.contains(".")){
                            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Toast.makeText(LoginActivity.this, getResources().getString(R.string.email_recuperacao_enviado), Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        } else {
                            Toast.makeText(LoginActivity.this, getResources().getString(R.string.email_invalido), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }

    private void hideSoftKeyboard() {
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );
    }


    private boolean validarParametros(TextInputLayout login, TextInputLayout senha, TextInputLayout repetirSenha) {
        String Slogin, Ssenha, Srepetir;
        Slogin = login.getEditText().getText().toString();
        Ssenha = senha.getEditText().getText().toString();
        Srepetir = repetirSenha.getEditText().getText().toString();

        if (Slogin.contains("@") && Slogin.contains(".")){
            if (Ssenha.equals(Srepetir) && Ssenha.length() > 6){
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
        signInButton.setSize(SignInButton.COLOR_LIGHT);
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

            case R.id.logar: {
                LogarComEmail();
                break;
            }
            case R.id.facebook: {
                logarFacebook();
                break;
            }
            case R.id.bt_anonimous:{
                logarAnonimous();
                break;
            }
        }
    }


    private void logarFacebook() {
        LoginManager
                .getInstance()
                .logInWithReadPermissions(
                        this,
                        Arrays.asList("public_profile", "email")
                );
    }

    private void LogarComEmail() {
        if (pass.getText().toString().length() > 4 && email.getText().toString().length() > 4) {
            mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            //  Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            //   Log.d(TAG, "signInWithEmail:onComplete: " + email.getText().toString() + " " + pass.getText().toString());
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,  R.string.ops,
                                        Toast.LENGTH_SHORT).show();
                            }


                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //  Log.w(TAG, "signInWithEmail:failed " + e.getMessage());
                }
            });
        } else {
            Toast.makeText(LoginActivity.this, R.string.ops,
                    Toast.LENGTH_SHORT).show();
        }
    }


    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    logUser();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.d(TAG, "não logou... ");
                }
            }
        };

        return callback;
    }

    //Crash
    private void logUser() {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        if (mAuth.getCurrentUser() != null) {
            Crashlytics.setUserIdentifier(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "");
            Crashlytics.setUserEmail(mAuth.getCurrentUser().getEmail());
            Crashlytics.setUserName(mAuth.getCurrentUser().getDisplayName());
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            super.onActivityResult(requestCode, resultCode, data);
            // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
            //  Log.d(TAG, "Google Result");
            if (requestCode == RC_SIGN_IN) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                if (result.isSuccess()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "google");
                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                    // Google Sign In was successful, authenticate with Firebase
                    GoogleSignInAccount account = result.getSignInAccount();
                    // Log.d(TAG, account.getDisplayName());
                    accessGoogle(account.getIdToken());
                } else {
                    // Log.d(TAG, "Falha no login Google");
                }
            } else {
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "facebook");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
            }
        } catch (Exception E){
            Toast.makeText(this, R.string.ops, Toast.LENGTH_SHORT).show();
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

           // Log.d(TAG, "credencial :" + credential.getProvider());
          //  Log.d(TAG, "credencial :" + provider);
            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential(tokens[0], null) : credential;
         //   Log.d(TAG, "credencial :" + credential.getProvider());
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this,  R.string.ops, Toast.LENGTH_SHORT ).show();
                            }

                            mAuthProgressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                         //   Log.d(TAG, e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            mAuthProgressDialog.dismiss();
                        }
                    });
        } else {
            if (mAuth.getCurrentUser()
                     != null) {
                mAuth.signOut();
            }
        }

    }


    public void criarLoginEmail(String email, String pass) {
      //  Log.d(TAG, "createUserWithEmail:Email: " + email);
        mAuthProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                     //   Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "email");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);

                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this,  R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(LoginActivity.this,  R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
             //   Log.d(TAG, "getMessage : " + e.getMessage());
                Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
        mAuthProgressDialog.hide();
    }


    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(stateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (stateListener != null) {
            mAuth.removeAuthStateListener(stateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
      //  Log.d(TAG, "falhou:" + connectionResult.getErrorMessage());
    }

    public void logarAnonimous() {
        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                      //  Log.d(TAG, "signInAnonymously:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "anonimo");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN, bundle);
                        Toast.makeText(LoginActivity.this, getResources().getString(R.string.anonimo_alerta),
                                Toast.LENGTH_LONG).show();
                        if (!task.isSuccessful()) {
                        //    Log.w(TAG, "signInAnonymously", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
