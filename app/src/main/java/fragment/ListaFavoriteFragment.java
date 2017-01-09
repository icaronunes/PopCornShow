package fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;

import activity.FilmeActivity;
import activity.TvShowActivity;
import adapter.ListaFilmeAdapter;
import adapter.ListaTvShowAdapter;
import br.com.icaro.filme.R;
import domain.FilmeDB;
import domain.TvshowDB;
import utils.Constantes;
import utils.UtilsFilme;

import static android.R.attr.id;


/**
 * Created by icaro on 23/08/16.
 */
public class ListaFavoriteFragment extends Fragment {

    final String TAG = TvShowFragment.class.getName();

    int tipo;
    List<TvshowDB> tvSeries;
    List<FilmeDB> movieDbs;
    RecyclerView recyclerViewFilme;
    RecyclerView recyclerViewTvShow;
    private FirebaseAnalytics firebaseAnalytics;

    public static Fragment newInstanceMovie(int tipo, List<FilmeDB> movie) {
        ListaFavoriteFragment fragment = new ListaFavoriteFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.FILME, (Serializable) movie);
        bundle.putInt(Constantes.ABA, tipo);
        fragment.setArguments(bundle);
        return fragment;
    }

    public static Fragment newInstanceTvShow(int tvshow, List<TvshowDB> tvshows) {
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
            movieDbs = (List<FilmeDB>) getArguments().getSerializable(Constantes.FILME);
            tvSeries = (List<TvshowDB>) getArguments().getSerializable(Constantes.SERIE);
        }
        firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
      //  Log.d(TAG, "onCreateView");
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
              //  Log.d("ListaFilmeAdapter", "ListaFilmeAdapter");
                ImageView imageView = (ImageView) view;
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.FILME_ID, movieDbs.get(position).getId());
                intent.putExtra(Constantes.NOME_FILME, movieDbs.get(position).getTitle());
                startActivity(intent);

            }

            @Override
            public void onClickLong(View view, final int position) {
                final int id = movieDbs.get(position).getId();
               // Log.d("OnClick", "Onclick");
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(movieDbs.get(position).getTitle())
                        .setMessage(getResources().getString(R.string.excluir_filme))
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                DatabaseReference filmeTv = database.getReference("users").child(mAuth.getCurrentUser()
                                        .getUid()).child("favorites")
                                        .child("movie").child(String.valueOf(id));
                                filmeTv.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        movieDbs.remove(movieDbs.get(position));
                                        recyclerViewFilme.getAdapter().notifyItemRemoved(position);
                                        recyclerViewFilme.getAdapter().notifyItemChanged(position);
                                    }
                                });
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
              //  Log.d("OnClick", "Onclick");
                ImageView imageView = (ImageView) view;
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.TVSHOW_ID, tvSeries.get(position).getExternalIds().getId());
                intent.putExtra(Constantes.NOME_TVSHOW, tvSeries.get(position).getTitle());
                startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaTvShowAdapter.ListaOnClickListener:onclick");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeries.get(position).getTitle());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, tvSeries.get(position).getNota());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }

            @Override
            public void onClickLong(View view, final int position) {

                final TvshowDB tvshowDB = tvSeries.get(position);
               // Log.d("OnClick", "onClickLong");
                new AlertDialog.Builder(getActivity())
                        .setIcon(R.drawable.icon_agenda)
                        .setTitle(tvshowDB.getTitle())
                        .setMessage(getResources().getString(R.string.excluir_tvshow))
                        .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                                FirebaseDatabase database = FirebaseDatabase.getInstance();

                                DatabaseReference favoriteTv = database.getReference("users").child(mAuth.getCurrentUser()
                                        .getUid()).child("favorites")
                                        .child("tvshow").child(String.valueOf(tvshowDB.getExternalIds().getId()));

                                favoriteTv.setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        tvSeries.remove(tvSeries.get(position));
                                        recyclerViewTvShow.getAdapter().notifyItemRemoved(position);
                                        recyclerViewTvShow.getAdapter().notifyItemChanged(position);
                                    }
                                });

                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "ListaWatchlistFragment:ListaTvShowAdapter.ListaOnClickListener:onClickLong");
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeries.get(position).getTitle());
                                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "Tv");
                                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id);
                                bundle.putString("AlertDialog-WatchList", "Excluiu TvShow");
                                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            }
                        }).show();
            }
        };
    }


    private View getViewMovie(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false); // Criar novo layout
        view.findViewById(R.id.progressBarTemporadas).setVisibility(View.GONE);
        recyclerViewFilme = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewFilme.setHasFixedSize(true);
        recyclerViewFilme.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFilme.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if (movieDbs.size() > 0) {
            recyclerViewFilme.setAdapter(new ListaFilmeAdapter(getActivity(), movieDbs, onclickListerne(), false));
        } else {
            view.findViewById(R.id.text_search_empty).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.text_search_empty)).setText(R.string.empty_favorites);
        }

        return view;
    }

    private View getViewTvShow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);// Criar novo layout
        view.findViewById(R.id.progressBarTemporadas).setVisibility(View.GONE);
        recyclerViewTvShow = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewTvShow.setHasFixedSize(true);
        recyclerViewTvShow.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTvShow.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if (tvSeries.size() > 0) {
            recyclerViewTvShow.setAdapter(new ListaTvShowAdapter(getActivity(), tvSeries, onclickTvShowListerne(), false));
        } else {
            view.findViewById(R.id.text_search_empty).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.text_search_empty)).setText(R.string.empty_favorites);
        }

        return view;
    }
}
