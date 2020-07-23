package avaliado;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.List;

import adapter.ListaFilmeAdapter;
import adapter.ListaTvShowAdapter;
import br.com.icaro.filme.R;
import domain.MovieDb;
import domain.TvshowDB;
import filme.activity.MovieDetailsActivity;
import tvshow.activity.TvShowActivity;
import utils.Constant;
import utils.UtilsApp;


/**
 * Created by icaro on 23/08/16.
 */
public class ListaRatedFragment extends Fragment {

    private int tipo;
    private List<MovieDb> movies;
    private List<TvshowDB> tvSeries;
    private RecyclerView recyclerViewFilme;
    private RecyclerView recyclerViewTvShow;

    public static Fragment newInstanceMovie(int tipo, List<MovieDb> movieDbs) {
        ListaRatedFragment fragment = new ListaRatedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.FILME, (Serializable) movieDbs);
        bundle.putInt(Constant.ABA, tipo);
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment newInstanceTvShow(int tvshow, List<TvshowDB> tvshowDBs) {
        ListaRatedFragment fragment = new ListaRatedFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.SERIE, (Serializable) tvshowDBs);
        bundle.putInt(Constant.ABA, tvshow);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constant.ABA);
            movies = (List<MovieDb>) getArguments().getSerializable(Constant.FILME);
            tvSeries = (List<TvshowDB>) getArguments().getSerializable(Constant.SERIE);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                ImageView imageView = (ImageView) view;
                int color = UtilsApp.INSTANCE.loadPalette(imageView);
                intent.putExtra(Constant.COLOR_TOP, color);
                intent.putExtra(Constant.FILME_ID, movies.get(position).getId());
                intent.putExtra(Constant.NOME_FILME, movies.get(position).getTitle());
                startActivity(intent);
            }

            @Override
            public void onClickLong(View view, final int position) {

                final Dialog alertDialog = new Dialog(getContext());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_custom_rated);

                Button ok = alertDialog.findViewById(R.id.ok_rated);
                Button no = alertDialog.findViewById(R.id.cancel_rated);
                final RatingBar ratingBar = alertDialog.findViewById(R.id.ratingBar_rated);
                int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
                int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                ratingBar.setRating(movies.get(position).getNota() / 2);


                no.setOnClickListener(view1 -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference myRated = database.getReference("users").child(mAuth.getCurrentUser()
                            .getUid()).child("rated")
                            .child("movie").child(String.valueOf(movies.get(position).getId()));

                    myRated.setValue(null)
                            .addOnCompleteListener(task -> {
                                movies.remove(movies.get(position));
                                recyclerViewFilme.getAdapter().notifyItemRemoved(position);
                                recyclerViewFilme.getAdapter().notifyItemChanged(position);
                            });
                    alertDialog.dismiss();
                });

                alertDialog.getWindow().setLayout(width, height);

                ok.setOnClickListener(view12 -> {
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference myRated = database.getReference("users").child(mAuth.getCurrentUser()
                            .getUid()).child("rated")
                            .child("movie").child(String.valueOf(movies.get(position).getId()));

                    if (ratingBar.getRating() > 0) {

                        movies.get(position).setNota(ratingBar.getRating() * 2);

                        myRated.setValue(movies.get(position))
                                .addOnCompleteListener(task -> recyclerViewFilme.getAdapter().notifyItemChanged(position));
                    }

                    alertDialog.dismiss();
                });
                alertDialog.show();
            }
        };
    }


    private ListaTvShowAdapter.ListaOnClickListener onclickTvShowListerne() {
        return new ListaTvShowAdapter.ListaOnClickListener() {
            @Override
            public void onClick(final View view, final int position) {
                Intent intent = new Intent(getActivity(), TvShowActivity.class);
                ImageView imageView = (ImageView) view;
                int color = UtilsApp.INSTANCE.loadPalette(imageView);
                intent.putExtra(Constant.COLOR_TOP, color);
                intent.putExtra(Constant.TVSHOW_ID, tvSeries.get(position).getId());
                intent.putExtra(Constant.NOME_TVSHOW, tvSeries.get(position).getTitle());
                startActivity(intent);

            }

            @Override
            public void onClickLong(View view, final int position) {

                final Dialog alertDialog = new Dialog(getActivity());
                alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                alertDialog.setContentView(R.layout.dialog_custom_rated);

                Button ok = alertDialog.findViewById(R.id.ok_rated);
                Button no = alertDialog.findViewById(R.id.cancel_rated);
                final RatingBar ratingBar = alertDialog.findViewById(R.id.ratingBar_rated);
                int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
                int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);
                ratingBar.setRating(tvSeries.get(position).getNota() / 2);
                alertDialog.getWindow().setLayout(width, height);

                no.setOnClickListener(view1 -> {

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference myRated = database.getReference("users").child(mAuth.getCurrentUser()
                            .getUid()).child("rated")
                            .child("tvshow").child(String.valueOf(tvSeries.get(position).getId()));

                    myRated.setValue(null)
                            .addOnCompleteListener(task -> {
                                tvSeries.remove(tvSeries.get(position));
                                recyclerViewTvShow.getAdapter().notifyItemRemoved(position);
                                recyclerViewTvShow.getAdapter().notifyItemChanged(position);
                            });
                    alertDialog.dismiss();
                });

                ok.setOnClickListener(view12 -> {

                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();

                    DatabaseReference myRated = database.getReference("users").child(mAuth.getCurrentUser()
                            .getUid()).child("rated")
                            .child("tvshow").child(String.valueOf(tvSeries.get(position).getId()));

                    if (ratingBar.getRating() > 0) {

                        tvSeries.get(position).setNota( ratingBar.getRating() * 2);

                        myRated.setValue(tvSeries.get(position))
                                .addOnCompleteListener(task -> recyclerViewTvShow.getAdapter().notifyItemChanged(position));
                    }

                    alertDialog.dismiss();
                });
                alertDialog.show();
            }
        };
    }


    private View getViewMovie(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false); // Criar novo layout
        view.findViewById(R.id.progress_temporadas).setVisibility(View.GONE);
        recyclerViewFilme = view.findViewById(R.id.temporadas_recycler);
        recyclerViewFilme.setHasFixedSize(true);
        recyclerViewFilme.setItemAnimator(new DefaultItemAnimator());
        recyclerViewFilme.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if (movies.size() > 0) {
            recyclerViewFilme.setAdapter(new ListaFilmeAdapter(getActivity(), movies,
                    onclickListerne(), true));
        } else {
            view.findViewById(R.id.text_search_empty).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.text_search_empty)).setText(R.string.empty_rated);
        }

        return view;
    }

    private View getViewTvShow(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);
        view.findViewById(R.id.progress_temporadas).setVisibility(View.GONE);
        recyclerViewTvShow = view.findViewById(R.id.temporadas_recycler);
        recyclerViewTvShow.setHasFixedSize(true);
        recyclerViewTvShow.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTvShow.setLayoutManager(new GridLayoutManager(getContext(), 2));
        if (tvSeries.size() > 0) {
            recyclerViewTvShow.setAdapter(new ListaTvShowAdapter(getActivity(), tvSeries,
                    onclickTvShowListerne(), true));
        } else {
            view.findViewById(R.id.text_search_empty).setVisibility(View.VISIBLE);
            ((TextView) view.findViewById(R.id.text_search_empty)).setText(R.string.empty_rated);
        }
        return view;
    }
}
