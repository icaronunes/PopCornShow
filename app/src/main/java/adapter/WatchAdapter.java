//package adapter;
//
//import android.content.Context;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageButton;
//import android.widget.ImageView;
//import android.widget.ProgressBar;
//import android.widget.TextView;
//
//import com.squareup.picasso.Callback;
//import com.squareup.picasso.Picasso;
//
//import java.util.List;
//
//import activity.WatchListActivity2;
//import br.com.icaro.filme.R;
//import info.movito.themoviedbapi.model.MovieDb;
//import utils.UtilsFilme;
//
//
///**
// * Created by icaro on 01/08/16.
// */
//public class WatchAdapter extends RecyclerView.Adapter<WatchAdapter.WatchViewHolder> {
//
//    List<MovieDb> watchlist;
//    Context context;
//    WatchListOnClickListener watchListOnClickListener;
//
//    public WatchAdapter(WatchListActivity2 watchListActivity, List<MovieDb> watchlist, WatchListOnClickListener onClickListener) {
//        this.context = watchListActivity;
//        this.watchlist = watchlist;
//        this.watchListOnClickListener = onClickListener;
//    }
//
//    @Override
//    public WatchAdapter.WatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(context).inflate(R.layout.usuario_list_adapter, parent, false);
//        WatchViewHolder holder = new WatchViewHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(final WatchViewHolder holder, final int position) {
//
//        final MovieDb movie = watchlist.get(position);
//        if (movie != null) {
//
//            holder.img_button_relogio_favorite.setVisibility(View.GONE);
//
//            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
//                    .into(holder.img_favorite, new Callback() {
//                        @Override
//                        public void onSuccess() {
//                            holder.progressBar.setVisibility(View.GONE);
//                        }
//
//                        @Override
//                        public void onError() {
//
//                        }
//                    });
//
//            holder.img_favorite.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    watchListOnClickListener.onClick(view, position);
//                }
//            });
//
//            holder.img_favorite.setOnLongClickListener(new View.OnLongClickListener() {
//                @Override
//                public boolean onLongClick(View view) {
//                    watchListOnClickListener.onClickLong(view, position);
//                    return true;
//                }
//            });
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        if (watchlist != null) {
//            return watchlist.size();
//        }
//        return 0;
//    }
//
//    public interface WatchListOnClickListener {
//        void onClick(View view, int posicao);
//
//        void onClickLong(View view, final int posicao);
//    }
//
//    public static class WatchViewHolder extends RecyclerView.ViewHolder {
//        ImageView img_favorite;
//        ImageButton img_button_coracao_favorite, img_button_estrela_favorite, img_button_relogio_favorite;
//        ProgressBar progressBar;
//        TextView text_rated_favoritos;
//
//        public WatchViewHolder(View itemView) {
//            super(itemView);
//            img_favorite = (ImageView) itemView.findViewById(R.id.img_filme_usuario);
//            img_button_coracao_favorite = (ImageButton) itemView.findViewById(R.id.img_button_coracao_usuario);
//            img_button_estrela_favorite = (ImageButton) itemView.findViewById(R.id.img_button_estrela_usuario);
//            img_button_relogio_favorite = (ImageButton) itemView.findViewById(R.id.img_button_relogio_usuario);
//            text_rated_favoritos = (TextView) itemView.findViewById(R.id.text_rated_favoritos);
//            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
//            itemView.findViewById(R.id.botoes_lista).setVisibility(View.GONE);
//
//        }
//    }
//
//}
