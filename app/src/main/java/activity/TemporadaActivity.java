package activity;

import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.util.Locale;

import adapter.TemporadaAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

/**
 * Created by icaro on 26/08/16.
 */
public class TemporadaActivity extends BaseActivity {

    int temporada_id;
    String nome, nome_temporada;
    int serie_id, color;
    TvSeason tvSeason;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporada_layout);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setUpToolBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        temporada_id = getIntent().getIntExtra(Constantes.TEMPORADA_ID, 0);
        serie_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
        color = getIntent().getIntExtra(Constantes.COLOR_TOP, 0);
        nome_temporada = getIntent().getStringExtra(Constantes.NOME);
        getSupportActionBar().setTitle(nome_temporada);
        nome = getIntent().getStringExtra(Constantes.NOME_TVSHOW);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_temporada);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        AdView adview = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        new TMDVAsync().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(TemporadaActivity.this);
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            if (idioma_padrao) {
                tvSeason = FilmeService.getTmdbTvSeasons()
                        .getSeason(serie_id, temporada_id, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry() + ",en,null");
                return null; // ????????
            }else {
                tvSeason = FilmeService.getTmdbTvSeasons()
                        .getSeason(serie_id, temporada_id, ",en,null"); //????
                return null;
                }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(new TemporadaAdapter(TemporadaActivity.this, tvSeason, serie_id, nome, color));
        }
    }
}
