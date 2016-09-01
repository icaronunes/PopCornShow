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
import android.widget.Toast;

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

    public FilmesAdapter(Context context, List<MovieDb> tmdbMovies,
                         FilmeOnClickListener filmeOnClickListener) {

        this.context = context;
        this.tmdbMovies = tmdbMovies;
        this.filmeOnClickListener = filmeOnClickListener;
    }

    @Override
    public FilmeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_filmes_list, parent, false);
        FilmeViewHolder holder = new FilmeViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FilmesAdapter.FilmeViewHolder holder, final int position) {
        movie = tmdbMovies.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);

        if (movie != null) {

            String title = movie.getReleaseDate();
            if (title != null) {
                holder.title.setText(title);
            }

            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.imagem_filme);
            holder.progressBar.setVisibility(View.INVISIBLE);
            if (filmeOnClickListener != null) {
                holder.imagem_filme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        filmeOnClickListener.onClickFilme(holder.imagem_filme, position);
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        if (tmdbMovies != null){
            return tmdbMovies.size();
        }
        return 0;
    }


    public interface FilmeOnClickListener {
        void onClickFilme(View view, int position);
    }

    public static class FilmeViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imagem_filme, coracao;
        CardView cardView;
        ProgressBar progressBar;

        public FilmeViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            imagem_filme = (ImageView) itemView.findViewById(R.id.imgFilme);
            coracao = (ImageView) itemView.findViewById(R.id.coracao);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }


}
