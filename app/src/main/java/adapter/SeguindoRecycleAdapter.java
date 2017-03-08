package adapter;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.TvShowActivity;
import br.com.icaro.filme.R;
import domain.UserTvshow;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 02/12/16.
 */
public class SeguindoRecycleAdapter extends RecyclerView.Adapter<SeguindoRecycleAdapter.SeguindoViewHolder> {

    private FragmentActivity context;
    private List<UserTvshow> userTvshows;

    public SeguindoRecycleAdapter(FragmentActivity activity, List<UserTvshow> userTvshows) {
        this.context = activity;
        this.userTvshows = userTvshows;
    }

    @Override
    public SeguindoRecycleAdapter.SeguindoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.seguindo_tvshow, parent, false);
        return new SeguindoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SeguindoRecycleAdapter.SeguindoViewHolder holder, int position) {
        final UserTvshow userTvshow = userTvshows.get(position);
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context,2)) + userTvshow.getPoster())
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .error(R.drawable.poster_empty)
                .into(holder.poster, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        holder.title.setText(userTvshow.getNome());
                        holder.title.setVisibility(View.VISIBLE);
                    }
                });



        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TvShowActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID,userTvshow.getId());
                intent.putExtra(Constantes.NOME_TVSHOW, userTvshow.getNome());
                intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(holder.poster));
                context.startActivity(intent);

                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(userTvshow.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userTvshow.getNome());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        });

    }

    @Override
    public int getItemCount() {
        if (userTvshows != null) {
            return userTvshows.size();
        }
         return 0;
    }

    public class SeguindoViewHolder extends RecyclerView.ViewHolder {

        ImageView poster;
        TextView title;

        public SeguindoViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.seguindo_imageView);
            title = (TextView) itemView.findViewById(R.id.seguindo_title);
        }
    }
}