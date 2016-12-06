package fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import activity.FilmeActivity;
import activity.FilmesActivity;
import activity.MainActivity;
import activity.SettingsActivity;
import activity.TvShowActivity;
import activity.TvShowsActivity;
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
import static utils.UtilsFilme.getTimezone;


/**
 * Created by icaro on 23/08/16.
 */
    public class MainFragment extends Fragment {

    final static String TAG = MainActivity.class.getName();
    static List<String> buttonFilme, buttonTvshow;
    int tipo;
    TvResultsPage popularTvshow, toDay;
    MovieResultsPage popularMovie, cinema;

    private FirebaseAnalytics mFirebaseAnalytics;

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
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
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

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Filme");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.now_playing);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.now_playing);
                            startActivity(intent);

                            break;
                        }

                        case 1: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Filme");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.upcoming);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.upcoming);
                            startActivity(intent);
                            break;
                        }


                        case 2: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Filme");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.popular);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.popular);
                            startActivity(intent);
                            break;
                        }

                        case 3: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Filme");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
        final List<TvSeries> tvSeries;
        if (popularTvshow.getResults().size() > 0 & isAdded()) {
            int tamanho = popularTvshow.getResults().size() < 15 ? popularTvshow.getResults().size() : 15;
           // Log.d("MainFragment", "Tamanho " + popularTvshow.getResults().size());
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

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"TvShowPopulares");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.air_date);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.air_date);
                            startActivity(intent);
                            break;
                        }
                        case 1: {
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.today);
                            startActivity(intent);
                            break;
                        }
                        case 2: {
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.ABA, R.id.popular);
                            intent.putExtra(Constantes.NAV_DRAW_ESCOLIDO, R.string.popular);
                            startActivity(intent);
                            break;
                        }

                        case 3: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       // Log.d(TAG, "onCreateView");
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

    private void setScrollTvShowToDay() {

        List<TvSeries> tvSeries;
        if (toDay.getResults().size() > 0 & isAdded()) {
            int tamanho = toDay.getResults().size() < 15 ? toDay.getResults().size() : 15;
          //  Log.d("MainFragment", "Tamanho " + toDay.getResults().size());
            tvSeries = toDay.getResults();
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

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Main_TvShowOntheAir");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
           // Log.d("PersonFragment", "doInBackground");

            if (isDetached()){
                return null;
            }

            if (status) {
                boolean idioma_padrao = false;
                if (isDetached()) {
                    SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
                }
                if (idioma_padrao) {
                    TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
                    TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                    popularTvshow = tmdbTv.getPopular(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
                            //.toLanguageTag()
                            + ",en,null", 1);
                    toDay = tmdbTv.getAiringToday(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
                            //.toLanguageTag()
                            + ",en,null", 1, getTimezone());
                    popularMovie = tmdbMovies.getPopularMovies(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
                            //.toLanguageTag()
                            + ",en,null", 1);
                    cinema = tmdbMovies.getUpcoming(Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry()
                            //.toLanguageTag()
                            + ",en,null", 1);
                }else{
                    TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
                    TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                    popularTvshow = tmdbTv.getPopular("en", 1);
                    toDay = tmdbTv.getAiringToday("en,null", 1, getTimezone());
                    popularMovie = tmdbMovies.getPopularMovies("en", 1);
                    cinema = tmdbMovies.getUpcoming("en", 1);
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (status && isAdded()) {
                if (tipo == R.string.tvshow_main) {
                    setScrollTvShowPopulares();
                    setScrollTvShowToDay();
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

        if (cinema.getResults().size() > 0 && isAdded()) {
            int tamanho = cinema.getResults().size() < 15 ? cinema.getResults().size() : 15;
           // Log.d("MainFragment", "Tamanho " + cinema.getResults().size());
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

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Main_MovieOntheAir");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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

        if (popularMovie.getResults().size() > 0 & isAdded()) {
            int tamanho = popularMovie.getResults().size() < 15 ? popularMovie.getResults().size() : 15;
           // Log.d("MainFragment", "Tamanho " + popularMovie.getResults().size());
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

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT,"Main_MoviePopular");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
