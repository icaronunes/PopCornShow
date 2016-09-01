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

import activity.FilmeActivity;
import activity.TvShowActivity;
import adapter.FavoriteFilmeAdapter;
import adapter.FavoriteTvShowAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbAccount;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.core.ResponseStatus;
import utils.Constantes;

import static android.media.CamcorderProfile.get;


/**
 * Created by icaro on 23/08/16.
 */
public class FavoriteFragment extends Fragment {

    final String TAG = TvShowFragment.class.getName();
    ResponseStatus status;
    int tipo;
    MovieResultsPage movies;
    TvResultsPage tvSeries;
    RecyclerView recyclerViewFilme;
    RecyclerView recyclerViewTvShow;
    FavoriteFilmeAdapter.FavotireOnClickListener onClickMovieListener;
    FavoriteTvShowAdapter.FavotireOnClickListener onClickTvListener;


    public static Fragment newInstanceMovie(int tipo, MovieResultsPage series) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle bundle = new Bundle();
        Log.d("newInstanceMovie", series.getResults().get(1).getOriginalTitle());
        bundle.putSerializable(Constantes.FILME,  series);
        bundle.putInt(Constantes.ABA, tipo);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment newInstanceTvShow(int tvshow, TvResultsPage results) {
        FavoriteFragment fragment = new FavoriteFragment();
        Bundle bundle = new Bundle();
        Log.d("newInstanceTvShow", results.getResults().get(1).getName());
        bundle.putSerializable(Constantes.SERIE, results);
        bundle.putInt(Constantes.ABA, tvshow);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);
            movies = (MovieResultsPage) getArguments().getSerializable(Constantes.FILME);
            tvSeries = (TvResultsPage) getArguments().getSerializable(Constantes.SERIE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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

    private FavoriteFilmeAdapter.FavotireOnClickListener onclickListerne() {
        return new FavoriteFilmeAdapter.FavotireOnClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                Intent intent = new Intent(getActivity(), FilmeActivity.class);

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
                intent.putExtra(Constantes.FILME_ID, movies.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movies.getResults().get(position).getTitle());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int posicao) {
                Log.d("onBusAtualizarLista", "onClickLong - " + posicao);
                Log.d("onBusAtualizarLista", "onClickLong - " + movies.getResults().get(posicao).toString());
                final int id = movies.getResults().get(posicao).getId();
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_coracao_redondo)
                        .setTitle(movies.getResults().get(posicao).getTitle())
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
                                        //Necessario descobrir se a MediaType é filme ou tvshow

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    movies.getResults().remove(movies.getResults().get(posicao));
                                                    recyclerViewFilme.getAdapter().notifyItemRemoved(posicao);
                                                    recyclerViewFilme.getAdapter().notifyItemChanged(posicao);
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

    private FavoriteTvShowAdapter.FavotireOnClickListener onclickTvShowListerne() {
        return new FavoriteTvShowAdapter.FavotireOnClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                Intent intent = new Intent(getActivity(), TvShowActivity.class);

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
                intent.putExtra(Constantes.TVSHOW_ID, tvSeries.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_TVSHOW, tvSeries.getResults().get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int posicao) {
                Log.d("onBusAtualizarLista", "onClickLong - " + posicao);
                Log.d("onBusAtualizarLista", "onClickLong - " + tvSeries.getResults().get(posicao).toString());
                final int id = tvSeries.getResults().get(posicao).getId();
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_coracao_redondo)
                        .setTitle(tvSeries.getResults().get(posicao).getName())
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
                                        //Necessario descobrir se a MediaType é filme ou tvshow

                                        getActivity().runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if (status.getStatusCode() == 13) {
                                                    tvSeries.getResults().remove(tvSeries.getResults().get(posicao));
                                                    recyclerViewTvShow.getAdapter().notifyItemRemoved(posicao);
                                                    recyclerViewTvShow.getAdapter().notifyItemChanged(posicao);
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
        recyclerViewFilme.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewFilme.setAdapter(new FavoriteFilmeAdapter(getActivity(), movies, onclickListerne()));

        return view;
    }


    private View getViewTvShow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);// Criar novo layout
        recyclerViewTvShow = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewTvShow.setHasFixedSize(true);
        recyclerViewTvShow.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTvShow.setLayoutManager(new GridLayoutManager(getContext(),2));
        recyclerViewTvShow.setAdapter(new FavoriteTvShowAdapter(getActivity(), tvSeries, onclickTvShowListerne()));

        return view;
    }

}
