package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import activity.FilmeActivity;
import activity.ProdutoraActivity;
import br.com.icaro.filme.R;
import domain.ResultsItem;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 10/08/16.
 */
public class ProdutoraAdapter extends RecyclerView.Adapter<ProdutoraAdapter.ProdutoraViewHolde> {
    private Context context;
    private List<ResultsItem> movies = new ArrayList<>();

    public ProdutoraAdapter(ProdutoraActivity produtoraActivity) {
        this.context = produtoraActivity;
        movies.add(new ResultsItem());
    }

    @Override
    public ProdutoraViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_produtora, parent, false);
        return new ProdutoraViewHolde(view);
    }

    @Override
    public void onBindViewHolder(final ProdutoraViewHolde holder, final int position) {

        final ResultsItem movie = movies.get(position);
        holder.progressBar.setVisibility(View.VISIBLE);

        holder.title.setText(movie.getTitle());

        Picasso.with(context)
                .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + movie.getPosterPath())
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });


        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilmeActivity.class);
                int color = UtilsFilme.loadPalette(holder.imageView);
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                intent.putExtra(Constantes.INSTANCE.getFILME_ID(), movie.getId());
                intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), movie.getTitle());
                context.startActivity(intent);


                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, ProdutoraAdapter.class.getName());
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, FilmeActivity.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movie.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movie.getTitle());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

            }
        });


    }

    @Override
    public int getItemCount() {
            return movies.size();
    }

    public void addMovies(@Nullable List<ResultsItem> results) {

        int ultimaPosicao = movies.size() - 1;
        movies.remove(ultimaPosicao);
        notifyItemRemoved(ultimaPosicao);

        if (results != null)
        for (ResultsItem result : results) {
            movies.add(result);
        }
        //games?.add(loadingItem)
        notifyItemRangeChanged(ultimaPosicao, movies.size() +1 /* plus loading item */);
        movies.add(new ResultsItem());
    }

    class ProdutoraViewHolde extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private ImageView imageView;
        private TextView title;

        ProdutoraViewHolde(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            imageView = (ImageView) itemView.findViewById(R.id.imgFilme_produtora);
            title = (TextView) itemView.findViewById(R.id.titleTextView_produtora);

        }
    }
}
