package fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import activity.SettingsActivity;
import adapter.MovieMainAdapter;
import adapter.TvShowMainAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.TmdbTV;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import listafilmes.activity.FilmesActivity;
import listaserie.activity.TvShowsActivity;
import utils.Constantes;
import utils.UtilsApp;

import static br.com.icaro.filme.R.string.filmes_main;
import static java.util.Arrays.asList;
import static utils.UtilsApp.getLocale;
import static utils.UtilsApp.getTimezone;


/**
 * Created by icaro on 23/08/16.
 */
public class MainFragment extends Fragment {

    final static String TAG = MainFragment.class.getName();
    private List<String> buttonFilme, buttonTvshow;
    private int tipo;
    private TvResultsPage popularTvshow = null, toDay = null;
    private MovieResultsPage popularMovie = null, cinema = null;


    private FirebaseAnalytics mFirebaseAnalytics;
    private RecyclerView recycle_tvshow_popular_main;
    private RecyclerView recycle_tvshowtoday_main;
    private RecyclerView recycle_movie_popular_main;
    private RecyclerView recycle_movieontheair_main;


    public static Fragment newInstance(int informacoes) {
        MainFragment fragment = new MainFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Constantes.INSTANCE.getABA(), informacoes);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        buttonFilme = new ArrayList<>(asList(getString(R.string.now_playing),
                getString(R.string.upcoming), getString(R.string.populares), getString(R.string.top_rated)));
        buttonTvshow = new ArrayList<>(asList(getString(R.string.air_date),
                getString(R.string.today), getString(R.string.populares), getString(R.string.top_rated)));
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.INSTANCE.getABA());
        }
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (UtilsApp.isNetWorkAvailable(getContext())) {
            new MainAsync().execute();
        } else {
            Toast.makeText(getContext(), R.string.no_internet, Toast.LENGTH_LONG).show();
        }

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
                            intent.putExtra(Constantes.INSTANCE.getABA(), R.string.now_playing);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.now_playing);
                            startActivity(intent);

                            break;
                        }

                        case 1: {

                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getABA(), R.string.upcoming);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.upcoming);
                            startActivity(intent);
                            break;
                        }


                        case 2: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Button_Filme");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getABA(), R.string.populares);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.populares);
                            startActivity(intent);
                            break;
                        }

                        case 3: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Button_Filme");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), FilmesActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getABA(), R.string.top_rated);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.top_rated);
                            startActivity(intent);
                            break;
                        }

                    }
                }
            });

            linearLayout.addView(layoutScroll);
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
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.air_date);
                            startActivity(intent);
                            break;
                        }
                        case 1: {
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(),  R.string.today);
                            startActivity(intent);
                            break;
                        }
                        case 2: {
                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(),  R.string.populares);
                            startActivity(intent);
                            break;
                        }

                        case 3: {

                            Bundle bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Button_Tvshow");
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, button.getText().toString());
                            mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            Intent intent = new Intent(getActivity(), TvShowsActivity.class);
                            intent.putExtra(Constantes.INSTANCE.getNAV_DRAW_ESCOLIDO(), R.string.top_rated);
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

        recycle_movie_popular_main = (RecyclerView) view.findViewById(R.id.recycle_movie_popular_main);
        recycle_movieontheair_main = (RecyclerView) view.findViewById(R.id.recycle_movieontheair_main);

        recycle_movie_popular_main.setHasFixedSize(true);
        recycle_movie_popular_main.setItemAnimator(new DefaultItemAnimator());
        recycle_movie_popular_main.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recycle_movieontheair_main.setHasFixedSize(true);
        recycle_movieontheair_main.setItemAnimator(new DefaultItemAnimator());
        recycle_movieontheair_main.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }

    private View getViewTvshow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.tvshow_main, container, false);
        recycle_tvshow_popular_main = (RecyclerView) view.findViewById(R.id.tvshow_popular_main);
        recycle_tvshowtoday_main = (RecyclerView) view.findViewById(R.id.recycle_tvshowtoday_main);

        recycle_tvshow_popular_main.setHasFixedSize(true);
        recycle_tvshow_popular_main.setItemAnimator(new DefaultItemAnimator());
        recycle_tvshow_popular_main.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        recycle_tvshowtoday_main.setHasFixedSize(true);
        recycle_tvshowtoday_main.setItemAnimator(new DefaultItemAnimator());
        recycle_tvshowtoday_main.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        return view;
    }

    private void setScrollTvShowToDay() {
        recycle_tvshowtoday_main.setAdapter(new TvShowMainAdapter(getActivity(), toDay));
    }

    private void setScrollMoviePopular() {
        recycle_movieontheair_main.setAdapter(new MovieMainAdapter(getActivity(), cinema));
    }

    private void setScrollMovieOntheAir() {
        recycle_movie_popular_main.setAdapter(new MovieMainAdapter(getActivity(), popularMovie));
    }

    private void setScrollTvShowPopulares() {
        recycle_tvshow_popular_main.setAdapter(new TvShowMainAdapter(getActivity(), popularTvshow));
    }


    private class MainAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            if (isDetached()) {
                return null;
            }

            boolean idioma_padrao = false;
            try {

                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            } catch (Exception e){
                Crashlytics.logException(e);
            }
            if (idioma_padrao) {
                try {
                    if (UtilsApp.isNetWorkAvailable(getActivity())) {
                        TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
                        TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                        popularTvshow = tmdbTv.getPopular(getLocale(), 1);
                        toDay = tmdbTv.getAiringToday(getLocale(), 1, getTimezone());
                        popularMovie = tmdbMovies.getPopularMovies(getLocale(), 1);
                        cinema = tmdbMovies.getUpcoming(getLocale(), 1);
                    }
                } catch (Exception e) {
                   // Log.d(TAG, e.getMessage());
                    Crashlytics.logException(e);
                    if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                try { // É preciso? o tmdb não retorna 'en' se não houver o idioma?
                     if (UtilsApp.isNetWorkAvailable(getActivity())) {
                        TmdbTV tmdbTv = FilmeService.getTmdbTvShow();
                        TmdbMovies tmdbMovies = FilmeService.getTmdbMovies();
                        popularTvshow = tmdbTv.getPopular("en", 1);
                        toDay = tmdbTv.getAiringToday("en", 1, getTimezone());
                        popularMovie = tmdbMovies.getPopularMovies("en", 1);
                        cinema = tmdbMovies.getUpcoming("en", 1);
                    }
                } catch (Exception e) {
                    Crashlytics.logException(e);
                    if (getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getActivity(), R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    });
                    //Log.d(TAG, e.getMessage());
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            try {
                if (UtilsApp.isNetWorkAvailable(getActivity()) && isAdded()) {
                    if (tipo == R.string.tvshow_main) {
                        setScrollTvShowPopulares();
                        setScrollTvShowToDay();
                    }
                    if (tipo == R.string.filmes_main) {
                        setScrollMoviePopular();
                        setScrollMovieOntheAir();
                    }
                }
            } catch (Exception e){
                Crashlytics.logException(e);
                if (getActivity() != null)
                Toast.makeText(getActivity(), R.string.ops, Toast.LENGTH_SHORT).show();
            }
        }

    }
}
