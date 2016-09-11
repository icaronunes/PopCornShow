package fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import activity.FilmeActivity;
import activity.FilmesActivity;
import activity.MainActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.filmes_main;
import static java.util.Arrays.asList;


/**
 * Created by icaro on 23/08/16.
 */
public class MainFragment extends Fragment {

    final static String TAG = MainActivity.class.getName();
    static List<String> buttonFilme, buttonTvshow;
    int tipo;
    TvResultsPage popularTvshow, onTheAirTvshow;
    MovieResultsPage popularMovie, cinema;

    public static Fragment newInstance(int informacoes) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constantes.ABA, informacoes);
        fragment.setArguments(bundle);

        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonFilme = new ArrayList<>(asList(getString(R.string.now_playing),
                getString(R.string.upcoming), getString(R.string.popular), getString(R.string.top_rated)));
        buttonTvshow = new ArrayList<>(asList(getString(R.string.air_date),
                getString(R.string.today), getString(R.string.popular), getString(R.string.top_rated)));
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);

        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new MainAsync().execute();

        if (tipo == R.string.tvshow_main) {
            setScrollTvshowButton();
        }
        if (tipo == R.string.filmes_main) {
            setScrollFilmeButton();
        }

    }

    private void setScrollFilmeButton() {
        for (int i = 0; i <= 3; i++) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.main_botton, (ViewGroup) getView(), false);
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_filme_button_main);
            View layoutScroll = view.findViewById(R.id.layout_main_button);

            final Button button = (Button) view.findViewById(R.id.button_main);
            button.setText(buttonFilme.get(i));

            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (finalI) {

                        case 0: {
                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.now_playing);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing);
                            startActivity(intent);
                            break;
                        }

                        case 1: {
                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.upcoming);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.upcoming);
                            startActivity(intent);
                            break;
                        }


                        case 2: {
                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.popular);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.popular);
                            startActivity(intent);
                            break;
                        }

                        case 3: {
                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.top_rated);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.top_rated);
                            startActivity(intent);
                            break;
                        }

                    }
                }
            });

            linearLayout.addView(layoutScroll);
        }

    }

    private void setScrollTvShowPopulares() {
        List<TvSeries> tvSeries;
        if (popularTvshow.getResults().size() > 0) {
            int tamanho = popularTvshow.getResults().size() < 15 ? popularTvshow.getResults().size() : 15;
            Log.d("MainFragment", "Tamanho " + popularTvshow.getResults().size());
            tvSeries = popularTvshow.getResults();
            for (int i = 0; i < tamanho; i++) {
                final TvSeries series = tvSeries.get(i);
                View view = getActivity().getLayoutInflater().inflate(R.layout.poster_main, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_tvshow_popular_main);
                View layoutScroll = view.findViewById(R.id.layout_poster_main);

                final ImageView poster = (ImageView) view.findViewById(R.id.img_poster_grid);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_poster_grid);
                final TextView title = (TextView) view.findViewById(R.id.title_main);

                Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + series.getPosterPath())
                        .error(R.drawable.poster_empty)
                        .into(poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                                title.setText(series.getName());
                                title.setVisibility(View.VISIBLE);
                            }
                        });

                poster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TvShowActivity.class);
                        intent.putExtra(Constantes.NOME_TVSHOW, series.getName());
                        intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                        intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(poster));
                        startActivity(intent);
                    }
                });

                linearLayout.addView(layoutScroll);
            }

        }

    }


    private void setScrollTvshowButton() {
        for (int i = 0; i <= 3; i++) {
            View view = getActivity().getLayoutInflater().inflate(R.layout.main_botton, (ViewGroup) getView(), false);
            LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_tvshow_button_main);
            View layoutScroll = view.findViewById(R.id.layout_main_button);

            final Button button = (Button) view.findViewById(R.id.button_main);
            button.setText(buttonTvshow.get(i));

            final int finalI = i;
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (finalI) {

                        case 0: {
                            Toast.makeText(getActivity(), button.getText(), Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
//                            intent.putExtra(Constantes.ABA, R.id.now_playing);
//                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.id.now_playing);
//                            startActivity(intent);
                            break;
                        }

                        case 1: {
                            Toast.makeText(getActivity(), button.getText(), Toast.LENGTH_SHORT).show();
//                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
//                            intent.putExtra(Constantes.ABA, R.id.upcoming);
//                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.id.upcoming);
//                            startActivity(intent);
                            break;
                        }


                        case 2: {
                            Toast.makeText(getActivity(), button.getText(), Toast.LENGTH_SHORT).show();
                            break;
                        }

                        case 3: {
                            Toast.makeText(getActivity(), button.getText(), Toast.LENGTH_SHORT).show();
                            break;
                        }

                    }
                }
            });

            linearLayout.addView(layoutScroll);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        switch (tipo) {

            case filmes_main: {
                return getViewMovie(inflater, container);
            }
            case R.string.tvshow_main: {
                return getViewTvshow(inflater, container);
            }
        }
        return null;
    }

    private View getViewMovie(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.filmes_main, container, false);

        return view;
    }

    private View getViewTvshow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.tvshow_main, container, false);
        return view;

    }

    private void setScrollTvShowOntheAir() {

        List<TvSeries> tvSeries;
        if (onTheAirTvshow.getResults().size() > 0) {
            int tamanho = onTheAirTvshow.getResults().size() < 15 ? onTheAirTvshow.getResults().size() : 15;
            Log.d("MainFragment", "Tamanho " + onTheAirTvshow.getResults().size());
            tvSeries = onTheAirTvshow.getResults();
            for (int i = 0; i < tamanho; i++) {
                final TvSeries series = tvSeries.get(i);
                View view = getActivity().getLayoutInflater().inflate(R.layout.poster_main, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_tvshow_ontheair_main);
                View layoutScroll = view.findViewById(R.id.layout_poster_main);


                final ImageView poster = (ImageView) view.findViewById(R.id.img_poster_grid);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_poster_grid);
                final TextView title = (TextView) view.findViewById(R.id.title_main);

                Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + series.getPosterPath())
                        .error(R.drawable.poster_empty)
                        .into(poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                                title.setText(series.getName());
                                title.setVisibility(View.VISIBLE);
                            }
                        });

                poster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), TvShowActivity.class);
                        intent.putExtra(Constantes.NOME_TVSHOW, series.getName());
                        intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                        intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(poster));
                        startActivity(intent);
                    }
                });

                linearLayout.addView(layoutScroll);
            }

        }

    }

    private class MainAsync extends AsyncTask<Void, Void, Void> {
        boolean status = false;

        @Override
        protected void onPreExecute() {
            if (UtilsFilme.isNetWorkAvailable(getContext())) {
                status = true;
            }
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("PersonFragment", "doInBackground");
            if (status) {
                TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
                TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                popularTvshow = tmdbTv.getPopular("pt-BR", 1);
                onTheAirTvshow = tmdbTv.getOnTheAir("pt-BR", 1);
                popularMovie = tmdbMovies.getPopularMovies("pt-BR", 1);
                cinema = tmdbMovies.getNowPlayingMovies("pt-BR", 1);

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (status) {
                if (tipo == R.string.tvshow_main) {
                    setScrollTvShowPopulares();
                    setScrollTvShowOntheAir();
                }
                if (tipo == R.string.filmes_main) {
                    setScrollMoviePopular();
                    setScrollMovieOntheAir();
                }
            }
        }

    }

    private void setScrollMovieOntheAir() {
        List<MovieDb> movie;

        if (cinema.getResults().size() > 0) {
            int tamanho = cinema.getResults().size() < 15 ? cinema.getResults().size() : 15;
            Log.d("MainFragment", "Tamanho " + cinema.getResults().size());
            movie = cinema.getResults();
            for (int i = 0; i < tamanho; i++) {
                final MovieDb movieDb = movie.get(i);
                View view = getActivity().getLayoutInflater().inflate(R.layout.poster_main, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_filme_ontheair_main);
                View layoutScroll = view.findViewById(R.id.layout_poster_main);

                final ImageView poster = (ImageView) view.findViewById(R.id.img_poster_grid);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_poster_grid);
                final TextView title = (TextView) view.findViewById(R.id.title_main);

                Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + movieDb.getPosterPath())
                        .error(R.drawable.poster_empty)
                        .into(poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                                title.setText(movieDb.getTitle());
                                title.setVisibility(View.VISIBLE);
                            }
                        });

                poster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), FilmeActivity.class);
                        intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                        intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(poster));
                        startActivity(intent);
                    }
                });

                linearLayout.addView(layoutScroll);
            }

        }

    }

    private void setScrollMoviePopular() {
        List<MovieDb> movie;

        if (popularMovie.getResults().size() > 0) {
            int tamanho = popularMovie.getResults().size() < 15 ? popularMovie.getResults().size() : 15;
            Log.d("MainFragment", "Tamanho " + popularMovie.getResults().size());
            movie = popularMovie.getResults();
            for (int i = 0; i < tamanho; i++) {
                final MovieDb movieDb = movie.get(i);
                View view = getActivity().getLayoutInflater().inflate(R.layout.poster_main, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_filme_popular_main);
                View layoutScroll = view.findViewById(R.id.layout_poster_main);

                final ImageView poster = (ImageView) view.findViewById(R.id.img_poster_grid);
                final ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progress_poster_grid);
                final TextView title = (TextView) view.findViewById(R.id.title_main);

                Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + movieDb.getPosterPath())
                        .error(R.drawable.poster_empty)
                        .into(poster, new Callback() {
                            @Override
                            public void onSuccess() {
                                progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                progressBar.setVisibility(View.GONE);
                                title.setText(movieDb.getTitle());
                                title.setVisibility(View.VISIBLE);
                            }
                        });

                poster.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getActivity(), FilmeActivity.class);
                        intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                        intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(poster));
                        startActivity(intent);
                    }
                });

                linearLayout.addView(layoutScroll);
            }

        }
    }

}
