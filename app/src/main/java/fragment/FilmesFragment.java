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

import java.util.List;

import activity.FilmeActivity;
import adapter.FilmesAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;


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

    public FilmesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.abaEscolhida = getArguments().getInt(Constantes.NAV_DRAW_ESCOLIDO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

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

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!UtilsFilme.isNetWorkAvailable(getContext())) {
            Log.d("onActivityCreated", "Sem internet");
            textView.setVisibility(View.VISIBLE);
            textView.setText("SEM INTERNET");
            swipeRefreshLayout.setEnabled(false);
            snack();

        } else {
            TMDVAsync tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
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
            public void onRefresh() { //Verificar se ha internet
                if (UtilsFilme.isNetWorkAvailable(getContext())) {
                   // process.setVisibility(View.VISIBLE);
                    TMDVAsync tmdvAsync = new TMDVAsync();
                    tmdvAsync.execute();
                }
            }
        };
    }

    public class TMDVAsync extends AsyncTask<Void, Void, List<MovieDb>> {


        @Override
        protected void onPreExecute() {
            if (pagina != 1) {
                getView().findViewById(R.id.swipeToRefresh).setEnabled(false);
            }
            super.onPreExecute();
        }

        @Override
        protected List<MovieDb> doInBackground(Void... voids) {
            Log.d("doInBackground", "doInBackground");
            TmdbMovies movies = FilmeService.getTmdbMovies();
            List<MovieDb> dbList = getListaTipo(movies);
            return dbList;
        }


        @Override
        protected void onPostExecute(List<MovieDb> tmdbMovies) {
            Log.d("onPostExecute", "onPostExecute");
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
            recyclerView.setAdapter(new FilmesAdapter(getContext(), movies, onClickListener() ));
            pagina++;
        }

        protected List<MovieDb> getListaTipo(TmdbMovies tmdbMovies) {

            switch (abaEscolhida) {

                case R.string.now_playing: {
                    return tmdbMovies.getNowPlayingMovies("pt-BR", pagina).getResults();
                }

                case R.string.upcoming: {
                    return tmdbMovies.getUpcoming("pt-BR", pagina).getResults();
                }


                case R.string.popular: {
                    return tmdbMovies.getPopularMovies("pt-BR", pagina).getResults();
                }

                case R.string.top_rated: {
                    return tmdbMovies.getTopRatedMovies("pt-BR", pagina).getResults();
                }

            }
            return tmdbMovies.getNowPlayingMovies("pt-BR", pagina).getResults();
        }

    }

    private FilmesAdapter.FilmeOnClickListener onClickListener() {
        return new FilmesAdapter.FilmeOnClickListener() {
            @Override
            public void onClickFilme(View view, int position) {
                Intent intent = new Intent(getActivity(), FilmeActivity.class);
                Log.d("onClickListener", "" + position);
                Log.d("onClickListener", "" + movies.get(position).getTitle());
                intent.putExtra(Constantes.FILME_ID, movies.get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movies.get(position).getTitle());
                getContext().startActivity(intent);
            }
        };
    }

}
