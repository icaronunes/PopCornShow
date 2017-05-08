package adapter;

import android.content.Intent;
import android.os.Bundle;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import activity.BaseActivity;
import activity.TemporadaActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.UserEp;
import domain.UserSeasons;
import domain.UserTvshow;
import info.movito.themoviedbapi.TmdbTvEpisodes;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 25/11/16.
 */
public class ProximosAdapter extends RecyclerView.Adapter<ProximosAdapter.CalendarViewHolder> {
    private final String TAG = ProximosAdapter.class.getName();
    private FragmentActivity context;
    private final List<UserTvshow> userTvshows;
    private int color;

    public ProximosAdapter(FragmentActivity activity, List<UserTvshow> userTvshows) {
        this.context = activity;
        this.userTvshows = userTvshows;
        //Log.d(TAG,"ProximosAdapter" );
    }


    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.calendar_adapter, parent, false);
        //Log.d(TAG,"CalendarViewHolder" );
        return new ProximosAdapter.CalendarViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CalendarViewHolder holder, int position) {
        //Log.d(TAG,"onBindViewHolder" );
        int vistos, total;
        final UserTvshow userTvshow = userTvshows.get(position);
        holder.title.setText(userTvshow.getNome());

        vistos  = contagemDeFaltantes(userTvshow);
        total = contamgeTotalEp(userTvshow);
        holder.faltando.setText(vistos + "/" + total);
        holder.progressBar.setMax(userTvshow.getNumberOfEpisodes());
        holder.progressBar.setProgress(vistos);
        getEpTitle(userTvshow, holder.ep_title, holder.proximo, holder.date, holder.itemView, holder.new_seguindo);
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + userTvshow.getPoster())
                .error(R.drawable.poster_empty)
                .into(holder.poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        color = UtilsFilme.loadPalette(holder.poster);
                    }

                    @Override
                    public void onError() {

                    }
                });

        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TvShowActivity.class);
                intent.putExtra(Constantes.NOME_TVSHOW, userTvshow.getNome());
                intent.putExtra(Constantes.TVSHOW_ID, userTvshow.getId());
                intent.putExtra(Constantes.COLOR_TOP, UtilsFilme.loadPalette(holder.poster));
                context.startActivity(intent);

                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(userTvshow.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userTvshow.getNome());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        });
        if ((userTvshow.getNumberOfEpisodes() - vistos) > 0) {
            holder.eps_faltantes.setText("+" + String.valueOf(userTvshow.getNumberOfEpisodes() - vistos));
        }
    }

    private int contamgeTotalEp(UserTvshow userTvshow) {
        int total =0;

        for (UserSeasons seasons : userTvshow.getSeasons()) {
            if (seasons.getSeasonNumber() != 0 && seasons.getUserEps() != null)
            total = total + seasons.getUserEps().size();
        }

        return total;
    }

    private int contagemDeFaltantes(UserTvshow userTvshow) {
        int contagem = 0;
        for (UserSeasons seasons : userTvshow.getSeasons()) {
            if (seasons.getSeasonNumber() != 0) {
                if(seasons.getUserEps() != null)
                for (UserEp userEp : seasons.getUserEps()) {
                        if (userEp.isAssistido()) {
                            contagem = contagem + 1;
                        }

                }
            }
        }
        return contagem;
    }

    @Override
    public int getItemCount() {
      //  Log.d(TAG,"getItemCount" );
        if (userTvshows != null) {
            return userTvshows.size();
        }
        return 0;

    }

    public void getEpTitle(final UserTvshow userTvshow, final TextView ep_title, final TextView proximo,
                           final TextView dataTvshow, final View itemView, final TextView new_seguindo) {
        int posicao = 0; //Gambiara. Tentar arrumar
        for (final UserSeasons seasons : userTvshow.getSeasons()) {
            if (seasons.getSeasonNumber() != 0) {
                for (final UserEp userEp : seasons.getUserEps()) {
                    if (!userEp.isAssistido()) {
                        proximo.setText(String.valueOf("S" + seasons.getSeasonNumber() + "E" + userEp.getEpisodeNumber()));
                        final int finalPosicao = posicao;
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                final TvEpisode tvEpisode = FilmeService.getTmdbTvEpisodes()
                                        .getEpisode(userTvshow.getId(), userEp.getSeasonNumber(), userEp.getEpisodeNumber(),
                                                BaseActivity.getLocale(), TmdbTvEpisodes.EpisodeMethod.external_ids);
                                context.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Date date = null;
                                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                        try {
                                            date = sdf.parse(tvEpisode.getAirDate());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

//                                        if (verificaDataProximaLancamentoDistante(date)){
//                                            itemView.setAlpha((float) 0.5);
//                                            return;
//                                        } TODO não mostrar episodio muito distante. Não funcionando

                                        ep_title
                                                .setText(tvEpisode.getName() != null && !tvEpisode.getName().equals("") ? tvEpisode.getName() :
                                                        context.getResources().getString(R.string.no_epsodio));
                                        dataTvshow.setText(" - " +tvEpisode.getAirDate());


                                        if (UtilsFilme.verificaDataProximaLancamento(date)){
                                            new_seguindo.setVisibility(View.VISIBLE);
                                        } else {
                                            new_seguindo.setVisibility(View.GONE);
                                        }

                                        itemView.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Intent intent = new Intent(context, TemporadaActivity.class);
                                                intent.putExtra(Constantes.TVSHOW_ID, userTvshow.getId());
                                                intent.putExtra(Constantes.TEMPORADA_ID, userEp.getSeasonNumber() );
                                                intent.putExtra(Constantes.TEMPORADA_POSITION, finalPosicao);
                                                intent.putExtra(Constantes.NOME, userTvshow.getNome());
                                                intent.putExtra(Constantes.COLOR_TOP, color);
                                                context.startActivity(intent);

                                                FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(context);
                                                Bundle bundle = new Bundle();
                                                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(userTvshow.getId()));
                                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, userTvshow.getNome());
                                                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                            }
                                        });
                                    }
                                });
                            }
                        }).start();
                        return;
                    }
                }
            }
            posicao++;
        }
    }

    class CalendarViewHolder extends RecyclerView.ViewHolder {

        private ImageView poster;
        private TextView proximo, title, faltando, ep_title, date, eps_faltantes, new_seguindo;
        private ProgressBar progressBar;

        CalendarViewHolder(View itemView) {
            super(itemView);

            poster = (ImageView) itemView.findViewById(R.id.calendar_poster);
            proximo = (TextView) itemView.findViewById(R.id.calendar_proximo_ver);
            title = (TextView) itemView.findViewById(R.id.calendar_title);
            date = (TextView) itemView.findViewById(R.id.calendar_date);
            faltando = (TextView) itemView.findViewById(R.id.calendar_faltante);
            ep_title = (TextView) itemView.findViewById(R.id.calendar_ep_title);
            progressBar = (ProgressBar) itemView.findViewById(R.id.calendar_progress);
            eps_faltantes = (TextView) itemView.findViewById(R.id.calendar_eps_faltantes);
            new_seguindo = (TextView) itemView.findViewById(R.id.new_seguindo);


        }
    }
}
