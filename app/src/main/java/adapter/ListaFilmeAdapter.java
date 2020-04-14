package adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import domain.MovieDb;
import utils.UtilsApp;


/**
 * Created by icaro on 01/08/16.
 */
public class ListaFilmeAdapter extends RecyclerView.Adapter<ListaFilmeAdapter.FavoriteViewHolder> {

    private List<MovieDb> filmes;
    private Context context;
    private ListaOnClickListener onClickListener;
    private boolean status = false;

    public ListaFilmeAdapter(FragmentActivity favotireActivity, List<MovieDb> favoritos,
                             ListaOnClickListener onClickListener, boolean status) {
        this.context = favotireActivity;
        this.filmes = favoritos;
        this.onClickListener = onClickListener;
        this.status = status;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.usuario_list_adapter, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final FavoriteViewHolder holder, final int position) {

        final MovieDb movie = filmes.get(position);
        if (movie != null) {

        if (status) {
            String valor = String.valueOf(movie.getNota());

            if (valor.length() > 3) {
                valor = valor.substring(0, 2);
                holder.rated.setText(valor);
            }
            holder.rated.setText(valor);
            holder.rated.setVisibility(View.VISIBLE);
        }


            Picasso.get()
                    .load(UtilsApp.INSTANCE
                            .getBaseUrlImagem(UtilsApp.INSTANCE.getTamanhoDaImagem(context, 4)) + movie.getPoster())
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {

                        }
                    });

            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClickListener.onClick(view, position);
                }
            });

            holder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
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
        if (filmes != null) {
            return filmes.size();
        }
        return 0;
    }


    // Colocar em apenas um lugar
    public interface ListaOnClickListener {
        void onClick(View view, int posicao);
        void onClickLong(View view, final int posicao);
    }

     class FavoriteViewHolder extends RecyclerView.ViewHolder {
       private ImageView imageView;
        private ProgressBar progressBar;
        private TextView rated;

        FavoriteViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img_filme_usuario);
            rated = itemView.findViewById(R.id.text_rated_user);
            progressBar = itemView.findViewById(R.id.progress);

        }
    }

}
