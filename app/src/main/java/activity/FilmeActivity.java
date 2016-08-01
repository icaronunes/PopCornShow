package activity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import br.com.icaro.filme.R;
import domian.FilmeService;
import fragment.FilmeBottonFragment;
import fragment.ImagemTopScrollFragment;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;

public class FilmeActivity extends BaseActivity {

    ViewPager viewPager;
    private int id_filme;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ProgressBar progressBar;
    private MovieDb movieDb;
    int color_fundo;
    TMDVAsync tmdvAsync;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filme);
        setUpToolBar();
        setupNavDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.NOME_FILME));
        color_fundo = getIntent().getIntExtra(Constantes.COLOR_TOP, R.color.transparent);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        viewPager = (ViewPager) findViewById(R.id.top_img_viewpager);
        viewPager.setBackgroundColor(color_fundo);
        Log.d("color", " "+ color_fundo);


        if (savedInstanceState == null) {
            FilmeBottonFragment filmeFrag = new FilmeBottonFragment();
            Bundle bundle = new Bundle(); //Tentar pegar nome que esta no bundle
            bundle.putInt(Constantes.FILME_ID, getIntent().getExtras().getInt(Constantes.FILME_ID));
            filmeFrag.setArguments(bundle);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.filme_container, filmeFrag, null)
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .commit();
        }
    }

    private void setTitle(String title) {
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        id_filme = getIntent().getIntExtra(Constantes.FILME_ID, 0);
        tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!tmdvAsync.isCancelled()){
            tmdvAsync.cancel(true);
        }

    }

    private class ImagemTopFragment extends FragmentPagerAdapter {

        public ImagemTopFragment(FragmentManager supportFragmentManager) {
            super(supportFragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            if(movieDb.getImages(ArtworkType.BACKDROP) != null) {
                if (position == 0) {
                    return new ImagemTopScrollFragment().newInstance(movieDb.getBackdropPath());
                }
                Log.d("FilmeActivity", "getItem: ->  " + movieDb.getImages(ArtworkType.BACKDROP).get(position).getFilePath());
                return new ImagemTopScrollFragment().newInstance(movieDb.getImages(ArtworkType.BACKDROP).get(position).getFilePath());
            }
            return null;
        }

        @Override
        public int getCount() {
            if (movieDb.getImages(ArtworkType.BACKDROP) != null) {

                int tamanho = movieDb.getImages(ArtworkType.BACKDROP).size();
                Log.d("FilmeActivity", "getCount: ->  "+ tamanho);
                return tamanho > 0 ? tamanho : 1;
            }
            return 0;
        }
    }

    public class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {//
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("FilmeActivity", "doInBackground: -> ID " + id_filme);
            movieDb = movies.getMovie(id_filme, getResources().getString(R.string.IDIOMAS)
                    , images);
            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            viewPager.setAdapter(new ImagemTopFragment(getSupportFragmentManager()));
            progressBar.setVisibility(View.INVISIBLE);
        }
    }

}