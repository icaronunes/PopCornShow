package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import activity.PosterActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 28/07/16.
 */
public class PosterGridAdapter extends RecyclerView.Adapter<PosterGridAdapter.PosterViewHolder> {

    List<Artwork> artworks;
    Context context;
    String nome;


    public PosterGridAdapter(Context context, List<Artwork> artworks, String nome) {
        this.context = context;
        this.artworks = artworks;
        this.nome = nome;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false);
        PosterViewHolder posterViewHolder = new PosterViewHolder(view);
        Log.d("PosterGridActivity", "onCreateViewHolder ");
        return posterViewHolder;
    }

    @Override
    public void onBindViewHolder(final PosterViewHolder holder, final int position) {
        if (artworks.size() > 0) {
            Log.d("PosterGridActivity", "onBindViewHolder ");
            Picasso.with(context).load(UtilsFilme
                    .getBaseUrlImagem(4) + artworks.get(position).getFilePath())
                    .into(holder.img, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.img.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Intent intent = new Intent(context, PosterActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constantes.ARTWORKS, (Serializable) artworks);
                                    intent.putExtra(Constantes.BUNDLE, bundle);
                                    intent.putExtra(Constantes.POSICAO, position);
                                    intent.putExtra(Constantes.NOME, nome);
                                    ActivityOptionsCompat opts = ActivityOptionsCompat.makeCustomAnimation(context,
                                            android.R.anim.fade_in, android.R.anim.fade_out);
                                    ActivityCompat.startActivity((Activity) context, intent, opts.toBundle());
                                    FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                                    mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                }
                            });
                        }

                        @Override
                        public void onError() {

                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        Log.d("PosterGridActivity", "getItemCount " + artworks.size());
        return artworks.size() > 0 ? artworks.size() : 0;
    }

    public class PosterViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        ProgressBar progressBar;

        public PosterViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_poster_grid);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
            Log.d("PosterGridActivity", "PosterViewHolder " + artworks.size());
        }
    }
}
