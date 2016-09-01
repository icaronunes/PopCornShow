package adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import activity.ListaUserActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import utils.Constantes;
import utils.UtilsFilme;

import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.lists;

/**
 * Created by icaro on 14/08/16.
 */
public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListViewHolder> {


    List<MovieDb> lista;
    Context context;


    public ListUserAdapter(ListaUserActivity listaUserActivity, List<MovieDb> movieLists) {
        this.context = listaUserActivity;
        lista = movieLists;
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuario_list_adapter, parent, false);
        ListUserAdapter.ListViewHolder holder = new ListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

            final MovieDb listaMovie = lista.get(position);

            if (listaMovie != null) {
                Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + listaMovie.getPosterPath())
                        .into(holder.img_rated, new Callback() {
                            @Override
                            public void onSuccess() {
                                holder.progressBar.setVisibility(View.GONE);
                            }

                            @Override
                            public void onError() {
                                holder.progressBar.setVisibility(View.GONE);
                            }
                        });
            }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //NÃO FUNCIONA. METODO FilmeService.getTmdbTvShow()
                //.getSeries() NÃO TRAS O TIPO DE MEDIA
                if (listaMovie.getMediaType().equals(Multi.MediaType.TV_SERIES)){
                    Intent intent = new Intent(context, TvShowActivity.class);
                    intent.putExtra(Constantes.TVSHOW_ID, listaMovie.getId());
                    intent.putExtra(Constantes.NOME_TVSHOW, listaMovie.getTitle());
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, FilmeActivity.class);
                    intent.putExtra(Constantes.FILME_ID, listaMovie.getId());
                    context.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if (lista != null) {
            return lista.size();
        }
        return 0;
    }

    public class ListViewHolder extends RecyclerView.ViewHolder {

        ImageView img_rated;
        ProgressBar progressBar;

        public ListViewHolder(View itemView) {
            super(itemView);
            img_rated = (ImageView) itemView.findViewById(R.id.img_filme_usuario);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            itemView.findViewById(R.id.botoes_lista).setVisibility(View.GONE);
        }
    }
}
