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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import activity.FilmeActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import domain.Lista;
import domain.ListaItem;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 14/08/16.
 */
public class ListUserAdapter extends RecyclerView.Adapter<ListUserAdapter.ListViewHolder> {

    private FirebaseAnalytics mFirebaseAnalytics;
    private List<ListaItem> lista;
    private Context context;

    public ListUserAdapter(Context listaUserActivity) {
        this.context = listaUserActivity;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
        lista = new ArrayList<>();
    }

    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.lista, parent, false);
        ListUserAdapter.ListViewHolder holder = new ListViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final ListViewHolder holder, int position) {

        final ListaItem movie = lista.get(position);

            if (movie.getReleaseDate() != null && movie.getReleaseDate().length() >= 4){
                holder.release.setText(movie.getReleaseDate().substring(0,4));
            }


            if (movie != null) {
                Picasso.with(context)
                        .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 3)) + movie.getPosterPath())
                        .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
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
                        intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), movie.getId());
                        intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), movie.getTitle());
                        intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsFilme.loadPalette(holder.img_rated));
                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(movie.getId()));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    } else {
                        Intent intent = new Intent(context, FilmeActivity.class);
                        intent.putExtra(Constantes.INSTANCE.getFILME_ID(), movie.getId());
                        intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), movie.getTitle());
                        intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsFilme.loadPalette(holder.img_rated));
                        context.startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(movie.getId()));
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    }
                }
            });

    }

    public void addPersonPopular(Lista listaMedia) {

       // int initPosition = lista.size() - 1;
        //this.lista.remove(initPosition);
      //  notifyItemRemoved(initPosition);

        // insert news and the loading at the end of the list
        this.lista.addAll(listaMedia.getResults());
        //games?.add(loadingItem)
        //notifyItemRangeChanged(initPosition, this.lista.size() + 1 /* plus loading item */)
        //personResultsPage.add(loadingItem)
        notifyDataSetChanged();

    }

    @Override
    public int getItemCount() {
        if (lista != null) {
            return lista.size();
        }
        return 0;
    }

    class ListViewHolder extends RecyclerView.ViewHolder {

        private ImageView img_rated;
        private ProgressBar progressBar;
        private TextView release;

        ListViewHolder(View itemView) {
            super(itemView);
            img_rated = (ImageView) itemView.findViewById(R.id.img_lista);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            release  = (TextView) itemView.findViewById(R.id.date_oscar);
        }
    }
}
