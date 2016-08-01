package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FavotireActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.UtilsFilme;

/**
 * Created by icaro on 01/08/16.
 */
public class FavotireAdapter extends RecyclerView.Adapter<FavotireAdapter.FavoriteViewHolder> {

    List<MovieDb> movieDb;
    Context context;

    public FavotireAdapter(FavotireActivity context, List<MovieDb> results) {
        movieDb = results;
        this.context = context;
        Log.d("FavotireAdapter", "Total " + movieDb.size());
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = (View) LayoutInflater.from(context).inflate(R.layout.favorite_list_adapter, parent, false);
        FavoriteViewHolder holder = new FavoriteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        MovieDb movie = movieDb.get(position);
        Log.d("FavotireAdapter", "onBindViewHolder ");
        if (movie != null) {
            Log.d("FavotireAdapter", "nome " + movie.getTitle());
            holder.nome.setText(movie.getTitle());
            holder.title_original.setText(movie.getReleaseDate());
            Log.d("FavotireAdapter", "Lancamento " +movie.getReleaseDate());

            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath()).into(holder.imageView);

        }
    }


    @Override
    public int getItemCount() {
        Log.d("FavotireAdapter", "getItemCount " +movieDb.size());
        return movieDb.size() > 0 ? movieDb.size() : 0;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView nome;
        TextView title_original;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img_favorite);
            nome = (TextView) itemView.findViewById(R.id.favorite_nome);
            title_original = (TextView) itemView.findViewById(R.id.favorite_title_original);
        }
    }
}
