package activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
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
 * Created by icaro on 06/11/16.
 */


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 1;
    private final String TAG = this.getClass().getName();
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private EditText email, pass;
    private SignInButton signInButton;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog mAuthProgressDialog;
    private CallbackManager mCallbackManager;

    public LoginActivity() {

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(getBaseContext());
        mAuth = FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getBaseContext());
        setContentView(R.layout.activity_login);
        email = (EditText) findViewById(R.id.login);
        pass = (EditText) findViewById(R.id.pass);

        stateListener = getAuthStateListener();

        setGoogle();
        setFacebook();


        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle("Loading");
        mAuthProgressDialog.setMessage("Authenticating with Firebase...");
        mAuthProgressDialog.setCancelable(false);

    }

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
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
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
                Log.d(TAG, "facebook:onSuccess: " + loginResult.getAccessToken());
                accessFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel ");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError ", error);
            }
        });
    }

    private void accessFacebook(AccessToken accessToken) {
        accessLoginData("facebook",  accessToken.getToken());
    }

    public void onclick(View view) {
        switch (view.getId()) {

            case R.id.facebook: {
                Log.d(TAG, "Facebook");
                LogarFacebook();
                break;
            }
//            case R.id.criar: {
//                criarLoginEmail();
//                break;
//            }
                case R.id.logar: {
                    LogarComEmail();
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

    private void LogarComEmail() {
        mAuth.signInWithEmailAndPassword(email.getText().toString(), pass.getText().toString())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Falhou",
                                    Toast.LENGTH_SHORT).show();
                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "signInWithEmail:failed " + e.getMessage());
            }
        });
    }



    private FirebaseAuth.AuthStateListener getAuthStateListener() {
        FirebaseAuth.AuthStateListener callback = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else {
                    Log.d(TAG, "não logou... ");
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
                Log.d(TAG, account.getDisplayName());
                accessGoogle(account.getIdToken());
            } else {
                Log.d(TAG, "Falha no login Google");
            }
        } else {
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void accessGoogle(String token) {
        accessLoginData("google", token);
    }

    private void accessLoginData( String provider, String... tokens ){
        mAuthProgressDialog.show();
        if( tokens != null
                && tokens.length > 0
                && tokens[0] != null ){

            AuthCredential credential = FacebookAuthProvider.getCredential( tokens[0]);

            Log.d(TAG, "credencial :" + credential.getProvider());
            credential = provider.equalsIgnoreCase("google") ? GoogleAuthProvider.getCredential( tokens[0], null) : credential;

            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if( !task.isSuccessful() ){
                                Toast.makeText(LoginActivity.this,"Login social falhou", Toast.LENGTH_SHORT ).show();
                            }
                            mAuthProgressDialog.dismiss();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, e.getMessage());
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                            mAuthProgressDialog.dismiss();
                        }
                    });
        }
        else{
            mAuth.signOut();
        }

    }


    public void criarLoginEmail() {
        Log.d(TAG, "createUserWithEmail:Email:" + String.valueOf(email.getText()));
        mAuthProgressDialog.show();
        mAuth.createUserWithEmailAndPassword(String.valueOf(email.getText()), String.valueOf(pass.getText()))
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login Falhou", Toast.LENGTH_SHORT).show();
                        }
//                        else {
//                            finish();
//                            startActivity(new Intent(MainActivity.this, LogadoActivity.class));
//                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "getMessage : " + e.getMessage());

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
        if (mAuth != null) {
            mAuth.removeAuthStateListener(stateListener);
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "falhou:" + connectionResult.getErrorMessage());
    }

    public void onclickMain(View view) {
        startActivity(new Intent(this, MainActivity.class));
    }
}
