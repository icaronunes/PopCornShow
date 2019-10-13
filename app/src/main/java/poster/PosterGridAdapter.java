package poster;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.List;

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

            Picasso.get().load(UtilsApp.INSTANCE
                    .getBaseUrlImagem(UtilsApp.INSTANCE.getTamanhoDaImagem(context, 4)) + artworks.get(position).getFilePath())
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.img, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.img.setOnClickListener(view -> {
                                Intent intent = new Intent(context, PosterActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(Constantes.ARTWORKS, (Serializable) artworks);
                                intent.putExtra(Constantes.BUNDLE, bundle);
                                intent.putExtra(Constantes.POSICAO, position);
                                intent.putExtra(Constantes.NOME, nome);
                                ActivityOptionsCompat opts = ActivityOptionsCompat.makeCustomAnimation(context,
                                        android.R.anim.fade_in, android.R.anim.fade_out);
                                ActivityCompat.startActivity(context, intent, opts.toBundle());
                                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                            });
                        }

                        @Override
                        public void onError(Exception e) {

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
            img = itemView.findViewById(R.id.img_poster_grid);
            progressBar = itemView.findViewById(R.id.progress_poster_grid);
        }
    }
}
