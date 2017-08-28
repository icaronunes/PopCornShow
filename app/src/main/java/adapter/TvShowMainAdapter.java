package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Keep;
import android.support.v4.app.FragmentActivity;
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


import activity.TvShowActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.TvResultsPage;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;


/**
 * Created by icaro on 17/02/17.
 */
@Keep
public class TvShowMainAdapter extends RecyclerView.Adapter<TvShowMainAdapter.TvShowPopularesViewHolder>{
    private Context context;
    private TvResultsPage popularTvshow;

    public TvShowMainAdapter(FragmentActivity activity, TvResultsPage popularTvshow) {
        context = activity;
        this.popularTvshow = popularTvshow;
    }

    @Override
    public TvShowMainAdapter.TvShowPopularesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_main, parent, false);
        return new TvShowPopularesViewHolder(view);
    }

    @Override
    @Keep
    public void onBindViewHolder(final TvShowMainAdapter.TvShowPopularesViewHolder holder, final int position) {
        final TvSeries series = popularTvshow.getResults().get(position);

        Picasso.with(context)
                .load(UtilsFilme.getBaseUrlImagem( UtilsFilme.getTamanhoDaImagem(context, 2)) + series.getPosterPath())
                //.placeholder(R.drawable.poster_empty)
                .into(holder.img_poster_grid, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progress_poster_grid.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progress_poster_grid.setVisibility(View.GONE);
                        holder.title_main.setText(series.getName());
                        holder.title_main.setVisibility(View.VISIBLE);
                        holder.img_poster_grid.setImageResource(R.drawable.poster_empty);
                    }
                });

        holder.img_poster_grid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "TvShow");
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent intent = new Intent(context, TvShowActivity.class);
                intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), series.getName());
                intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), series.getId());
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsFilme.loadPalette(holder.img_poster_grid));
                context.startActivity(intent);
            }
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
}
