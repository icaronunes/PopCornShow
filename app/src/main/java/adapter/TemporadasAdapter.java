package adapter;

import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.UtilsFilme;

/**
 * Created by icaro on 26/08/16.
 */
public class TemporadasAdapter extends RecyclerView.Adapter<TemporadasAdapter.HoldeTemporada> {

    Context context;
    TvSeries series;
    TemporadaOnClickListener onClickListener;


    public TemporadasAdapter(FragmentActivity activity, TvSeries series, TemporadaOnClickListener temporadaOnClickListener) {
        this.context = activity;
        this.series = series;
        this.onClickListener = temporadaOnClickListener;
    }

    @Override
    public TemporadasAdapter.HoldeTemporada onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.season_layout, parent, false);
        HoldeTemporada holder = new HoldeTemporada(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(TemporadasAdapter.HoldeTemporada holder, final int position) {

        Log.d("Position", "numero " + position);
        holder.temporada.setText(context.getString(R.string.temporada) + " " + series.getSeasons().get(position).getSeasonNumber());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + series.getSeasons().get(position).getPosterPath())
                .error(R.drawable.poster_empty)
                .into(holder.image_temporada);
        holder.data.setText(series.getSeasons().get(position).getAirDate() != null ? series.getSeasons().get(position).getAirDate() : "");
        //holder.numero_ep.setText("Rated " + series.getSeasons().get(position).getEpisodes().size());
        //NÃO É POSSIVEL PEGAR INFORMAÇÕES DOS EPSODIOS.
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClickTemporada(view, position);
            }
        });

    }


    @Override
    public int getItemCount() {

        if (series.getNumberOfSeasons() > 0) {
            Log.d("getNumberOfSeason", "" + series.getSeasons().size());
            return series.getSeasons().size();
        }
        return 0;
    }

    public interface TemporadaOnClickListener {
        void onClickTemporada(View view, int position);
    }

    public class HoldeTemporada extends RecyclerView.ViewHolder {

        TextView temporada, data, numero_ep;
        ImageView image_temporada;

        public HoldeTemporada(View itemView) {
            super(itemView);
            image_temporada = (ImageView) itemView.findViewById(R.id.image_temporada);
            numero_ep = (TextView) itemView.findViewById(R.id.numero_epsodios_temporada);
            data = (TextView) itemView.findViewById(R.id.date_temporada);
            temporada = (TextView) itemView.findViewById(R.id.temporada);
        }
    }
}
