package activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import applicaton.PopCornViewModelFactory;
import avaliado.RatedActivity;
import br.com.icaro.filme.BuildConfig;
import br.com.icaro.filme.R;
import busca.SearchMultiActivity;
import configuracao.SettingsActivity;
import domain.busca.MultiSearch;
import favorito.FavoriteActivity;
import login.LoginActivity;
import main.MainActivity;
import oscar.OscarActivity;
import pessoaspopulares.PersonPopularActivity;
import queroassistir.WatchListActivity;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;
import seguindo.SeguindoActivity;
import utils.Api;
import utils.Constant;
import utils.UtilsApp;
import utils.UtilsKt;

/**
 * Created by icaro on 24/06/16.
 */
@SuppressLint("Registered")
@Keep
public class BaseActivity extends AppCompatActivity implements LifecycleOwner {
    //todo  Refatorar para criar o metodo snack() para enviar uma funcao com o getDados no hight ordem
    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    private ImageView imgUserPhoto;
    private TextView tUserName;
    private TextView tLogin;
    private TextView textLogin;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;
    private Dialog dialog;
    private CompositeSubscription subscriptions = new CompositeSubscription();

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    protected <T extends AndroidViewModel> T createViewModel(Class<T> classViewModel,@NonNull Activity activity) {
        PopCornViewModelFactory model = new PopCornViewModelFactory(this.getApplication(), new Api(getBaseContext()), activity);
        return ViewModelProviders.of(this, model).get(classViewModel);
    }

