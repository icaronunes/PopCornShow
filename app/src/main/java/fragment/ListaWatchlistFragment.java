package fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.google.firebase.analytics.FirebaseAnalytics;

import activity.FilmeActivity;
import activity.TvShowActivity;
import adapter.ListaFilmeAdapter;
import adapter.ListaTvShowAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 23/08/16.
 */
public class ListaWatchlistFragment extends Fragment {

    final String TAG = TvShowFragment.class.getName();
    ResponseStatus status;
    int tipo;
    MovieResultsPage movies;
    TvResultsPage tvSeries;
    RecyclerView recyclerViewFilme;
    RecyclerView recyclerViewTvShow;
    FirebaseAnalytics firebaseAnalytics;

    public static Fragment newInstanceMovie(int tipo, MovieResultsPage series) {
        ListaWatchlistFragment fragment = new ListaWatchlistFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.FILME, series);
        bundle.putInt(Constantes.ABA, tipo);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment newInstanceTvShow(int tvshow, TvResultsPage results) {
        ListaWatchlistFragment fragment = new ListaWatchlistFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.SERIE, results);
        bundle.putInt(Constantes.ABA, tvshow);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);
            movies = (MovieResultsPage) getArguments().getSerializable(Constantes.FILME);
            tvSeries = (TvResultsPage) getArguments().getSerializable(Constantes.SERIE);
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        switch (tipo) {

            case R.string.filme: {
                return getViewMovie(inflater, container);
            }
            case R.string.tvshow: {
                return getViewTvShow(inflater, container);
            }
        }
        return null;
    }

    private ListaFilmeAdapter.ListaOnClickListener onclickListerne() {
        return new ListaFilmeAdapter.ListaOnClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                Intent intent = new Intent(getActivity(), FilmeActivity.class);

                ImageView imageView = (ImageView) view;
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.FILME_ID, movies.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movies.getResults().get(position).getTitle());
                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaFilmeAdapter.ListaOnClickListener:onclick");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movies.getResults().get(position).getTitle());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movies.getResults().get(position).getId());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }

            @Override
            public void onClickLong(View view, final int position) {
                final int id = movies.getResults().get(position).getId();
                Log.d("OnClick", "Onclick");
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(movies.getResults().get(position).getTitle())
                        .setMessage(getResources().getString(R.string.excluir_filme))
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaFilmeAdapter.ListaOnClickListener:onClickLong");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movies.getResults().get(position).getTitle());
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Movie");
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
                                bundle.putString("AlertDialog-WatchList", "Não excluiu");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        status = FilmeService.addOrRemoverWatchList(id, false, TmdbAccount.MediaType.MOVIE);

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    movies.getResults().remove(movies.getResults().get(position));
                                                    recyclerViewFilme.getAdapter().notifyItemRemoved(position);
                                                    recyclerViewFilme.getAdapter().notifyItemChanged(position);
                                                }
                                            }
                                        });
                                    }
                                }).start();
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaFilmeAdapter.ListaOnClickListener:onClickLong");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movies.getResults().get(position).getTitle());
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Movie");
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
                                bundle.putString("AlertDialog-WatchList", "Excluiu Filme");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            }
                        }).show();
            }
        };
    }


    private ListaTvShowAdapter.ListaOnClickListener onclickTvShowListerne() {
        return new ListaTvShowAdapter.ListaOnClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                Intent intent = new Intent(getActivity(), TvShowActivity.class);
                Log.d("OnClick", "Onclick");
                ImageView imageView = (ImageView) view;
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.TVSHOW_ID, tvSeries.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_TVSHOW, tvSeries.getResults().get(position).getName());
                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaTvShowAdapter.ListaOnClickListener:onClick");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeries.getResults().get(position).getName());
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tvshow");
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, tvSeries.getResults().get(position).getId());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }

            @Override
            public void onClickLong(View view, final int position) {
                final int id = tvSeries.getResults().get(position).getId();
                Log.d("OnClick", "onClickLong");
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(tvSeries.getResults().get(position).getName())
                        .setMessage(getResources().getString(R.string.excluir_filme))
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaTvShowAdapter.ListaOnClickListener:onClickLong");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeries.getResults().get(position).getName());
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tvshow");
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
                                bundle.putString("AlertDialog-WatchList", "Não excluiu");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        status = FilmeService.addOrRemoverWatchList(id, false, TmdbAccount.MediaType.TV);

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    tvSeries.getResults().remove(tvSeries.getResults().get(position));
                                                    recyclerViewTvShow.getAdapter().notifyItemRemoved(position);
                                                    recyclerViewTvShow.getAdapter().notifyItemChanged(position);
                                                }
                                            }
                                        });
                                    }
                                }).start();

                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaTvShowAdapter.ListaOnClickListenerr:onClickLong");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeries.getResults().get(position).getName());
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tvshow");
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
                                bundle.putString("AlertDialog_WatchList", "Excluiu Tvshow");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            }
                        }).show();
            }
        };
    }


    private View getViewMovie(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false); // Criar novo layout
        recyclerViewFilme = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewFilme.setHasFixedSize(true);
        recyclerViewFilme.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFilme.setLayoutManager(new GridLayoutManager(getContext(), 2));
        //recyclerViewFilme.setAdapter(new ListaFilmeAdapter(getActivity(), movies, onclickListerne(), false));

        return view;
    }

    private View getViewTvShow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);// Criar novo layout
        recyclerViewTvShow = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewTvShow.setHasFixedSize(true);
        recyclerViewTvShow.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTvShow.setLayoutManager(new GridLayoutManager(getContext(), 2));
       // recyclerViewTvShow.setAdapter(new ListaTvShowAdapter(getActivity(), tvSeries, onclickTvShowListerne(), false));

        return view;
    }
}
