package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import activity.EpsodioActivity;
import activity.TemporadaActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 26/08/16.
 */
public class TemporadaAdapter extends RecyclerView.Adapter<TemporadaAdapter.HoldeTemporada> {
    Context context;
    TvSeason tvSeason;
    TvEpisode episode;
    int serie_id;

    public TemporadaAdapter(TemporadaActivity temporadaActivity, TvSeason tvSeason, int serie_id) {

        this.tvSeason = tvSeason;
        this.context = temporadaActivity;
        this.serie_id = serie_id;
    }

    @Override
    public HoldeTemporada onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.temporada_epsodio_layout, parent, false);
        HoldeTemporada holdeTemporada = new HoldeTemporada(view);

        return holdeTemporada;
    }

    @Override
    public void onBindViewHolder(HoldeTemporada holder, final int position) {
        episode = tvSeason.getEpisodes().get(position);

        holder.numero.setText(context.getString(R.string.epsodio) + " " + episode.getEpisodeNumber());

        holder.data.setText(episode.getAirDate() != null ? episode.getAirDate() : context.getString(R.string.sem_data));
        holder.nome.setText(episode.getName() != "" ? episode.getName() : context.getString(R.string.sem_nome));
        holder.nota.setText(episode.getVoteAverage() > 0 ? String.valueOf(episode.getVoteAverage()) : context.getString(R.string.sem_nota));

        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(4) + episode.getStillPath())
                .into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EpsodioActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, serie_id);
                intent.putExtra(Constantes.TVSEASON_ID, tvSeason.getId());
                intent.putExtra(Constantes.EPSODIO_ID, episode.getId());
                intent.putExtra(Constantes.POSICAO, position);
                intent.putExtra("teste", tvSeason);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (tvSeason.getEpisodes() != null) {
            return tvSeason.getEpisodes().size();
        }
        return 0;
    }

    public class HoldeTemporada extends RecyclerView.ViewHolder {

        TextView nome, numero, nota, data;
        ImageView poster;

        public HoldeTemporada(View itemView) {
            super(itemView);
            nome = (TextView) itemView.findViewById(R.id.ep_name);
            numero = (TextView) itemView.findViewById(R.id.ep_number);
            nota = (TextView) itemView.findViewById(R.id.ep_nota);
            data = (TextView) itemView.findViewById(R.id.ep_date);
            poster = (ImageView) itemView.findViewById(R.id.image_temp_ep);
        }
    }

}
