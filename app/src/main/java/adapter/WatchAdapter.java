package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
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
import activity.WatchListActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 01/08/16.
 */
public class WatchAdapter extends RecyclerView.Adapter<WatchAdapter.WatchViewHolder> {

    List<MovieDb> favoritos, watchlist, rated;
    Context context;
    FavoriteOnClickListener favoriteOnClickListener;

    public WatchAdapter(WatchListActivity watchListActivity, List<MovieDb> watchlist, List<MovieDb> rated) {
        this.context = watchListActivity;
        this.watchlist = watchlist;
        this.rated = rated;
        this.favoriteOnClickListener = favoriteOnClickListener;
    }

    @Override
    public WatchAdapter.WatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(context).inflate(R.layout.usuario_list_adapter, parent, false);
        WatchViewHolder holder = new WatchViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final WatchViewHolder holder, final int position) {
//        Log.d("FavotireAdapter", "favotiros " + favoritos.size());
//        Log.d("FavotireAdapter", "watchlist " + watchlist.size());
//        Log.d("FavotireAdapter", "rated " + rated.size());
        boolean addOrRemove = true;

        final MovieDb movie = watchlist.get(position);
        if (movie != null) {
            // Log.d("FavotireAdapter", "nome " + movie.getTitle());
            holder.img_button_coracao_favorite.setVisibility(View.GONE);

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

            if (rated.contains(movie)) {
                holder.img_button_estrela_favorite.setImageResource(R.drawable.icon_star);
                //  Log.d("FavotireAdapter", "Rated :" + movierated.getId() + " " + movierated.getUserRating());
                holder.text_rated_favoritos.setVisibility(View.VISIBLE);
                holder.text_rated_favoritos.setText(String.valueOf(rated.get(position).getUserRating()));
            }


//            if (watchlist.contains(movie)) {
//                Log.d("FavotireAdapter", "watchlist True:" + favoritos.get(position).getTitle());
//                holder.img_button_relogio_favorite.setImageResource(R.drawable.icon_agenda);
//            }

//            holder.img_button_relogio_favorite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    favoriteOnClickListener.onClickRelogio(view, position, !watchlist.contains(movie));
//                }
//            });
        }
    }


    @Override
    public int getItemCount() {
        return watchlist.size() > 0 ? watchlist.size() : 0;
    }

    public interface FavoriteOnClickListener {
        void onClickCoracao(View view, int posicao, boolean AddOrRemove);

        void onClickEstrela(View view, int posicao, boolean AddOrRemove);

        void onClickRelogio(View view, int posicao, boolean AddOrRemove);
    }

    public static class WatchViewHolder extends RecyclerView.ViewHolder {
        ImageView img_favorite;
        ImageButton img_button_coracao_favorite, img_button_estrela_favorite, img_button_relogio_favorite;
        ProgressBar progressBar;
        TextView text_rated_favoritos;

        public WatchViewHolder(View itemView) {
            super(itemView);
            img_favorite = (ImageView) itemView.findViewById(R.id.img_filme_usuario);
            img_button_coracao_favorite = (ImageButton) itemView.findViewById(R.id.img_button_coracao_usuario);
            img_button_estrela_favorite = (ImageButton) itemView.findViewById(R.id.img_button_estrela_usuario);
            img_button_relogio_favorite = (ImageButton) itemView.findViewById(R.id.img_button_relogio_usuario);
            text_rated_favoritos = (TextView) itemView.findViewById(R.id.text_rated_favoritos);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }

}
