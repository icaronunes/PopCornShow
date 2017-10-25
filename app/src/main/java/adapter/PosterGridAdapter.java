package adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

import activity.PosterActivity;
import br.com.icaro.filme.R;
import domain.PostersItem;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 28/07/16.
 */
public class PosterGridAdapter extends RecyclerView.Adapter<PosterGridAdapter.PosterViewHolder> {

    private List<PostersItem> artworks;
    private Context context;
    private String nome;


    public PosterGridAdapter(Context context, List<PostersItem> artworks, String nome) {
        this.context = context;
        this.artworks = artworks;
        this.nome = nome;
    }

    @Override
    public PosterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false);
        return new PosterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PosterViewHolder holder, final int position) {
        if (artworks.size() > 0) {

            Picasso.with(context).load(UtilsApp
                    .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 4)) + artworks.get(position).getFilePath())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.img, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.img.setOnClickListener(view -> {
                                Intent intent = new Intent(context, PosterActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constantes.INSTANCE.getARTWORKS(), (Serializable) artworks);
                                intent.putExtra(Constantes.INSTANCE.getBUNDLE(), bundle);
                                intent.putExtra(Constantes.INSTANCE.getPOSICAO(), position);
                                intent.putExtra(Constantes.INSTANCE.getNOME(), nome);
                                ActivityOptionsCompat opts = ActivityOptionsCompat.makeCustomAnimation(context,
                                        android.R.anim.fade_in, android.R.anim.fade_out);
                                ActivityCompat.startActivity((Activity) context, intent, opts.toBundle());
                                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
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
        return artworks.size() > 0 ? artworks.size() : 0;
    }

    class PosterViewHolder extends RecyclerView.ViewHolder {
        private ImageView img;
        private ProgressBar progressBar;

        PosterViewHolder(View itemView) {
            super(itemView);
            img = (ImageView) itemView.findViewById(R.id.img_poster_grid);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
        }
    }
}
