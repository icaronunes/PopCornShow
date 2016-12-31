package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import activity.CrewsActivity;
import activity.ElencoActivity;
import br.com.icaro.filme.R;
import domian.UserTvshow;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 26/08/16.
 */


public class TemporadasAdapter extends RecyclerView.Adapter<TemporadasAdapter.HoldeTemporada> {

    public static final String TAG = TemporadasAdapter.class.getName();

    Context context;
    private TvSeries series;
    private int color;
    private TemporadasOnClickListener onClickListener;
    private UserTvshow userTvshow;

    public interface TemporadasOnClickListener {
        void onClickTemporada(View view, int position, int color);
        void onClickCheckTemporada(View view, int position);
    }

    public TemporadasAdapter(FragmentActivity activity, TvSeries series,
                             TemporadasOnClickListener temporadaOnClickListener, int color, UserTvshow userTvshow) {
        this.context = activity;
        this.series = series;
        this.onClickListener = temporadaOnClickListener;
        this.color = color;
        this.userTvshow = userTvshow;
    }

    @Override
    public TemporadasAdapter.HoldeTemporada onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.season_layout, parent, false);
        HoldeTemporada holder = new HoldeTemporada(view);
        return holder;
    }

    private void showPopUp(View ancoraView, final int seasonNumber) {
        if (ancoraView != null) {
            PopupMenu popupMenu = new PopupMenu(context, ancoraView);
            popupMenu.inflate(R.menu.menu_popup_temporada);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.elenco_temporada: {
                            Intent intent = new Intent(context, ElencoActivity.class);
                            intent.putExtra(Constantes.MEDIATYPE, series.getMediaType());
                            intent.putExtra(Constantes.ID, series.getId());
                            intent.putExtra(Constantes.TVSEASONS, seasonNumber);
                            intent.putExtra(Constantes.NOME, series.getName());
                            context.startActivity(intent);
                            return true;
                        }

                        case R.id.producao_temporada: {
                            Intent intent = new Intent(context, CrewsActivity.class);
                            intent.putExtra(Constantes.MEDIATYPE, series.getMediaType());
                            intent.putExtra(Constantes.ID, series.getId());
                            intent.putExtra(Constantes.TVSEASONS, seasonNumber);
                            intent.putExtra(Constantes.NOME, series.getName());
                            context.startActivity(intent);
                            return true;
                        }

                    }
                    return true;
                }
            });
            popupMenu.show();
        }
    }


    @Override
    public void onBindViewHolder(final TemporadasAdapter.HoldeTemporada holder, final int position) {

        holder.temporada.setText(context.getString(R.string.temporada) + " " + series.getSeasons().get(position).getSeasonNumber());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + series.getSeasons().get(position).getPosterPath())
                .error(R.drawable.poster_empty)
                .into(holder.image_temporada);
        holder.data.setText(series.getSeasons().get(position).getAirDate() != null ? series.getSeasons().get(position).getAirDate() : "");
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClickTemporada(view, position, color);
            }
        });


        holder.popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPopUp(view, series.getSeasons().get(position).getSeasonNumber());
            }
        });

        holder.bt_seguindo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickListener.onClickCheckTemporada(view, position);
            }
        });

        if (userTvshow == null ){
            holder.bt_seguindo.setVisibility(View.GONE);
        } else {
            if (isVisto(position)) {
                holder.bt_seguindo.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_visto));
            } else {
                holder.bt_seguindo.setImageDrawable(context.getResources().getDrawable(R.drawable.icon_movie_now));
            }
        }

    }

    private boolean isVisto(int position) {
        if (userTvshow != null) {
                return userTvshow.getSeasons().get(position).isVisto();
        } else {
            return false;
        }
    }


    @Override
    public int getItemCount() {

        if (series.getNumberOfSeasons() > 0) {
            return series.getSeasons().size();
        }
        return 0;
    }

    public class HoldeTemporada extends RecyclerView.ViewHolder {

        TextView temporada, data, numero_ep;
        ImageView image_temporada;
        ImageButton popup;
        ImageView bt_seguindo;

        public HoldeTemporada(View itemView) {
            super(itemView);
            image_temporada = (ImageView) itemView.findViewById(R.id.image_temporada);
            numero_ep = (TextView) itemView.findViewById(R.id.numero_epsodios_temporada);
            data = (TextView) itemView.findViewById(R.id.date_temporada);
            temporada = (TextView) itemView.findViewById(R.id.temporada);
            popup = (ImageButton) itemView.findViewById(R.id.popup_temporada);
            bt_seguindo = (ImageView) itemView.findViewById(R.id.bt_assistido);
        }
    }
}
