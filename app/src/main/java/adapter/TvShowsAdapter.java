package adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
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

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.UtilsFilme;

/**
 * Created by icaro on 14/09/16.
 */
public class TvShowsAdapter extends RecyclerView.Adapter<TvShowsAdapter.TvShowViewHolder> {

    private final Context context;
    protected List<TvSeries> tvSeries;
    private  TvShowsAdapter.TvshowOnClickListener tvshowOnClickListener;

    public TvShowsAdapter(Context context, List<TvSeries> tvSeries,
                          TvShowsAdapter.TvshowOnClickListener filmeOnClickListener) {

        this.context = context;
        this.tvSeries = tvSeries;
        this.tvshowOnClickListener = filmeOnClickListener;
    }

    @Override
    public TvShowsAdapter.TvShowViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_filmes_list, parent, false);
        TvShowsAdapter.TvShowViewHolder holder = new TvShowsAdapter.TvShowViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final TvShowsAdapter.TvShowViewHolder holder, final int position) {
        final TvSeries series = tvSeries.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);

        if (series != null) {


            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 4)) + series.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.imagem_filme, new Callback() {
                        @Override
                        public void onSuccess() {
                            String fist = series.getFirstAirDate();
                            holder.title.setText(fist.length() >= 4 ? fist.substring(0,4) : "");
                        }

                        @Override
                        public void onError() {
                            String title = series.getName();
                            String release = series.getFirstAirDate();
                            holder.title.setText(title + " - " + release);
                        }
                    });
            holder.progressBar.setVisibility(View.INVISIBLE);
            if (tvshowOnClickListener != null) {
                holder.imagem_filme.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        tvshowOnClickListener.onClickTvshow(holder.imagem_filme, position);
                    }
                });
            }
        }


    }

    @Override
    public int getItemCount() {
        if (tvSeries != null) {
            return tvSeries.size();
        }
        return 0;
    }


    public interface TvshowOnClickListener {
        void onClickTvshow(View view, int position);
    }

    public static class TvShowViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView imagem_filme, coracao;
        CardView cardView;
        ProgressBar progressBar;

        public TvShowViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.titleTextView);
            imagem_filme = (ImageView) itemView.findViewById(R.id.imgFilme);
            coracao = (ImageView) itemView.findViewById(R.id.coracao);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

}
