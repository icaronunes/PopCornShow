package fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.List;

import activity.FilmeActivity;
import activity.TvShowActivity;
import adapter.ListaFilmeAdapter;
import adapter.ListaTvShowAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;


/**
 * Created by icaro on 23/08/16.
 */
public class ListaFavoriteFragment extends Fragment {

    final String TAG = TvShowFragment.class.getName();
    ResponseStatus status;
    int tipo;
    List<TvSeries> tvSeries;
    List<MovieDb> movieDbs;
    RecyclerView recyclerViewFilme;
    RecyclerView recyclerViewTvShow;

    public static Fragment newInstanceMovie(int tipo, List<MovieDb> movie) {
        ListaFavoriteFragment fragment = new ListaFavoriteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.FILME, (Serializable) movie);
        bundle.putInt(Constantes.ABA, tipo);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment newInstanceTvShow(int tvshow, List<TvSeries> tvshows) {
        ListaFavoriteFragment fragment = new ListaFavoriteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.SERIE, (Serializable) tvshows);
        bundle.putInt(Constantes.ABA, tvshow);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);
            movieDbs  = (List<MovieDb>) getArguments().getSerializable(Constantes.FILME);
            tvSeries = (List<TvSeries>) getArguments().getSerializable(Constantes.SERIE);
        }
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
                Log.d("ListaFilmeAdapter", "ListaFilmeAdapter");
                ImageView imageView = (ImageView) view;
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    Palette.Builder builder = new Palette.Builder(bitmap);
                    Palette palette = builder.generate();
                    for (Palette.Swatch swatch : palette.getSwatches()) {
                        intent.putExtra(Constantes.COLOR_TOP, swatch.getRgb());
                    }
                }
                intent.putExtra(Constantes.FILME_ID, tvSeries.get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, tvSeries.get(position).getName());
                startActivity(intent);

            }

            @Override
            public void onClickLong(View view, final int position) {
                final int id = tvSeries.get(position).getId();
                Log.d("OnClick", "Onclick");
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(tvSeries.get(position).getName())
                        .setMessage(getResources().getString(R.string.excluir_filme))
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        status = FilmeService.addOrRemoverFavorite(id, false, TmdbAccount.MediaType.MOVIE);
                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    tvSeries.remove(tvSeries.get(position));
                                                    recyclerViewFilme.getAdapter().notifyItemRemoved(position);
                                                    recyclerViewFilme.getAdapter().notifyItemChanged(position);
                                                }
                                            }
                                        });
                                    }
                                }).start();
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
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                if (drawable != null) {
                    Bitmap bitmap = drawable.getBitmap();
                    Palette.Builder builder = new Palette.Builder(bitmap);
                    Palette palette = builder.generate();
                    for (Palette.Swatch swatch : palette.getSwatches()) {
                        intent.putExtra(Constantes.COLOR_TOP, swatch.getRgb());
                    }
                }
                intent.putExtra(Constantes.TVSHOW_ID, movieDbs.get(position).getId());
                intent.putExtra(Constantes.NOME_TVSHOW, movieDbs.get(position).getTitle());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int position) {
                final int id = movieDbs.get(position).getId();
                Log.d("OnClick", "onClickLong");
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(movieDbs.get(position).getTitle())
                        .setMessage(getResources().getString(R.string.excluir_tvshow))
                        .setNegativeButton("Não", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton("Sim", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        status = FilmeService.addOrRemoverFavorite(id, false, TmdbAccount.MediaType.TV);

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    movieDbs.remove(movieDbs.get(position));
                                                    recyclerViewTvShow.getAdapter().notifyItemRemoved(position);
                                                    recyclerViewTvShow.getAdapter().notifyItemChanged(position);
                                                }
                                            }
                                        });
                                    }
                                }).start();
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
        recyclerViewFilme.setAdapter(new ListaFilmeAdapter(getActivity(),  movieDbs, onclickListerne(), false));

        return view;
    }

    private View getViewTvShow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);// Criar novo layout
        recyclerViewTvShow = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewTvShow.setHasFixedSize(true);
        recyclerViewTvShow.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTvShow.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewTvShow.setAdapter(new ListaTvShowAdapter(getActivity(), tvSeries, onclickTvShowListerne(), false));

        return view;
    }
}
