package adapter;

import android.content.Context;
import android.content.Intent;
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

import activity.FilmeActivity;
import activity.ProdutoraActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Collection;
import utils.Constantes;
import utils.UtilsFilme;



/**
 * Created by icaro on 10/08/16.
 */
public class ProdutoraAdapter extends RecyclerView.Adapter<ProdutoraAdapter.ProdutoraViewHolde> {
    Context context;
    List<Collection> movies;

    public ProdutoraAdapter(ProdutoraActivity produtoraActivity, List<Collection> results) {
        this.context = produtoraActivity;
        this.movies = results;
    }

    @Override
    public ProdutoraViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_produtora, parent, false);
        ProdutoraViewHolde holder = new ProdutoraViewHolde(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ProdutoraViewHolde holder, final int position) {

        final Collection movie = movies.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);

        if (movie != null) {

            String title = movie.getName();
            if (title != null) {
                holder.title.setText(title);
                Log.d("onBindViewHolder", title);
            }

            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.imageView);
            holder.progressBar.setVisibility(View.INVISIBLE);

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FilmeActivity.class);
                    int color = UtilsFilme.loadPalette(holder.imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                    intent.putExtra(Constantes.FILME_ID, movie.getId());
                    intent.putExtra(Constantes.NOME_FILME, movie.getTitle());
                    context.startActivity(intent);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (movies != null) {
            return movies.size();
        }
        return 0;
    }

    public class ProdutoraViewHolde extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        ImageView imageView;
        TextView title;

        public ProdutoraViewHolde(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            imageView = (ImageView) itemView.findViewById(R.id.imgFilme_produtora);
            title = (TextView) itemView.findViewById(R.id.titleTextView_produtora);

        }
    }
}
