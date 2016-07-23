package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.PosterScrollFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;

/**
 * Created by icaro on 12/07/16.
 */

public class PosterActivity extends BaseActivity {
    int id_filme;
    MovieDb movieDb;
    ViewPager viewPager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("PosterActivity", "onCreate");
        setContentView(R.layout.activity_scroll_poster);
        id_filme = getIntent().getExtras().getInt("id_filme");
        viewPager = (ViewPager) findViewById(R.id.pager);
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
            Log.d("PosterFragment", "getItem");
            return new PosterScrollFragment().newInstance(position, movieDb.getImages().get(position).getFilePath());
        }

        @Override
        public int getCount() {
            Log.d("PosterFragment", "getCount");
            Log.d("PosterFragment", "getCount " + movieDb.getImages().size() );
            return movieDb.getImages().size();
           // return 3;
        }
    }

    public class TesteAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("PosterFragment", "doInBackground: -> " + id_filme);
            MovieDb movieDb = movies.getMovie(id_filme, "en", images);
            Log.d("PosterFragment", String.valueOf("MovieDb: -> " + movieDb == null));
            Log.d("PosterFragment", "doInBackground: Imagens " + movieDb.getImages().size());
            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDbs) {
            movieDb = movieDbs;
            Log.d("PosterFragment", "onPostExecute: Imagens " + movieDb.getImages().size());
            viewPager.setAdapter(new PosterFragment(getSupportFragmentManager()));
        }
    }
}
