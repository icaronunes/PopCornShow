package adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.UtilsFilme;

/**
 * Created by icaro on 30/06/16.
 */
public class FilmesAdapter extends RecyclerView.Adapter<FilmesAdapter.FilmeViewHolder> {

    private final Context context;
    protected List<MovieDb> tmdbMovies;
    protected MovieDb movie;
    private FilmeOnClickListener filmeOnClickListener;

    public FilmesAdapter(Context context, List<MovieDb> tmdbMovies, FilmeOnClickListener filmeOnClickListener) {

        this.context = context;
        this.tmdbMovies = tmdbMovies;
        this.filmeOnClickListener = filmeOnClickListener;
    }

    @Override
    public FilmeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(context).inflate(R.layout.adapter_filmes_list, parent, false);
        FilmeViewHolder holder = new FilmeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FilmesAdapter.FilmeViewHolder holder, final int position) {
        movie = tmdbMovies.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);
       // scroll_elenco();
        if (movie != null) {
//            Log.d("onBindViewHolder", "Titulo Original - " + movie.getOriginalTitle());
//            Log.d("onBindViewHolder", "Titulo - " + movie.toString());
//            Log.d("onBindViewHolder", "ID: " + movie.getId());
//            Log.d("Reviwes", "" + movie.getOverview());
//            Log.d("Direção", ""+movie.getHomepage());
            String title = movie.getTitle();
            if (title != null) {
                holder.title.setText(title);
            }
            String sinopse = movie.getOverview();
            if (!sinopse.isEmpty()) {
                holder.sinopse.setText(sinopse);
            } else {
                holder.sinopse.setVisibility(View.INVISIBLE);
            }

            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.img);
            holder.progressBar.setVisibility(View.INVISIBLE);
            if (filmeOnClickListener != null) {
                holder.img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filmeOnClickListener.onClickFilme(holder.img, position);
                    }
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        int total = tmdbMovies.size();
        return total == 0 ? 0 : total;
    }


    public interface FilmeOnClickListener {
        void onClickFilme(View view, int position);
    }

    public static class FilmeViewHolder extends RecyclerView.ViewHolder {

        TextView title, sinopse;
        ImageView img;
        CardView cardView;
        ProgressBar progressBar;

        public FilmeViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            sinopse = (TextView) itemView.findViewById(R.id.sinopse);
            img = (ImageView) itemView.findViewById(R.id.imgFilme);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }


}
