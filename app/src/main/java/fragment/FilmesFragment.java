package fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;

import java.util.List;
import java.util.Locale;

import activity.FilmeActivity;
import adapter.FilmesAdapter;
import br.com.icaro.filme.R;
import domain.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;

import static com.google.android.gms.internal.zzs.TAG;


/**
 * A simple {@link Fragment} subclass.
 */
public class FilmesFragment extends Fragment {

    List<MovieDb> movies = null;
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;
    TextView textView;
    FrameLayout frameLayout;
    ProgressBar process;
    int abaEscolhida;
    int pagina = 1;
    private FirebaseAnalytics mFirebaseAnalytics;

    public FilmesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            if (getActivity().getIntent().getAction() == null) {
                this.abaEscolhida = getArguments().getInt(Constantes.NAV_DRAW_ESCOLIDO);
            } else {
                this.abaEscolhida = Integer.parseInt(getArguments().getString(Constantes.NAV_DRAW_ESCOLIDO));
            }
        }

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_list_filme, container, false);
        textView = (TextView) view.findViewById(R.id.textLayoutFilmes);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycleView);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeToRefresh);
        frameLayout = (FrameLayout) view.findViewById(R.id.frame_list_filme);
        process = (ProgressBar) view.findViewById(R.id.progress);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);

        swipeRefreshLayout.setOnRefreshListener(OnRefreshListener());

        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        recyclerView.setAdapter(new FilmesAdapter(getContext(), movies != null ? movies : null,
                onClickListener()));

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!UtilsFilme.isNetWorkAvailable(getContext())) {
          //  Log.d("onActivityCreated", "Sem internet");
            textView.setVisibility(View.VISIBLE);
            textView.setText("SEM INTERNET");
            swipeRefreshLayout.setEnabled(false);
            snack();

        } else {
            new TMDVAsync().execute();
        }
    }

    protected void snack() {
        Snackbar.make(frameLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getContext())) {
                            textView.setVisibility(View.INVISIBLE);
                            TMDVAsync tmdvAsync = new TMDVAsync();
                            tmdvAsync.execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }


    protected SwipeRefreshLayout.OnRefreshListener OnRefreshListener() {
        return new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (UtilsFilme.isNetWorkAvailable(getContext())) {
                    new TMDVAsync().execute();
                }
            }
        };
    }

    private FilmesAdapter.FilmeOnClickListener onClickListener() {
        return new FilmesAdapter.FilmeOnClickListener() {
            @Override
            public void onClickFilme(View view, int position) {
              //  Log.d("onClickMovieListener", "" + position);
               // Log.d("onClickMovieListener", "" + movies.get(position).getTitle());
                Intent intent = new Intent(getActivity(), FilmeActivity.class);
                intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(view));
                intent.putExtra(Constantes.FILME_ID, movies.get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movies.get(position).getTitle());
                getContext().startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(movies.get(position).getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME,movies.get(position).getTitle() );
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


            }
        };
    }

    private class TMDVAsync extends AsyncTask<Void, Void, List<MovieDb>> {

        @Override
        protected void onPreExecute() {
            if (pagina != 1) {
                getView().findViewById(R.id.swipeToRefresh).setEnabled(false);
            }
            super.onPreExecute();
        }

        @Override
        protected List<MovieDb> doInBackground(Void... voids) {
          //  Log.d("doInBackground", "doInBackground");
            try {
                TmdbMovies movies = FilmeService.getTmdbMovies();
                List<MovieDb> dbList = getListaTipo(movies);
                return dbList;
            } catch (Exception e){
                Log.d(TAG, e.getMessage());
                FirebaseCrash.report(e);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }
            return null;
        }


        @Override
        protected void onPostExecute(List<MovieDb> tmdbMovies) {
           // Log.d("onPostExecute", "onPostExecute");
            process.setVisibility(View.GONE);
            if (tmdbMovies != null && pagina != 1) {
                List<MovieDb> x = movies;
                movies = tmdbMovies;
                for (MovieDb movie : x) {
                    movies.add(movie);
                }
            } else {
                movies = tmdbMovies;
            }
            swipeRefreshLayout.setRefreshing(false);
            swipeRefreshLayout.setEnabled(true);
            recyclerView.setAdapter(new FilmesAdapter(getContext(), movies != null ? movies : null,
                    onClickListener()));
            pagina++;
        }

        protected List<MovieDb> getListaTipo(TmdbMovies tmdbMovies) {
            String language = Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry();
            if (language != null) {
                switch (abaEscolhida) {

                    case R.string.now_playing: {
                            return tmdbMovies.getNowPlayingMovies(language, pagina).getResults();
                    }

                    case R.string.upcoming: {
                        return tmdbMovies.getUpcoming(language, pagina).getResults();
                    }


                    case R.string.populares: {
                        return tmdbMovies.getPopularMovies(language, pagina).getResults();
                    }

                    case R.string.top_rated: {
                        return tmdbMovies.getTopRatedMovies(language, pagina).getResults();
                    }

                }
                return tmdbMovies.getNowPlayingMovies(language, pagina).getResults();
            }
            return null;
        }

    }

}
