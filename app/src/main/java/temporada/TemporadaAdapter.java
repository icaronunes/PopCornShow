package temporada;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.crashlytics.android.Crashlytics;
import com.squareup.otto.Subscribe;
import com.squareup.picasso.Picasso;

import applicaton.PopCornApplication;
import br.com.icaro.filme.R;
import domain.EpisodesItem;
import domain.UserEp;
import domain.UserSeasons;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.UtilsApp;

/**
 * Created by icaro on 26/08/16.
 */
public class TemporadaAdapter extends RecyclerView.Adapter<TemporadaAdapter.HoldeTemporada> {

    public static final String TAG = TemporadaAdapter.class.getName();

    private Context context;
    private TvSeason tvSeason;
    private UserSeasons seasons;
    private boolean seguindo;
    private TemporadaOnClickListener temporadaOnClickListener;

    public interface TemporadaOnClickListener {
        void onClickVerTemporada(View view, int position);
        void onClickTemporada(View view, int position);
        void onClickTemporadaNota(View view, EpisodesItem ep, int position, UserEp userEp);
        void onClickScrool(int position);
    }

    public TemporadaAdapter(TemporadaActivity temporadaActivity, TvSeason tvSeason,
                            UserSeasons seasons, boolean seguindo, TemporadaOnClickListener temporadaOnClickListener) {

        this.tvSeason = tvSeason;
        this.context = temporadaActivity;
        this.seasons = seasons;
        this.seguindo = seguindo;
        this.temporadaOnClickListener = temporadaOnClickListener;
          PopCornApplication.getInstance().getBus().register(this);
    }


    @Override
    public HoldeTemporada onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.temporada_epsodio_layout, parent, false);

        return new HoldeTemporada(view);
    }

    @Subscribe
    public void onBusAtualizarListaCarros(UserSeasons seasons) {
        //seasons.getUserEps().get(position).setAssistido(!seasons.getUserEps().get(position).isAssistido());
        //  Log.d(TAG, "onBusAtualizarListaCarros: "+ seasons.toString());
        this.seasons = seasons;
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        PopCornApplication.getInstance().getBus().unregister(this);
    }

    @Override
    public void onBindViewHolder(HoldeTemporada holder, final int position) {
        try {
            TvEpisode episode = tvSeason.getEpisodes().get(position);

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

            Picasso.get()
                    .load(UtilsApp.INSTANCE.getBaseUrlImagem(UtilsApp.INSTANCE.getTamanhoDaImagem(context, 2)) + episode.getStillPath())
                    .error(R.drawable.empty_popcorn)
                    .into(holder.poster);

            if (!seguindo) {
                holder.bt_visto.setVisibility(View.GONE);
            }

            if (seasons != null && seguindo) {
                if (seasons.getUserEps().get(position).isAssistido()) {
                    holder.bt_visto.setImageResource(R.drawable.icon_visto);
                } else {
                    holder.bt_visto.setImageResource(R.drawable.icon_movie_now);
                }
            }


            holder.bt_visto.setOnClickListener(v -> temporadaOnClickListener.onClickVerTemporada(v, position));

            holder.itemView.setOnClickListener(view -> temporadaOnClickListener.onClickTemporada(view, position));
        } catch (Exception e) {
            Crashlytics.logException(e);
            Toast.makeText(context, R.string.ops_seguir_novamente, Toast.LENGTH_SHORT).show();
        }

    }


    @Override
    public int getItemCount() {
        if (tvSeason.getEpisodes() != null && !seguindo) {
            return tvSeason.getEpisodes().size();
        }
        if (seasons.getUserEps() != null && seguindo) {
            return seasons.getUserEps().size();
        }
        return 0;
    }


    public class HoldeTemporada extends RecyclerView.ViewHolder {

        private TextView nome, numero, nota, data;
        private ImageView poster, bt_visto;

        public HoldeTemporada(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.ep_name);
            numero = itemView.findViewById(R.id.ep_number);
            nota = itemView.findViewById(R.id.ep_nota);
            data = itemView.findViewById(R.id.ep_date);
            poster = itemView.findViewById(R.id.image_temp_ep);
            bt_visto = itemView.findViewById(R.id.bt_visto);
        }
    }

}
