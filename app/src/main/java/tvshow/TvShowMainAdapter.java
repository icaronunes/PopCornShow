package tvshow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import br.com.icaro.filme.R;
import domain.ListaItemSerie;
import domain.ListaSeries;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;


/**
 * Created by icaro on 17/02/17.
 */
@Keep
public class TvShowMainAdapter extends RecyclerView.Adapter<TvShowMainAdapter.TvShowPopularesViewHolder>{
    private Context context;
    private ListaSeries popularTvshow;

    public TvShowMainAdapter(FragmentActivity activity, ListaSeries popularTvshow) {
        context = activity;
        this.popularTvshow = popularTvshow;
    }

    @Override
    public TvShowMainAdapter.TvShowPopularesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_main, parent, false);
        return new TvShowPopularesViewHolder(view);
    }

    @Override
    @Keep
    public void onBindViewHolder(@NonNull final TvShowMainAdapter.TvShowPopularesViewHolder holder, final int position) {
        final ListaItemSerie series = popularTvshow.getResults().get(position);

        Picasso.get()
                .load(UtilsApp.getBaseUrlImagem( UtilsApp.getTamanhoDaImagem(context, 2)) + series.getPosterPath())
                .error(R.drawable.poster_empty)
                .into(holder.img_poster_grid, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress_poster_grid.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError(Exception e) {
                        holder.progress_poster_grid.setVisibility(View.GONE);
                        holder.title_main.setText(series.getName());
                        holder.title_main.setVisibility(View.VISIBLE);
                    }

                });

        holder.img_poster_grid.setOnClickListener(view -> {

            Intent intent = new Intent(context, TvShowActivity.class);
            intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), series.getName());
            intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), series.getId());
            intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.loadPalette(holder.img_poster_grid));
            context.startActivity(intent);
        });

    }

    @Override
    public int getItemCount() {
        if (popularTvshow != null){
            return  popularTvshow.getResults().size() < 15 ? popularTvshow.getResults().size() : 15;
        }
        return  0;
    }

    @Keep
    class TvShowPopularesViewHolder extends RecyclerView.ViewHolder {

       private TextView title_main;
        private ProgressBar progress_poster_grid;
        private ImageView img_poster_grid;


        TvShowPopularesViewHolder(View itemView) {
            super(itemView);
            title_main = (TextView) itemView.findViewById(R.id.title_main);
            progress_poster_grid = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
            img_poster_grid = (ImageView) itemView.findViewById(R.id.img_poster_grid);

        }
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }
}
