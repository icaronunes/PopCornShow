package adapter;

import android.content.Context;
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

import activity.ListaUserActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieList;
import utils.UtilsFilme;

/**
 * Created by icaro on 14/08/16.
 */
public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListViewHolder> {


    List<MovieList> lista;
    Context context;


    public ListUserAdapter(ListaUserActivity listaUserActivity, List<MovieList> movieLists) {
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
        MovieList listaMovie = lista.get(position);
        Log.d("onBindViewHolder", listaMovie.getName());
        Log.d("onBindViewHolder", listaMovie.getId());

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
