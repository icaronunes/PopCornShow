package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Window;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.List;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.PosterScrollFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static br.com.icaro.filme.R.id.art;
import static br.com.icaro.filme.R.id.pager;

/**
 * Created by icaro on 12/07/16.
 */


public class FotoPersonActivity extends BaseActivity {
    int id_foto;
    ViewPager viewPager;
    List<Artwork> artworks;
    CirclePageIndicator titlePageIndicator;
    String nome;
    int position;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        super.onCreate(savedInstanceState);
        Log.d("PosterActivity", "onCreate");
        setContentView(R.layout.activity_scroll_poster);
        id_foto = getIntent().getExtras().getInt(Constantes.PERSON_ID);
        nome = getIntent().getExtras().getString(Constantes.NOME_PERSON);
        viewPager = (ViewPager) findViewById(pager);
        titlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        Log.d("PosterActivity", "onCreate ID: " + id_foto);

    }

    @Override
    protected void onStart() {
        super.onStart();
        new FotoAsync().execute();
    }


    public class PosterFragment extends FragmentPagerAdapter {


        public PosterFragment(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            return new PosterScrollFragment().newInstance(artworks.get(position).getFilePath(), nome);
        }

        @Override
        public int getCount() {
            return artworks.size();

        }
    }

    private class FotoAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            artworks= FilmeService.getTmdbPerson().getPersonImages(id_foto);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
            titlePageIndicator.setViewPager(viewPager);
            titlePageIndicator.setFillColor(R.color.black);
            titlePageIndicator.setCurrentItem(getIntent().getExtras().getInt(Constantes.POSICAO));
        }
    }
}
