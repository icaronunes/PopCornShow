package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import br.com.icaro.filme.R;
import domain.ResultsSimilarItem;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 22/02/17.
 */
public class SimilaresFilmesAdapter extends RecyclerView.Adapter<SimilaresFilmesAdapter.SimilaresViewHolder> {
    private Context context;
    private List<ResultsSimilarItem> similarItems;
    private int color_top;

    public SimilaresFilmesAdapter(FragmentActivity activity, List<ResultsSimilarItem> movieDbs) {
        this.similarItems = movieDbs;
        context = activity;
    }

    @Override
    public SimilaresViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_similares, parent, false);
        return new SimilaresViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimilaresViewHolder holder, int position) {
        final ResultsSimilarItem movie = similarItems.get(position);
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
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + movie.getPosterPath())
                    .placeholder(R.drawable.poster_empty)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.imgPagerSimilares, new Callback() {
                        @Override
                        public void onSuccess() {
                            color_top = UtilsApp.loadPalette(holder.imgPagerSimilares);
                            holder.progressBarSimilares.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBarSimilares.setVisibility(View.GONE);
                        }
                    });

            holder.imgPagerSimilares.setOnClickListener(view -> {
                Intent intent = new Intent(context, FilmeActivity.class);
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color_top);
                intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), movie.getTitle());
                intent.putExtra(Constantes.INSTANCE.getFILME_ID(), movie.getId());
                context.startActivity(intent);

            });

        }
    }

    @Override
    public int getItemCount() {
        return similarItems.size();
    }


    class SimilaresViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBarSimilares;
        private TextView textSimilares;
        private ImageView imgPagerSimilares;

        SimilaresViewHolder(View itemView) {
            super(itemView);
            progressBarSimilares = (ProgressBar) itemView.findViewById(R.id.progressBarSimilares);
            textSimilares = (TextView) itemView.findViewById(R.id.textSimilaresNome);
            imgPagerSimilares = (ImageView) itemView.findViewById(R.id.imgPagerSimilares);
        }
    }
}
