package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import com.viewpagerindicator.CirclePageIndicator;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.PosterScrollFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static br.com.icaro.filme.R.id.pager;

/**
 * Created by icaro on 12/07/16.
 */

/**
 * Não esta monstrando todos os item da viewpager. Necessario arrumar.
 */

public class PosterActivity extends BaseActivity {
    int id_filme;
    ViewPager viewPager;
    MovieDb movieDb;
    CirclePageIndicator titlePageIndicator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PosterActivity", "onCreate");
        setContentView(R.layout.activity_scroll_poster);
        id_filme = getIntent().getExtras().getInt(Constantes.FILME_ID);
        viewPager = (ViewPager) findViewById(pager);
        titlePageIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        Log.d("PosterActivity", "onCreate ID: " + id_filme);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("PosterActivity", "onStart");
        TesteAsync testeAsync = new TesteAsync();
        testeAsync.execute();
    }


    public class PosterFragment extends FragmentPagerAdapter {


        public PosterFragment(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            Log.d("PosterFragment", "getItem: chegando -> " + getIntent().getExtras().getInt("posicao"));
            Log.d("PosterFragment", "getItem: position -> " + position);
            return new PosterScrollFragment().newInstance(movieDb.getImages(ArtworkType.POSTER).get(position).getFilePath(), movieDb.toString());
        }

        @Override
        public int getCount() {
            return movieDb.getImages(ArtworkType.POSTER).size();

        }
    }

    public class TesteAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("PosterFragment", "doInBackground: -> " + id_filme);
            movieDb = movies.getMovie(id_filme, getString(R.string.IDIOMAS), TmdbMovies.MovieMethod.images);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
            titlePageIndicator.setViewPager(viewPager);
            titlePageIndicator.setCurrentItem(getIntent().getExtras().getInt("posicao"));
        }
    }
}
