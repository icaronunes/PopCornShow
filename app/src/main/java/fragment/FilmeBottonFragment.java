package fragment;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.credits;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.images;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.releases;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.similar;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.videos;


/**
 * Created by icaro on 03/07/16.
 */

public class FilmeBottonFragment extends Fragment {

    TextView titulo, categoria, time_filme, descricao, voto_media, voto_quantidade;
    ImageView img_poster;
    int id_filme;
    MovieDb movieDb;

    //************* Alguns metodos senco chamados 2 vezes

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            id_filme = getArguments().getInt(Constantes.FILME_ID);
            Log.d("FilmeBottonFragment", "onCreate -> " + id_filme);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_filme, container, false);

        titulo = (TextView) view.findViewById(R.id.titulo_text);
        categoria = (TextView) view.findViewById(R.id.categoria_filme);
        time_filme = (TextView) view.findViewById(R.id.time_filme);
        descricao = (TextView) view.findViewById(R.id.descricao);
        voto_media = (TextView) view.findViewById(R.id.voto_media);
        voto_quantidade = (TextView) view.findViewById(R.id.voto_quantidade);
        img_poster = (ImageView) view.findViewById(R.id.img_poster);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("FilmeBottonFragment", "onActivityCreated -> " + id_filme);
        if (id_filme != 0) {
            TMDVAsync tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
        }

    }

    public String getGeneros(final MovieDb movieDb) {
        List<Genre> genres = movieDb.getGenres();
        StringBuilder stringBuilder = new StringBuilder("");
        Log.d("getGeneros", "" + genres.size());
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                stringBuilder.append(genre.getName() + " ");
                Log.d("Genero", " " + genre.getName());
            }
        }
        return stringBuilder.toString();
    }

    public String getSinopse(final MovieDb movieDb) {

        if (movieDb.getOverview() != null) {

            return movieDb.getOverview();
        }
        return getResources().getString(R.string.sem_sinopse);
    }

    private String getTempoFilme(MovieDb movieDb) {
        if (movieDb.getRuntime() != 0) {
            return String.valueOf(movieDb.getRuntime() + " " + getResources().getString(R.string.minutos));
        }
        return getResources().getString(R.string.não_informado);
    }


    public void setPoster(MovieDb poster) {
        String urlBase = "http://image.tmdb.org/t/p/";
        final StringBuilder stringBuilder = new StringBuilder(urlBase);
        stringBuilder.append("/")
                .append("w185");
        Log.d("setPoster", stringBuilder + movieDb.getPosterPath());
        Picasso.with(getContext()).load(stringBuilder + movieDb.getPosterPath()).into(img_poster);
    }


    public class TMDVAsync extends AsyncTask<Void, Void, MovieDb> {

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected MovieDb doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("FilmeBottonFragment", "doInBackground: -> " + id_filme);
            movieDb = movies.getMovie(id_filme, Constantes.PORTUGUES, credits, videos, releases, images, similar, reviews);
            Log.d("FilmeBottonFragment", "doInBackground: <-> " + movieDb.toString());

            return movieDb;
        }

        @Override
        protected void onPostExecute(MovieDb movieDb) {
            super.onPostExecute(movieDb);
            titulo.setText(movieDb.getTitle());
            categoria.setText(getGeneros(movieDb));
            time_filme.setText(getTempoFilme(movieDb));
            voto_media.setText(Float.toString(movieDb.getVoteAverage()));
            Log.d("voto_media", ""+movieDb.getVoteCount());
            voto_quantidade.setText(String.valueOf(movieDb.getVoteCount()));
            descricao.setText(getSinopse(movieDb));
            setPoster(movieDb);

        }

    }


}
