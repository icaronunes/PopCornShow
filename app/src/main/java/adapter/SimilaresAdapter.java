package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import activity.SimilaresActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 12/08/16.
 */

public class SimilaresAdapter extends RecyclerView.Adapter<SimilaresAdapter.SimilareViewHolde> {

   private Context context;
    private List<MovieDb> similares;

    public SimilaresAdapter(SimilaresActivity similaresActivity, List<MovieDb> similarMovies) {
        context = similaresActivity;
        this.similares = similarMovies;
    }

    @Override
    public SimilareViewHolde onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_similares, parent, false);
        return new SimilareViewHolde(view);
    }

    @Override
    public void onBindViewHolder(final SimilareViewHolde holder, final int position) {
        holder.similares_nome.setText(similares.get(position).getTitle());
        holder.similares_data_lancamento.setText(similares.get(position).getReleaseDate());
        holder.similares_title_original.setText(similares.get(position).getOriginalTitle());
        holder.similares_voto_media.setText(String
                .format(String.valueOf(similares.get(position).getVoteAverage())));

        Picasso.with(context)
                .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + similares.get(position)
                .getPosterPath())
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageView);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilmeActivity.class);
                int color = UtilsFilme.loadPalette(holder.imageView);
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                intent.putExtra(Constantes.INSTANCE.getFILME_ID(), similares.get(position).getId());
                intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), similares.get(position).getTitle());
                context.startActivity(intent);

                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, FilmeActivity.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, similares.get(position).getId() );
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, similares.get(position).getTitle() );
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (similares != null) {
            return similares.size();
        }
        return 0;
    }

    class SimilareViewHolde extends RecyclerView.ViewHolder {

         private   ImageView imageView;
        private TextView similares_nome;
        private TextView similares_data_lancamento;
        private TextView similares_voto_media;
        private TextView similares_title_original;


        SimilareViewHolde(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.img_similares);
            similares_nome = (TextView) itemView.findViewById(R.id.similares_nome);
            similares_data_lancamento = (TextView) itemView.findViewById(R.id.similares_data_lancamento);
            similares_voto_media = (TextView) itemView.findViewById(R.id.similares_voto_media);
            similares_title_original = (TextView) itemView.findViewById(R.id.similares_title_original);
        }
    }
}
