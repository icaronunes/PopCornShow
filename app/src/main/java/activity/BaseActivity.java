package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.io.File;

import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.config.Account;
import utils.Constantes;
import utils.Prefs;
import utils.UtilsFilme;


/**
 * Created by icaro on 24/06/16.
 */

public class BaseActivity extends AppCompatActivity {

    static String TAG = "BaseActivity";
    static Account account = null;
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    ImageView imgUserBackground;
    ImageView imgUserPhoto;
    TextView tUserName;
    TextView tLogin;
    TextView textLogin;
    String user, pass;
    private FirebaseAnalytics mFirebaseAnalytics;

    public static void SnackBar(final View view, String msg) {


        Snackbar.make(view, msg
                , Snackbar.LENGTH_SHORT).setCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar snackbar) {
                super.onShown(snackbar);
                view.setAlpha(0);
            }

            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                super.onDismissed(snackbar, event);
                view.setAlpha(1);
            }
        })
                .show();

    }

    protected void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }
    }

    protected void setupNavDrawer() {
        if (UtilsFilme.isNetWorkAvailable(getApplicationContext())) {
            new TMDVAsync().execute();
        }
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);


        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layoyt);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);


        if (navigationView != null && drawerLayout != null)

        {

            View view = getLayoutInflater().inflate(R.layout.nav_drawer_header, navigationView);
            view.setVisibility(View.VISIBLE);
            imgUserBackground = (ImageView) view.findViewById(R.id.imgUserBackground);
            imgUserPhoto = (ImageView) view.findViewById(R.id.imgUserPhoto);

            imgUserBackground.setImageResource(R.drawable.nav_drawer_header);
            tUserName = (TextView) view.findViewById(R.id.tUserName);
            tLogin = (TextView) view.findViewById(R.id.tLogin);
            textLogin = (TextView) view.findViewById(R.id.textLogin);
            Log.d(TAG, "BASEACTIVITY");

            navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem item) {
                    //Seleciona a Linha
                    item.setChecked(true);
                    //Fecha o menu
                    drawerLayout.closeDrawers();
                    //trata o evento do menu
                    onNavDrawerItemSelected(item);
                    return true;
                }
            });
        }
    }

    protected void setCheckable(int id) {

        switch (id) {

            case R.id.now_playing: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.upcoming: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.popular: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.top_rated: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.menu_drav_home: {
                this.navigationView.setCheckedItem(id);
            }

//            case R.id.list: {
//                this.navigationView.setCheckedItem(id);
//            } //Metoda da API não carrega filmes_main da list.

        }//??????????? Cade os outros?

    }

    private void onNavDrawerItemSelected(MenuItem menuItem) {
        Intent intent;
        Bundle bundle = new Bundle();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        switch (menuItem.getItemId()) {

            case R.id.menu_drav_home:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_MainActivity:menu_drav_home");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constantes.ABA, R.id.menu_drav_home);
                startActivity(intent);
                break;

            case R.id.now_playing:
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_FilmesActivity:now_playing");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, FilmesActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing);
                intent.putExtra(Constantes.ABA, R.id.now_playing);
                startActivity(intent);

                break;
            case R.id.upcoming:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_FilmesActivity:upcoming");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, FilmesActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.upcoming);
                intent.putExtra(Constantes.ABA, R.id.upcoming);
                startActivity(intent);

                break;
            case R.id.popular:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_FilmesActivity:popular");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, FilmesActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.popular);
                intent.putExtra(Constantes.ABA, R.id.popular);
                startActivity(intent);

                break;
            case R.id.top_rated:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_FilmesActivity:top_rated");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, FilmesActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.top_rated);
                intent.putExtra(Constantes.ABA, R.id.top_rated);
                startActivity(intent);

                break;
            case R.id.nav_item_settings:
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_SettingsActivity:item_settings");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.favorite:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_FavoriteActivity:favorite");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                intent = new Intent(this, FavoriteActivity.class);

                startActivity(intent);

                break;
            case R.id.rated:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_RatedActivity:rated");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(this, RatedActivity.class);
                startActivity(intent);
                break;
            case R.id.watchlist:

                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_WatchListActivity:watchlist");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(this, WatchListActivity.class);
                startActivity(intent);
                break;
//            case R.id.list:
//                intent = new Intent(this, ListUserActivity.class);
//                startActivity(intent);
//                Toast.makeText(this, "watchlist", Toast.LENGTH_SHORT).show();
//                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                if (drawerLayout != null) {
                    openDrawer();
                    return true;
                }
                break;
            case R.id.apagar:
                Prefs.apagarLoginSenha(BaseActivity.this, Prefs.LOGIN_PASS);
                FilmeApplication.getInstance().setLogado(false);
                startActivity(new Intent(BaseActivity.this, MainActivity.class));
                break;
            case R.id.serie: {
                Intent intent = new Intent(this, TvShowActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, 62560);
                intent.putExtra(Constantes.NOME_TVSHOW, "Breaking Bad: A Química do Mal");
                intent.putExtra(Constantes.COLOR_TOP, -14663350);
                startActivity(intent);
                break;
            }
            case R.id.filme: {
                Intent intent = new Intent(this, FilmeActivity.class);
                intent.putExtra(Constantes.FILME_ID, 76341);
                intent.putExtra(Constantes.NOME_FILME, "Mad Max: Estrada da Fúria");
                intent.putExtra(Constantes.COLOR_TOP, -14663350);
                startActivity(intent);
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setQueryHint("Procura Filme");
        searchView.setEnabled(false);

        return super.onCreateOptionsMenu(menu);
    }

    protected File salvaImagemMemoriaCache(Context context, String endereco) {
        File file = context.getExternalCacheDir();

        if (!file.exists()) {
            file.mkdir();
            Log.e("salvarArqNaMemoriaIn", "Directory created");
        }
        File dir = new File(file, endereco);
        ImageView imageView = new ImageView(context);
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + endereco).into(imageView);
        BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            UtilsFilme.writeBitmap(dir, bitmap);
        }
        return dir;
    }

    //Abre Menu Lateral
    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected View.OnClickListener onClickListenerLogar() {
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getApplicationContext());
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Event.LOGIN, "Tentativa de login");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Log.d("setupNavDrawer", "Login");
                final Dialog alertDialog = new Dialog(BaseActivity.this);
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.adialog_custom_login);

                Button button = (Button) alertDialog.findViewById(R.id.button_login_ok);
                ImageView tmdb = (ImageView) alertDialog.findViewById(R.id.tmdb);
                final EditText eLogin = (EditText) alertDialog.findViewById(R.id.text_login);
                final EditText ePass = (EditText) alertDialog.findViewById(R.id.text_pass_login);
                int width = getResources().getDimensionPixelSize(R.dimen.popup_width); //Criar os Dimen do layout do login - 300dp - 300dp ??
                int height = getResources().getDimensionPixelSize(R.dimen.popup_height);

                alertDialog.getWindow().setLayout(width, height);

                tmdb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(BaseActivity.this, Site.class);
                        intent.putExtra(Constantes.SITE, "https://www.themoviedb.org/account/signup");
                        startActivity(intent);
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SIGN_UP, "Tentativa de criar login - site TMDB");
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    }
                });

                button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d(TAG, "Adialog Login");
                        final ProgressDialog progressDialog = new ProgressDialog(BaseActivity.this,
                                android.R.style.Theme_Material_Dialog);
                        user = eLogin.getText().toString();
                        pass = ePass.getText().toString();
                        Log.d(TAG, "Login/Pass " + user + " " + pass);//
                        progressDialog.setIndeterminate(true);
                        progressDialog.setMessage("Authenticating...");
                        progressDialog.show();

                        new Thread() {
                            private Intent intent;

                            @Override
                            public void run() {
                                if (FilmeService.getAccount(user, pass) == null) {
                                    Log.d(TAG, "Não logou");
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(BaseActivity.this, R.string.no_login, Toast.LENGTH_SHORT).show();
                                            Bundle bundle = new Bundle();
                                            bundle.putString(FirebaseAnalytics.Event.LOGIN, "Sucesso");
                                            bundle.putString(FirebaseAnalytics.Param.DESTINATION, "MainActivity.class");
                                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                        }
                                    });
                                    progressDialog.dismiss();
                                } else {
                                    Prefs.setString(BaseActivity.this, Prefs.LOGIN, user, Prefs.LOGIN_PASS);
                                    Prefs.setString(BaseActivity.this, Prefs.PASS, pass, Prefs.LOGIN_PASS);
                                    FilmeApplication.getInstance().setAccount(account);
                                    intent = new Intent(BaseActivity.this, MainActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(intent);
                                    finish();
                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.LOGIN, "Sucesso");
                                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, "MainActivity.class");
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                    progressDialog.dismiss();
                                }
                            }
                        }.start();

                        alertDialog.dismiss();
                    }
                });
                alertDialog.show();
            }
        };
    }

    //Fecha Menu Lateral
    protected void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    protected View.OnClickListener onClickListenerlogado() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Login");
            }
        };
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            account = FilmeApplication.getInstance().getAccount();
            user = Prefs.getString(getBaseContext(), Prefs.LOGIN, Prefs.LOGIN_PASS);
            pass = Prefs.getString(getBaseContext(), Prefs.PASS, Prefs.LOGIN_PASS);
            if (account == null && user != null && pass != null) {
                account = FilmeService.getAccount(user, pass);
            }
            Log.d(TAG, "doInBackground - Login");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Menu grupo_login = navigationView.getMenu();
            if (account == null) {
                textLogin.setText(R.string.fazer_login);
                textLogin.setTextSize(20);
                textLogin.setVisibility(View.VISIBLE);
                imgUserPhoto.setImageResource(R.drawable.add_user);
                grupo_login = navigationView.getMenu();
                grupo_login.removeGroup(R.id.menu_drav_logado);
                imgUserPhoto.setOnClickListener(onClickListenerLogar());
                FilmeApplication.getInstance().setLogado(false);

            } else {
                FilmeApplication.getInstance().setLogado(true);
                textLogin.setVisibility(View.VISIBLE);
                grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
                tLogin.setText(account.getUserName());
                tUserName.setText(account.getName());
                imgUserPhoto.setImageResource(R.drawable.user);
                imgUserPhoto.setOnClickListener(onClickListenerlogado());
            }
        }
    }
}