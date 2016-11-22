package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import br.com.icaro.filme.BuildConfig;
import br.com.icaro.filme.R;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 24/06/16.
 */

public class BaseActivity extends AppCompatActivity {

    private static String TAG = BaseActivity.class.getName();
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    int[] drawer = {R.drawable.nav_drawer_header, R.drawable.nav_drawer_header2, R.drawable.nav_drawer_header3,
            R.drawable.nav_drawer_header4, R.drawable.nav_drawer_header5, R.drawable.nav_drawer_header6, R.drawable.nav_drawer_header7};
    ImageView imgUserBackground;
    ImageView imgUserPhoto;
    TextView tUserName;
    TextView tLogin;
    TextView textLogin;
    private FirebaseAnalytics mFirebaseAnalytics;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

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
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    view.setAlpha(1);
                }
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
             view.findViewById(R.id.textLogin);
            imgUserBackground = (ImageView) view.findViewById(R.id.imgUserBackground);
            imgUserPhoto = (ImageView) view.findViewById(R.id.imgUserPhoto);
            Random random = new Random();
            // Log.d("randon", ""+random.nextInt(7));
            imgUserBackground.setImageResource(drawer[random.nextInt(7)]);
            tUserName = (TextView) view.findViewById(R.id.tUserName);
            tLogin = (TextView) view.findViewById(R.id.tLogin);
            textLogin = (TextView) view.findViewById(R.id.textLogin);
            //  Log.d(TAG, "BASEACTIVITY");

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

        validarNavDrawerComLogin();
    }

    private void validarNavDrawerComLogin() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        Menu grupo_login = navigationView.getMenu();


        if (auth.getCurrentUser() == null) {
            textLogin.setText(R.string.fazer_login);
            textLogin.setTextSize(20);
            textLogin.setVisibility(View.VISIBLE);
            imgUserPhoto.setImageResource(R.drawable.add_user);
            grupo_login = navigationView.getMenu();
            grupo_login.removeGroup(R.id.menu_drav_logado);
            imgUserPhoto.setOnClickListener(onClickListenerLogar());

        } else {
                FirebaseUser user = auth.getCurrentUser();
                Log.d(TAG, user.getProviders().get(0));
                switch (user.getProviders().get(0)) {

                    case "google.com": {
                        textLogin.setVisibility(View.VISIBLE);
                        grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
                        tLogin.setText(user.getDisplayName());
                        tUserName.setText(user.getEmail());
                        Picasso.with(getBaseContext()).load(user.getPhotoUrl())
                                .into(imgUserPhoto);
                        imgUserPhoto.setOnClickListener(onClickListenerlogado());
                        //Log.d(TAG, user.getProviders().get(0));

                        break;
                    }

                    case "facebook":{
                        textLogin.setVisibility(View.VISIBLE);
                        grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
                        tLogin.setText(user.getDisplayName());
                        tUserName.setText(user.getEmail());
                        //imgUserPhoto.setImageResource(user.getPhotoUrl().getEncodedPath());
                        imgUserPhoto.setOnClickListener(onClickListenerlogado());

                        //Log.d(TAG, user.getPhotoUrl().getEncodedPath());
                        break;
                    }

                    case "password": {
                        textLogin.setVisibility(View.GONE);
                        grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
                        tLogin.setText(user.getDisplayName());
                        tUserName.setText(user.getEmail());
                        imgUserPhoto.setImageResource(R.drawable.user);
                        imgUserPhoto.setOnClickListener(onClickListenerlogado());

                        Log.d(TAG, "passWORD");
                        break;
                    }


                }
//                if (user.getProviderId().equalsIgnoreCase("firebase")) {
//                    textLogin.setVisibility(View.GONE);
//                    grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
//                    tLogin.setText(user.getDisplayName());
//                    tUserName.setText(user.getEmail());
//                   // imgUserPhoto.setImageResource(user);
//                    imgUserPhoto.setOnClickListener(onClickListenerlogado());
//                }
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
            case R.id.menu_drav_person: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.menu_drav_oscar: {
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

            case R.id.menu_drav_person:
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_PersonPopular");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                intent = new Intent(this, PersonPopularActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_drav_oscar:
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_PersonPopular");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);//
                intent = new Intent(this, OscarActivity.class);
                intent.putExtra(Constantes.LISTA_ID, "28");
                intent.putExtra(Constantes.LISTA_NOME, R.string.oscar);
                startActivity(intent);

                break;

            case R.id.menu_drav_surpresa:
                getParametrosDoRemoteConfig();
                break;
        }
    }

    private void getParametrosDoRemoteConfig() {

        final Intent intent = new Intent(this, ListaGenericaActivity.class);
        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setDeveloperModeEnabled(BuildConfig.DEBUG)
                .build();
        mFirebaseRemoteConfig.setConfigSettings(configSettings);

        mFirebaseRemoteConfig.setDefaults(R.xml.xml_defaults);

        long cacheExpiration = 3600; // 1 hour in seconds.
        // If in developer mode cacheExpiration is set to 0 so each fetch will retrieve values from
        // the server.
        if (mFirebaseRemoteConfig.getInfo().getConfigSettings().isDeveloperModeEnabled()) {
            cacheExpiration = 0;
        }

        mFirebaseRemoteConfig.fetch(cacheExpiration)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            mFirebaseRemoteConfig.activateFetched();

                        } else {

                        }

                        Map<String, String> map = new HashMap<String, String>();
                        map = getListaRemoteConfig();

                        String numero = String.valueOf(new Random().nextInt(10));
                        //Log.d(TAG, "numero : " + numero);

                        intent.putExtra(Constantes.LISTA_ID, map.get("id" + numero));
                        intent.putExtra(Constantes.LISTA_GENERICA, map.get("title" + numero));


                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "NavDrawer_Random:List:" + map.get("id" + numero) + ":" + "title" + numero);
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        startActivity(intent);

                    }
                });
    }

    private Map<String, String> getListaRemoteConfig() {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i <= 9; i++) {
            map.put("id" + i, mFirebaseRemoteConfig.getString("id" + i));
            map.put("title" + i, mFirebaseRemoteConfig.getString("title" + i));
            // Log.d("Log", "numero "+i);
        }

        return map;
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
            //Log.e("salvarArqNaMemoriaIn", "Directory created");
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
        final Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Event.LOGIN, "Tentativa de login");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(BaseActivity.this, LoginActivity.class));
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
                //  Log.d(TAG, "Login");
            }
        };
    }
}