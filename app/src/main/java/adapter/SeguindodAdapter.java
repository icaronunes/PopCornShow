package adapter;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.TvShowActivity;
import br.com.icaro.filme.R;
import domian.UserTvshow;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 02/12/16.
 */
public class SeguindodAdapter extends RecyclerView.Adapter<SeguindodAdapter.SeguindoViewHolder> {

    private FragmentActivity activity;
    private List<UserTvshow> userTvshows;

    public SeguindodAdapter(FragmentActivity activity, List<UserTvshow> userTvshows) {
        this.activity = activity;
        this.userTvshows = userTvshows;
    }

    @Override
    public SeguindodAdapter.SeguindoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(activity).inflate(R.layout.seguindo_tvshow, parent, false);
        SeguindoViewHolder holder = new SeguindoViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final SeguindodAdapter.SeguindoViewHolder holder, int position) {
        final UserTvshow userTvshow = userTvshows.get(position);
        Picasso.with(activity).load(UtilsFilme.getBaseUrlImagem(2) + userTvshow.getPoster())
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
                Intent intent = new Intent(activity, TvShowActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID,userTvshow.getId());
                intent.putExtra(Constantes.NOME_TVSHOW, userTvshow.getNome());
                intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(holder.poster));
                activity.startActivity(intent);

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