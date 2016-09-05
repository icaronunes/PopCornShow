package fragment;

import android.app.Dialog;
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
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;

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

    public static Fragment newInstanceMovie(int tipo, MovieResultsPage series) {
        ListaFragment fragment = new ListaFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.FILME, series);
        bundle.putInt(Constantes.ABA, tipo);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment newInstanceTvShow(int tvshow, TvResultsPage results) {
        ListaFragment fragment = new ListaFragment();
        Bundle bundle = new Bundle();
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
                intent.putExtra(Constantes.FILME_ID, movies.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movies.getResults().get(position).getTitle());
                startActivity(intent);

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
                            }
                        }).show();
            }
        };
    }


    private ListaTvShowAdapter.RatedOnClickListener onclickTvShowListerne() {
        return new ListaTvShowAdapter.RatedOnClickListener() {
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
                intent.putExtra(Constantes.TVSHOW_ID, tvSeries.getResults().get(position).getId());
                intent.putExtra(Constantes.NOME_TVSHOW, tvSeries.getResults().get(position).getName());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int position) {
                final ResponseStatus[] status = new ResponseStatus[1];
                final Dialog alertDialog = new Dialog(getActivity());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.adialog_custom_rated);
                Log.d("OnClick", "Onclick");
                Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                int width = getResources().getDimensionPixelSize(R.dimen.popup_width); //Criar os Dimen do layout do login - 300dp - 300dp ??
                int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);
                Log.d(TAG, "Valor Rated" + tvSeries.getResults().get(position).getUserRating());
                ratingBar.setRating(tvSeries.getResults().get(position).getUserRating());

                alertDialog.getWindow().setLayout(width, height);

                ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        final int id = tvSeries.getResults().get(position).getId();
                        new AlertDialog.Builder(getActivity())
                                .setIcon(R.drawable.icon_agenda)
                                .setTitle(tvSeries.getResults().get(position).getName())
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
                                                status[0] = FilmeService.addOrRemoverWatchList(id, false, TmdbAccount.MediaType.MOVIE);
                                                //Arrumar para utilizar em filme e tvshow

                                                getActivity().runOnUiThread(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        if (status[0].getStatusCode() == 13) {
                                                            tvSeries.getResults().remove(tvSeries.getResults().get(position));
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
                });
            }
        };
    }


    private View getViewMovie(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false); // Criar novo layout
        recyclerViewFilme = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewFilme.setHasFixedSize(true);
        recyclerViewFilme.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFilme.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewFilme.setAdapter(new ListaFilmeAdapter(getActivity(), movies, onclickListerne(), false));

        return view;
    }

    private View getViewTvShow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);// Criar novo layout
        recyclerViewTvShow = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewTvShow.setHasFixedSize(true);
        recyclerViewTvShow.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTvShow.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewTvShow.setAdapter(new ListaTvShowAdapter(getActivity(), tvSeries, onclickTvShowListerne()));

        return view;
    }
}
