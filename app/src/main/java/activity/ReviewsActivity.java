package activity;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import adapter.ReviewsAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;

public class ReviewsActivity extends BaseActivity {
    int id_filme;
    RecyclerView recyclerView;
    MovieDb movieDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reviews);
        setUpToolBar();
        TMDVAsync tmdvAsync = new TMDVAsync();
        tmdvAsync.execute();
        Log.d("ReviewsActivity", "onCreate");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getIntent().getStringExtra(Constantes.NOME_FILME));
        id_filme = getIntent().getExtras().getInt(Constantes.FILME_ID);
        recyclerView = (RecyclerView) findViewById(R.id.recycleView_reviews);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);


    }

    public class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected MovieDb doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("FilmeBottonFragment", "doInBackground: -> " + id_filme);
            movieDb = movies.getMovie(id_filme, "pt-BR", reviews);
            movieDb.getReviews().addAll(movies.getMovie(id_filme, "en", reviews).getReviews());
            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            if (!movieDb.getReviews().isEmpty())
            recyclerView.setAdapter(new ReviewsAdapter(getBaseContext(), movieDb.getReviews()));
        }
    }

}
