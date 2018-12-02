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

import org.jetbrains.annotations.NotNull;

import java.util.List;

import br.com.icaro.filme.R;
import domain.UserTvshow;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;

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
        Picasso.get().load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context,2)) + userTvshow.getPoster())
                .into(holder.poster, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.title.setText(userTvshow.getNome());
                        holder.poster.setImageResource(R.drawable.poster_empty);
                        holder.title.setVisibility(View.VISIBLE);
                    }
                });

        if (userTvshow.getDesatualizada()) {
            holder.circulo.setVisibility(View.VISIBLE);
        } else {
            holder.circulo.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TvShowActivity.class);
                intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(),userTvshow.getId());
                intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), userTvshow.getNome());
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.loadPalette(holder.poster));
                context.startActivity(intent);
            }
        });

    }

    public void add(@NotNull UserTvshow tvFire) {
        userTvshows.add(tvFire);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (userTvshows != null) {
            return userTvshows.size();
        }
         return 0;
    }

    class SeguindoViewHolder extends RecyclerView.ViewHolder {

        private ImageView poster;
        private TextView title;
        private ImageView circulo;

        SeguindoViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.seguindo_imageView);
            title = (TextView) itemView.findViewById(R.id.seguindo_title);
            circulo = itemView.findViewById(R.id.seguindo_circulo);
        }
    }
}