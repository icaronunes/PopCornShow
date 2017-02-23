package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import activity.PersonActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 22/02/17.
 */
public class SimilaresFilmesAdapter extends RecyclerView.Adapter<SimilaresFilmesAdapter.SimilaresViewHolder> {
    private Context context;
    private List<MovieDb> movieDbs;
    private int color_top;

    public SimilaresFilmesAdapter(FragmentActivity activity, List<MovieDb> movieDbs) {
        this.movieDbs = movieDbs;
        context = activity;
    }

    @Override
    public SimilaresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_similares, parent, false);
        return new SimilaresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimilaresViewHolder holder, int position) {
        final MovieDb movie = movieDbs.get(position);
        holder.progressBarSimilares.setVisibility(View.VISIBLE);
        if (movie.getTitle() != null && movie.getPosterPath() != null) {
            if (movie.getTitle().length() > 21) {
                String title = movie.getTitle().substring(0, 18);
                title = title.concat("...");
                holder.textSimilares.setText(title);

            } else {
                holder.textSimilares.setText(movie.getTitle());
            }
            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + movie.getPosterPath())
                    .placeholder(context.getDrawable(R.drawable.poster_empty))
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.imgPagerSimilares, new Callback() {
                        @Override
                        public void onSuccess() {
                            color_top = UtilsFilme.loadPalette(holder.imgPagerSimilares);
                            holder.progressBarSimilares.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBarSimilares.setVisibility(View.GONE);
                        }
                    });

            holder.imgPagerSimilares.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FilmeActivity.class);
                    intent.putExtra(Constantes.COLOR_TOP, color_top);
                    intent.putExtra(Constantes.NOME_FILME, movie.getTitle());
                    intent.putExtra(Constantes.FILME_ID, movie.getId());
                    context.startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movie.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                    FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });

        }
//        else {
//            textSimilares.setVisibility(View.GONE);
//            progressBarSimilares.setVisibility(View.GONE);
//            imgPagerSimilares.setVisibility(View.GONE);
//        }

    }

    @Override
    public int getItemCount() {
        return movieDbs.size();
    }


    public class SimilaresViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBarSimilares;
        TextView textSimilares;
        ImageView imgPagerSimilares;
        public SimilaresViewHolder(View itemView) {
            super(itemView);
            progressBarSimilares = (ProgressBar) itemView.findViewById(R.id.progressBarSimilares);
            textSimilares = (TextView) itemView.findViewById(R.id.textSimilaresNome);
            imgPagerSimilares = (ImageView) itemView.findViewById(R.id.imgPagerSimilares);
        }
    }
}