    public void SnackBar(final View view, String msg) {
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
        }).show();
    }

    static public String getLocale() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Locale.getDefault().toLanguageTag();
        } else {
            return Locale.getDefault().getLanguage() + "-" + Locale.getDefault().getCountry();
        }
    }

    protected void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);
        }
    }

    protected void setAdMob(AdView adView) {
        UtilsKt.setAdMob(adView);
    }

    public void hideSoftKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    protected void setupNavDrawer() {

        final ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
        actionBar.setDisplayHomeAsUpEnabled(true);

        drawerLayout = findViewById(R.id.drawer_layoyt);
        navigationView = findViewById(R.id.nav_view);
        navigationView.setItemIconTintList(null);


        if (navigationView != null && drawerLayout != null) {

            View view = getLayoutInflater().inflate(R.layout.nav_drawer_header, navigationView);
            view.setVisibility(View.VISIBLE);
            view.findViewById(R.id.textLogin);
            imgUserPhoto = view.findViewById(R.id.imgUserPhoto);

            tUserName = view.findViewById(R.id.tUserName);
            tLogin = view.findViewById(R.id.tLogin);
            textLogin = view.findViewById(R.id.textLogin);


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

            if (user.isAnonymous()) {
                textLogin.setVisibility(View.VISIBLE);
                grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
                tLogin.setText(R.string.anonymous);
                tUserName.setText(R.string.criar_login_popcorn);
                imgUserPhoto.setImageResource(R.drawable.add_user);
            } else {
                if (!user.getProviderData().isEmpty()) {
                    textLogin.setVisibility(View.VISIBLE);
                    grupo_login.setGroupVisible(R.id.menu_drav_logado, true);
                    tUserName.setText(user.getDisplayName() != null ? user.getDisplayName() : "");
                    tLogin.setText(user.getEmail() != null ? user.getEmail() : "");
                    Picasso.get().load(user.getPhotoUrl())
                            .placeholder(R.drawable.person)
                            .into(imgUserPhoto);
                }
            }
        }
    }


    protected void setCheckable(int id) {

        switch (id) {

            case R.id.menu_drav_home: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.menu_drav_person: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.menu_drav_oscar: {
                this.navigationView.setCheckedItem(id);
            }
            case R.id.seguindo: {
                this.navigationView.setCheckedItem(id);
            }
        }
    }

    private void onNavDrawerItemSelected(MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {

            case R.id.menu_drav_home:

                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constant.ABA, R.id.menu_drav_home);
                startActivity(intent);
                break;

            case R.id.nav_item_settings:

                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                break;
            case R.id.favorite:

                intent = new Intent(this, FavoriteActivity.class);
                startActivity(intent);

                break;
            case R.id.rated:

                intent = new Intent(this, RatedActivity.class);
                startActivity(intent);
                break;
            case R.id.watchlist:

                intent = new Intent(this, WatchListActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_drav_person:
                intent = new Intent(this, PersonPopularActivity.class);
                startActivity(intent);
                break;

            case R.id.menu_drav_oscar:

                intent = new Intent(this, OscarActivity.class);
                intent.putExtra(Constant.LISTA_ID, getResources().getString(R.string.id_oscar));
                intent.putExtra(Constant.LISTA_NOME, R.string.oscar);
                startActivity(intent);
                break;

            case R.id.menu_drav_surpresa:
                getParametrosDoRemoteConfig();
                break;

            case R.id.seguindo:

                intent = new Intent(this, SeguindoActivity.class);
                intent.putExtra(Constant.LISTA_ID, BuildConfig.VERSION_CODE - 1);
                intent.putExtra(Constant.LISTA_NOME, R.string.oscar);
                startActivity(intent);
                break;

        }
    }

    private void getParametrosDoRemoteConfig() {

        final Intent intent = new Intent(this, ListGenericActivity.class);
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
                        }
                        Map<String, String> map = new HashMap<String, String>();
                        map = getListaRemoteConfig();

                        String numero = String.valueOf(new Random().nextInt(10));
                        //Log.d(TAG, "numero : " + numero);
                        //TODO mandar somente o Map e fazer a troca na outra activity
                        intent.putExtra(Constant.LISTA_ID, map.get("id" + numero));
                        intent.putExtra(Constant.LISTA_GENERICA, map.get("title" + numero));
                        intent.putExtra(Constant.BUNDLE, (Serializable) map);

                        startActivity(intent);

                    }
                });
    }

    private Map<String, String> getListaRemoteConfig() {
        Map<String, String> map = new HashMap<String, String>();

        for (int i = 0; i <= 9; i++) {
            map.put("id" + i, mFirebaseRemoteConfig.getString("id" + i));
            map.put("title" + i, mFirebaseRemoteConfig.getString("title" + i));
        }
        return map;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home: {
                if (drawerLayout != null) {
                    openDrawer();
                    return true;
                }
                break;
            }

            case R.id.search: {
                if (dialog == null) {
                    View view = getLayoutInflater().inflate(R.layout.layout_search_multi, null);
                    dialog = new AlertDialog.Builder(BaseActivity.this, android.R.style.Theme_Translucent_NoTitleBar_Fullscreen)
                            .setView(view)
                            .setCancelable(true)
                            .create();
                }


                if (item.getIcon() != null) item.getIcon().setAlpha(10);

                dialog.show();

                SearchView searchViewDialog = dialog.findViewById(R.id.layout_search_multi_search);

                searchViewDialog.setIconifiedByDefault(false);
                searchViewDialog.setFocusable(true);
                searchViewDialog.setIconified(false);
                searchViewDialog.requestFocusFromTouch();

                searchViewDialog.setOnCloseListener(() -> {
                    if (dialog != null && dialog.isShowing())
                        dialog.dismiss();
                    return false;
                });

                dialog.setOnDismissListener(dialog -> {
                    getWindow().setSoftInputMode(
                            WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                    if (item.getIcon() != null) item.getIcon().setAlpha(255);
                });

                searchViewDialog.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        if (item.getIcon() != null) item.getIcon().setAlpha(255);
                        Intent intent = new Intent(BaseActivity.this, SearchMultiActivity.class);
                        intent.putExtra(SearchManager.QUERY, query);
                        startActivity(intent);

                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        if (newText.isEmpty() || newText.length() < 2) return false;

                        Subscription inscricao = new Api(BaseActivity.this)
                                .procuraMulti(newText)
                                .distinctUntilChanged()
                                .debounce(1200, TimeUnit.MILLISECONDS)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<MultiSearch>() {
                                    @Override
                                    public void onCompleted() {

                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        Toast.makeText(BaseActivity.this, getString(R.string.ops), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onNext(MultiSearch multiRetorno) {
                                        RecyclerView recyclerView = dialog.findViewById(R.id.layout_search_multi_recycler);
                                        recyclerView.setHasFixedSize(true);
                                        recyclerView.setLayoutManager(new LinearLayoutManager(getApplication()));
                                        recyclerView.setAdapter(new MultiSearchAdapter(BaseActivity.this, multiRetorno, item.getIcon()));
                                    }
                                });

                        subscriptions.add(inscricao);
                        return false;
                    }
                });
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);//
        return true;
    }

    @Override
    protected void onDestroy() {
        subscriptions.unsubscribe();
        super.onDestroy();
    }

    protected void salvaImagemMemoriaCache(final Context context, final String endereco, final SalvarImageShare callback) {

        final ImageView imageView = new ImageView(context);
        Picasso.get().load(UtilsApp.INSTANCE.getBaseUrlImagem(4) + endereco).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                File file = context.getExternalCacheDir();
                if (file != null)
                    if (!file.exists()) {
                        file.mkdir();

                    }
                File dir = new File(file, endereco);
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    UtilsApp.INSTANCE.writeBitmap(dir, bitmap);
                }
                callback.retornaFile(dir);
            }

            @Override
            public void onError(Exception e) {
                callback.RetornoFalha();
            }
        });
    }

    //Abre Menu Lateral
    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    protected View.OnClickListener onClickListenerLogar() {
        return view -> startActivity(new Intent(BaseActivity.this, LoginActivity.class));
    }

    //Fecha Menu Lateral
    protected void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }

    }

    public boolean validatePassword(String password) {
        return password.length() > 5;
    }

    protected boolean getIdioma() {
        try {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            return sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
        } catch (Exception e) {
            Crashlytics.logException(e);
            return false;
        }
    }

    public interface SalvarImageShare {
        void retornaFile(File file);

        void RetornoFalha();
    }
}