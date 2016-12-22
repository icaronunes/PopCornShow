package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import domian.ItemsLista;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 14/08/16.
 */
public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListViewHolder> {


    private final FirebaseAnalytics mFirebaseAnalytics;
    private List<ItemsLista> lista;
    Context context;



    public ListUserAdapter(Context listaUserActivity, List<ItemsLista> movieLists) {
        this.context = listaUserActivity;
        lista = movieLists;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista, parent, false);
        ListUserAdapter.ListViewHolder holder = new ListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {


          //  Log.d("domian.Lista", lista.get(position).getMediaType());
            final ItemsLista movie = lista.get(position);

            if (movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4){
                holder.release.setText(movie.getReleaseDate().substring(0,4));
            }


            if (movie != null) {
                Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
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
                    if (movie.getMediaType().equalsIgnoreCase("tv")) {
                        Intent intent = new Intent(context, TvShowActivity.class);
                        intent.putExtra(Constantes.TVSHOW_ID, movie.getId());
                        intent.putExtra(Constantes.NOME_TVSHOW, movie.getTitle());
                        intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(holder.img_rated));
                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(movie.getId()));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    } else {
                        Intent intent = new Intent(context, FilmeActivity.class);
                        intent.putExtra(Constantes.FILME_ID, movie.getId());
                        intent.putExtra(Constantes.NOME_FILME, movie.getTitle());
                        intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(holder.img_rated));
                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(movie.getId()));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
        TextView release;

        public ListViewHolder(View itemView) {
            super(itemView);
            img_rated = (ImageView) itemView.findViewById(R.id.img_lista);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            release  = (TextView) itemView.findViewById(R.id.date_oscar);
        }
    }
}
