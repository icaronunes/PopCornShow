package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
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
import com.squareup.picasso.Picasso;

import activity.FilmeActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 17/02/17.
 */
@Keep
public class MovieMainAdapter extends RecyclerView.Adapter<MovieMainAdapter.MovieViewHolder>{
    private Context context;
    private MovieResultsPage movieDbs ;

    public MovieMainAdapter(FragmentActivity context, MovieResultsPage movieDbs) {
        this.context = context;
        this.movieDbs = movieDbs;
    }

    @Override
    public MovieMainAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_main, parent, false);
        return new MovieMainAdapter.MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MovieMainAdapter.MovieViewHolder holder, final int position) {

        final MovieDb movieDb = movieDbs.getResults().get(position);

        Picasso.with(context)
                .load(UtilsFilme.getBaseUrlImagem( UtilsFilme.getTamanhoDaImagem(context, 2)) + movieDb.getPosterPath())
                //.placeholder(R.drawable.poster_empty)
                .into(holder.img_poster_grid, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress_poster_grid.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progress_poster_grid.setVisibility(View.GONE);
                        holder.title_main.setText(movieDb.getTitle());
                        holder.title_main.setVisibility(View.VISIBLE);
                        holder.img_poster_grid.setImageResource(R.drawable.poster_empty);
                    }
                });

        holder.img_poster_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Movie");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent intent = new Intent(context, FilmeActivity.class);
                intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(holder.img_poster_grid));
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        if (movieDbs != null){
            return  movieDbs.getResults().size() < 15 ? movieDbs.getResults().size() : 15;
        }
        return 0;
    }

    @Keep
    class MovieViewHolder extends RecyclerView.ViewHolder {

        private TextView title_main;
        private ProgressBar progress_poster_grid;
        private ImageView img_poster_grid;

        MovieViewHolder(View itemView) {
            super(itemView);
            title_main = (TextView) itemView.findViewById(R.id.title_main);
            progress_poster_grid = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
            img_poster_grid = (ImageView) itemView.findViewById(R.id.img_poster_grid);
        }
    }
}
