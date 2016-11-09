package activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;
import com.tr4android.recyclerviewslideitem.SwipeAdapter;
import com.tr4android.recyclerviewslideitem.SwipeConfiguration;

import adapter.TemporadaAdapter;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import info.movito.themoviedbapi.model.tv.TvSeason;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 09/11/16.
 */
public class SampleAdapter extends SwipeAdapter implements View.OnClickListener {

    int[] colors = new int[]{R.color.red, R.color.gray, R.color.green, R.color.yellow};

    private Context mContext;
    private RecyclerView mRecyclerView;
    TvSeason tvSeason;
    String nome_serie, nome_temporada;
    TvEpisode episode;
    int serie_id, color;

    public SampleAdapter(Context context, RecyclerView recyclerView, TvSeason tvSeason,
                         int serie_id, String nome, int color, String nome_temporada) {
        mContext = context;
        mRecyclerView = recyclerView;
        this.tvSeason = tvSeason;
        this.serie_id = serie_id;
        this.nome_serie = nome;
        this.color = color;
        this.nome_temporada = nome_temporada;

    }

    public class SampleViewHolder extends RecyclerView.ViewHolder {

        TextView nome, numero, nota, data;
        ImageView poster;

        public SampleViewHolder(View view) {
            super(view);
            nome = (TextView) itemView.findViewById(R.id.ep_name);
            numero = (TextView) itemView.findViewById(R.id.ep_number);
            nota = (TextView) itemView.findViewById(R.id.ep_nota);
            data = (TextView) itemView.findViewById(R.id.ep_date);
            poster = (ImageView) itemView.findViewById(R.id.image_temp_ep);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateSwipeViewHolder(ViewGroup parent, int i) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.temporada_epsodio_layout, parent, true);
        return new SampleViewHolder(v);
    }

    @Override
    public void onBindSwipeViewHolder(RecyclerView.ViewHolder Viewholder, final int position) {
        SampleViewHolder holder = (SampleViewHolder) Viewholder;
        ShapeDrawable drawable = new ShapeDrawable(new OvalShape());
        drawable.getPaint().setColor(mContext.getResources().getColor(colors[((int) (Math.random() * (colors.length - 1)))]));

        episode = tvSeason.getEpisodes().get(position);

        holder.numero.setText(mContext.getString(R.string.epsodio) + " " + episode.getEpisodeNumber());

        holder.data.setText(episode.getAirDate() != null ? episode.getAirDate() : mContext.getString(R.string.sem_data));
        holder.nome.setText(episode.getName() != "" ? episode.getName() : mContext.getString(R.string.sem_nome));
        if (episode.getVoteAverage() > 0) {
            String votos = (String) String.valueOf(episode.getVoteAverage()).subSequence(0, 3);
            if (episode.getVoteAverage() < 10) {
                holder.nota.setText(votos + "/" + episode.getVoteCount());
            } else {
                votos = votos.replace(".", "");
                holder.nota.setText(votos + "/" + episode.getVoteCount());
            }
        } else {
            holder.nota.setText(mContext.getString(R.string.sem_nota));
        }

        Log.d("Temporada", "Rating " + episode.getUserRating());
        Picasso.with(mContext).load(UtilsFilme.getBaseUrlImagem(4) + episode.getStillPath())
                .error(R.drawable.top_empty)
                .into(holder.poster);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, EpsodioActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, serie_id);
                intent.putExtra(Constantes.TVSEASON_ID, tvSeason.getId());
                intent.putExtra(Constantes.EPSODIO_ID, episode.getId());
                intent.putExtra(Constantes.POSICAO, position);
                intent.putExtra(Constantes.TVSEASONS, tvSeason);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.NOME_TVSHOW, nome_serie);
                intent.putExtra(Constantes.NOME, nome_temporada);
                mContext.startActivity(intent);

                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(mContext);
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
    public SwipeConfiguration onCreateSwipeConfiguration(Context context, final int position) {
        return new SwipeConfiguration.Builder(context)
//                .setLeftBackgroundColorResource(R.color.gray)
//                .setDrawableResource(R.drawable.ic_action_remove)
//                .setLeftUndoable(true)
//                .setLeftUndoDescription(R.string.action_undo)
//                .setDescriptionTextColorResource(android.R.color.white)
//                .setLeftSwipeBehaviour(SwipeConfiguration.SwipeBehaviour.NORMAL_SWIPE)
                .setDrawableResource(R.drawable.icon_movie_now)
                .setBackgroundColor(R.color.primary_dark)
                .build();
    }

    @Override
    public void onSwipe(final int position, final int direction) {
        if (direction == SWIPE_LEFT) {
            Toast toast = Toast.makeText(mContext, "Deleted item at position " + position, Toast.LENGTH_SHORT);
            toast.show();
        } else {
            Toast toast = Toast.makeText(mContext, "Marked item as read at position " + position, Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onClick(View view) {
        // We need to get the parent of the parent to actually have the proper view
        int position = mRecyclerView.getChildAdapterPosition((View) view.getParent().getParent());
        Toast toast = Toast.makeText(mContext, "Clicked item at position " + position, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public int getItemCount() {
        return tvSeason.getEpisodes().size();
    }
}