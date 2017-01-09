package adapter;

import android.content.Context;
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

import br.com.icaro.filme.R;
import domain.TvshowDB;
import utils.UtilsFilme;


/**
 * Created by icaro on 01/08/16.
 */
public class ListaTvShowAdapter extends RecyclerView.Adapter<ListaTvShowAdapter.FavoriteViewHolder> {

    private List<TvshowDB> tvshows;
    private Context context;
    private ListaOnClickListener onClickListener;
    boolean status = false;

    // Colocar em apenas um lugar
    public interface ListaOnClickListener {
        void onClick(View view, int posicao);
        void onClickLong(View view,final int posicao);
    }

    public ListaTvShowAdapter(Context activity, List<TvshowDB> tvSeries,
                              ListaOnClickListener ratedOnClickListener, boolean b) {
        this.context = activity;
        this.tvshows = tvSeries;
        this.onClickListener  = ratedOnClickListener;
        this.status = b;

    }


    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuario_list_adapter, parent, false);
        FavoriteViewHolder holder = new FavoriteViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, final int position) {

        final TvshowDB series = tvshows.get(position);
       // Log.d("onBindViewHolder", "position" + position);

        if (status) {
            String valor = String.valueOf(series.getNota());
          //  Log.d("Rated", "" + valor);
            if (valor.length() > 3) {
                valor = valor.substring(0, 2);
           //     Log.d("Rated 2", "" + valor);
                holder.text_rated_favoritos.setText(valor);
            }
            holder.text_rated_favoritos.setText(valor);
            holder.text_rated_favoritos.setVisibility(View.VISIBLE);
        }


        if (series != null) {

            holder.img_button_coracao_favorite.setVisibility(View.GONE);

            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + series.getPoster())
                    .error(R.drawable.poster_empty)
                    .into(holder.img_favorite, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                        }
                    });

            holder.img_favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(view, position);
                }
            });

            holder.img_favorite.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onClickListener.onClickLong(view, position);
                    return true;
                }
            });

        }
    }


    @Override
    public int getItemCount() {
        if (tvshows != null) {
            return tvshows.size();
        }
        return 0;
    }

    public static class FavoriteViewHolder extends RecyclerView.ViewHolder {
        ImageView img_favorite;
        ImageButton img_button_coracao_favorite, img_button_estrela_favorite, img_button_relogio_favorite;
        ProgressBar progressBar;
        TextView text_rated_favoritos;

        public FavoriteViewHolder(View itemView) {
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
