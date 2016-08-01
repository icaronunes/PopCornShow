package activity;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.NavigationView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.config.Account;
import utils.Constantes;
import utils.Prefs;

import static br.com.icaro.filme.R.id.watchlist;


/**
 * Created by icaro on 24/06/16.
 */

public class BaseActivity extends AppCompatActivity {


    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;
    Account account;
    ImageView imgUserBackground;
    ImageView imgUserPhoto;
    TextView tUserName;
    TextView tLogin;
    TextView textLogin;
    String user, pass;

    protected final static int getResourceID
            (final String resName, final String resType, final Context ctx) {
        final int ResourceID =
                ctx.getResources().getIdentifier(resName, resType,
                        ctx.getApplicationInfo().packageName);
        if (ResourceID == 0) {
            throw new IllegalArgumentException
                    (
                            "No resource string found with name " + resName
                    );
        } else {
            return ResourceID;
        }

    }


    protected void setUpToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(getResources().getColor(R.color.white));
            setSupportActionBar(toolbar);

        }
    }


    protected void setupNavDrawer() {
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();

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
        }

    }

    private void onNavDrawerItemSelected(MenuItem menuItem) {
        Intent intent;
        switch (menuItem.getItemId()) {
            case R.id.now_playing:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing);
                intent.putExtra(Constantes.ABA, R.id.now_playing);
                startActivity(intent);
                Toast.makeText(this, "click 1", Toast.LENGTH_SHORT).show();
                //Nada aqui, pois somente a mainactivity tem menu lateral
                break;
            case R.id.upcoming:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.upcoming);
                intent.putExtra(Constantes.ABA, R.id.upcoming);
                startActivity(intent);
                Toast.makeText(this, "click 2", Toast.LENGTH_SHORT).show();
                break;
            case R.id.popular:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.popular);
                intent.putExtra(Constantes.ABA, R.id.popular);
                startActivity(intent);
                Toast.makeText(this, "click 4", Toast.LENGTH_SHORT).show();
                break;
            case R.id.top_rated:
                intent = new Intent(this, MainActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.top_rated);
                intent.putExtra(Constantes.ABA, R.id.top_rated);
                startActivity(intent);
                Toast.makeText(this, "top_rated", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_item_settings:
                Toast.makeText(this, "nav_item_settings", Toast.LENGTH_SHORT).show();
                break;
            case R.id.favorite:
                intent = new Intent(this, FavotireActivity.class);
                intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.favorite);
                intent.putExtra(Constantes.ABA, R.id.favorite);
                startActivity(intent);
                Toast.makeText(this, "favorite", Toast.LENGTH_SHORT).show();
                break;
            case R.id.rated:
                Toast.makeText(this, "rated", Toast.LENGTH_SHORT).show();
                break;
            case watchlist:
                Toast.makeText(this, "watchlist", Toast.LENGTH_SHORT).show();
                break;
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
            case R.id.apagar:
                Prefs.apagar(BaseActivity.this, Prefs.LOGIN_PASS);
                startActivity(new Intent(BaseActivity.this, MainActivity.class));
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

    //Abre Menu Lateral
    private void openDrawer() {
        if (drawerLayout != null) {
            drawerLayout.openDrawer(GravityCompat.START);
        }
    }

    //Fecha Menu Lateral
    protected void closeDrawer() {
        if (drawerLayout != null) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    public class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            FilmeService.getTmdbMovies().getLatestMovie();
            account = FilmeService.getAccount(Prefs.getString(BaseActivity.this, Prefs.LOGIN, Prefs.LOGIN_PASS),
                    Prefs.getString(BaseActivity.this, Prefs.PASS, Prefs.LOGIN_PASS));
            Log.d("setupNavDrawer", "TMDVAsync");
            return null;
        }

        protected View.OnClickListener onClickListenerLogar() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Log.d("setupNavDrawer", "Login");
                    final Dialog alertDialog = new Dialog(BaseActivity.this);
                    alertDialog.setContentView(R.layout.adialog_custom_login);
                    Button button = (Button) alertDialog.findViewById(R.id.button_login_ok);
                    final EditText eLogin = (EditText) alertDialog.findViewById(R.id.text_login);
                    final EditText ePass = (EditText) alertDialog.findViewById(R.id.text_pass_login);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d("setupNavDrawer", "Adialog Login");
                            final ProgressDialog progressDialog = new ProgressDialog(BaseActivity.this,
                                    android.R.style.Theme_Material_Dialog);
                            user = eLogin.getText().toString();
                            pass = ePass.getText().toString();
                            Log.d("setupNavDrawer", "Login/Pass " + user + " " + pass);//
                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Authenticating...");
                            progressDialog.show();

                            new Thread() {
                                @Override
                                public void run() {

                                    if (FilmeService.getAccount(user, pass) == null) {
                                        Log.d("setupNavDrawer", "Não logou");
                                        progressDialog.dismiss();
                                    } else {
                                        Prefs.setString(BaseActivity.this, Prefs.LOGIN, user, Prefs.LOGIN_PASS);
                                        Prefs.setString(BaseActivity.this, Prefs.PASS, pass, Prefs.LOGIN_PASS);
                                        startActivity(new Intent(BaseActivity.this, MainActivity.class));

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

        protected View.OnClickListener onClickListenerlogado() {
            return new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("setupNavDrawer", "Login");
                }
            };
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (account == null) {
                textLogin.setText(R.string.fazer_login);
                textLogin.setTextSize(20);
                textLogin.setVisibility(View.VISIBLE);
                imgUserPhoto.setImageResource(R.drawable.add_user);
                Menu grupo_login = navigationView.getMenu();
                grupo_login.removeGroup(R.id.menu_drav_logado);
                imgUserPhoto.setOnClickListener(onClickListenerLogar());

            } else {
                textLogin.setVisibility(View.VISIBLE);
                tLogin.setText(account.getUserName());
                tUserName.setText(account.getName());
                imgUserPhoto.setImageResource(R.drawable.user);
                imgUserPhoto.setOnClickListener(onClickListenerlogado());
            }
        }
    }
}