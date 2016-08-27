package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ListView;

import adapter.TemporadaAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;

import static info.movito.themoviedbapi.TmdbTvSeasons.SeasonMethod.credits;
import static info.movito.themoviedbapi.TmdbTvSeasons.SeasonMethod.images;

/**
 * Created by icaro on 26/08/16.
 */
public class TemporadaActivity extends BaseActivity {
    int temporada_id;
    int serie_id;
    TvSeason tvSeason;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.temporada_layout);
        temporada_id = getIntent().getIntExtra(Constantes.TEMPORADA_ID, 0);
        serie_id = getIntent().getIntExtra(Constantes.TVSHOW_ID, 0);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_temporada);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        new TMDVAsync().execute();
    }

    private class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            tvSeason = FilmeService.getTmdbTvSeasons().getSeason(serie_id, temporada_id, getString(R.string.IDIOMAS), credits, images);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            recyclerView.setAdapter(new TemporadaAdapter(TemporadaActivity.this, tvSeason, serie_id));
        }
    }
}
