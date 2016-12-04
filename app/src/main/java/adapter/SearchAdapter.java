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

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FilmeActivity;
import activity.PersonActivity;
import activity.SearchMultiActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 18/09/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.HolderSearch> {
    Context context;
    List<Multi> multis;
    FirebaseAnalytics firebaseAnalytics;

    public SearchAdapter(SearchMultiActivity searchMultiActivity, List<Multi> movieDbList) {
        context = searchMultiActivity;
        multis = movieDbList;
    }

    @Override
    public HolderSearch onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_adapter, parent, false);
        HolderSearch holderSearch = new HolderSearch(view);
        firebaseAnalytics = FirebaseAnalytics.getInstance(context);
        return holderSearch;
    }

    @Override
    public void onBindViewHolder(HolderSearch holder, int position) {

        if (multis.get(position).getMediaType().equals(Multi.MediaType.MOVIE)) {
            final MovieDb movieDb = ((MovieDb) multis.get(position));

            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + movieDb.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(context, FilmeActivity.class);
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                    intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                  //  Log.d("setOnItemClickListener", movieDb.getOriginalTitle());
                    intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                    context.startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, FilmeActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, FilmeActivity.class.getName());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, movieDb.getTitle());
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });

            holder.search_title_original.setText(movieDb.getOriginalTitle());
            holder.search_voto_media.setText(Float.toString(movieDb.getVoteAverage()));
            holder.search_nome.setText(movieDb.getTitle());
            holder.search_data_lancamento.setText(movieDb.getReleaseDate());
            return;
        }

        if (multis.get(position).getMediaType().equals(Multi.MediaType.TV_SERIES)) {
            final TvSeries series = (TvSeries) multis.get(position);
            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + series.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(context, TvShowActivity.class);
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                    intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                  //  Log.d("setOnItemClickListener", series.getName());
                    intent.putExtra(Constantes.NOME_TVSHOW, series.getName());
                    context.startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TvShowActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, TvShowActivity.class.getName());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, series.getName());
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });

            holder.search_title_original.setText(series.getOriginalName());
            holder.search_voto_media.setText(Float.toString(series.getVoteAverage()));
            holder.search_nome.setText(series.getName());
            holder.search_data_lancamento.setText(series.getFirstAirDate());
            return;
        }

        if (multis.get(position).getMediaType().equals(Multi.MediaType.PERSON)) {
            final Person person = (Person) multis.get(position);
            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + person.getProfilePath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(context, PersonActivity.class);
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                    intent.putExtra(Constantes.PERSON_ID, person.getId());
                  //  Log.d("setOnItemClickListener", person.getName());
                    intent.putExtra(Constantes.NOME_PERSON, person.getName());
                    context.startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, PersonActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, person.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_ID, person.getName());
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            });

            holder.search_title_original.setVisibility(View.GONE);
            holder.search_voto_media.setVisibility(View.GONE);
            holder.estrela.setVisibility(View.GONE);
            holder.search_nome.setText(person.getName());
            holder.search_data_lancamento.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        if (multis == null) {
            return 0;
        }
        return multis.size();
    }

    public class HolderSearch extends RecyclerView.ViewHolder {
        ImageView poster, estrela;
        TextView search_nome, search_data_lancamento, search_voto_media, search_title_original;

        public HolderSearch(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.img_search);
            search_nome = (TextView) itemView.findViewById(R.id.search_nome);
            estrela = (ImageView) itemView.findViewById(R.id.estrela);
            search_data_lancamento = (TextView) itemView.findViewById(R.id.search_data_lancamento);
            search_voto_media = (TextView) itemView.findViewById(R.id.search_voto_media);
            search_title_original = (TextView) itemView.findViewById(R.id.search_title_original);

        }
    }
}
