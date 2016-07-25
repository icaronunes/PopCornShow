package activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import br.com.icaro.filme.R;
import utils.Constantes;


/**
 * Created by icaro on 24/06/16.
 */

public class BaseActivity extends AppCompatActivity {


    protected DrawerLayout drawerLayout;
    protected NavigationView navigationView;

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
            ImageView imgUserBackground = (ImageView) view.findViewById(R.id.imgUserBackground);

            if (imgUserBackground == null) {
                Log.d("ENTROU", "Img null");
            }
            imgUserBackground.setImageResource(R.drawable.nav_drawer_header);


            TextView tUserName = (TextView) view.findViewById(R.id.tUserName);
            if (tUserName == null) {
                Log.d("ENTROU", "Usuario null");
            }
            tUserName.setText("Icaro");

            TextView tUserEmail = (TextView) view.findViewById(R.id.tUserEmail);
            if (tUserEmail == null) {
                Log.d("ERRO", "EMail null");
            }
            tUserEmail.setText("email@email.com");


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
                Toast.makeText(this, "click 5", Toast.LENGTH_SHORT).show();
                break;
            case R.id.nav_item_settings:
                Toast.makeText(this, "click 6", Toast.LENGTH_SHORT).show();

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

    private Context getContext() {
        return this;
    }
}
