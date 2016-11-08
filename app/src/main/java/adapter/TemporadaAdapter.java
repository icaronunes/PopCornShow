package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
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
    String nome_serie, nome_temporada;
    TvEpisode episode;
    int serie_id, color;

    public TemporadaAdapter(TemporadaActivity temporadaActivity, TvSeason tvSeason,
                            int serie_id, String nome, int color, String nome_temporada) {

        this.tvSeason = tvSeason;
        this.context = temporadaActivity;
        this.serie_id = serie_id;
        this.nome_serie = nome;
        this.color = color;
        this.nome_temporada = nome_temporada;
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
        if (episode.getVoteAverage() > 0) {
            String votos = (String) String.valueOf(episode.getVoteAverage()).subSequence(0, 3);
            if (episode.getVoteAverage() < 10) {
                holder.nota.setText(votos + "/" + episode.getVoteCount());
            } else {
                votos = votos.replace(".", "");
                holder.nota.setText(votos + "/" + episode.getVoteCount());
            }
        } else {
            holder.nota.setText(context.getString(R.string.sem_nota));
        }

        Log.d("Temporada", "Rating " + episode.getUserRating());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(4) + episode.getStillPath())
                .error(R.drawable.top_empty)
                .into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, EpsodioActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, serie_id);
                intent.putExtra(Constantes.TVSEASON_ID, tvSeason.getId());
                intent.putExtra(Constantes.EPSODIO_ID, episode.getId());
                intent.putExtra(Constantes.POSICAO, position);
                intent.putExtra(Constantes.TVSEASONS, tvSeason);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.NOME_TVSHOW, nome_serie);
                intent.putExtra(Constantes.NOME, nome_temporada);
                context.startActivity(intent);

                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TemporadaAdapter.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, tvSeason.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, tvSeason.getName());
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, EpsodioActivity.class.getName());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

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
