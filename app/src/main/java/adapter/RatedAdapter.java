package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import activity.RatedActivity;
import activity.WatchListActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 01/08/16.
 */
public class RatedAdapter extends RecyclerView.Adapter<RatedAdapter.RatedViewHolder> {

    List<MovieDb> rated;
    Context context;

    public RatedAdapter(RatedActivity ratedActivity, List<MovieDb> rated) {
        this.context = ratedActivity;
        this.rated = rated;
    }

    @Override
    public RatedAdapter.RatedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view =  LayoutInflater.from(context).inflate(R.layout.usuario_list_adapter, parent, false);
        RatedViewHolder holder = new RatedViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final RatedViewHolder holder, final int position) {

        final MovieDb movie = rated.get(position);

        if (movie != null) {

            holder.img_button_estrela_favorite.setVisibility(View.GONE);
            holder.text_rated_favoritos.setVisibility(View.VISIBLE);
            String valor = String.valueOf(rated.get(position).getUserRating());
            Log.d("Rated", "" + valor);
            if (valor.length() > 3) {
                valor = valor.substring(0, 2);
                Log.d("Rated 2", "" + valor);
                holder.text_rated_favoritos.setText(valor);
            }
            holder.text_rated_favoritos.setText(valor);

            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
                    .into(holder.img_favorite, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {

                        }
                    });

            holder.img_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FilmeActivity.class);

                    ImageView imageView = (ImageView) view;
                    BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                    if (drawable != null) {
                        Bitmap bitmap = drawable.getBitmap();
                        Palette.Builder builder = new Palette.Builder(bitmap);
                        Palette palette = builder.generate();
                        for (Palette.Swatch swatch : palette.getSwatches()) {
                            intent.putExtra(Constantes.COLOR_TOP, swatch.getRgb());
                        }
                    }
                    intent.putExtra(Constantes.FILME_ID, movie.getId());
                    intent.putExtra(Constantes.NOME_FILME, movie.getTitle());
                    context.startActivity(intent);
                }
            });
        }
    }


    @Override
    public int getItemCount() {
        if (rated != null) {
            return rated.size();
        }
        return 0;
    }

    public static class RatedViewHolder extends RecyclerView.ViewHolder {
        ImageView img_favorite;
        ImageButton img_button_coracao_favorite, img_button_estrela_favorite, img_button_relogio_favorite;
        ProgressBar progressBar;
        TextView text_rated_favoritos;

        public RatedViewHolder(View itemView) {
            super(itemView);
            img_favorite = (ImageView) itemView.findViewById(R.id.img_filme_usuario);
            img_button_coracao_favorite = (ImageButton) itemView.findViewById(R.id.img_button_coracao_usuario);
            img_button_estrela_favorite = (ImageButton) itemView.findViewById(R.id.img_button_estrela_usuario);
            img_button_relogio_favorite = (ImageButton) itemView.findViewById(R.id.img_button_relogio_usuario);
            text_rated_favoritos = (TextView) itemView.findViewById(R.id.text_rated_favoritos);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            itemView.findViewById(R.id.botoes_lista).setVisibility(View.GONE);
        }
    }

}
