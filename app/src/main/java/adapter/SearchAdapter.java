package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import activity.SearchMultiActivity;
import br.com.icaro.filme.R;
import filme.activity.FilmeActivity;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.people.Person;
import info.movito.themoviedbapi.model.tv.TvSeries;
import pessoa.activity.PersonActivity;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 18/09/16.
 */
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.HolderSearch> {

    private Context context;
    private List<Multi> multis;

    public SearchAdapter(SearchMultiActivity searchMultiActivity, List<Multi> movieDbList) {
        context = searchMultiActivity;
        multis = movieDbList;
    }

    @Override
    public HolderSearch onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.search_list_adapter, parent, false);
        HolderSearch holderSearch = new HolderSearch(view);
        return holderSearch;
    }

    @Override
    public void onBindViewHolder(HolderSearch holder, int position) {

        if (multis.get(position).getMediaType().equals(Multi.MediaType.MOVIE)) {
            final MovieDb movieDb = ((MovieDb) multis.get(position));

            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + movieDb.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(context, FilmeActivity.class);
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.loadPalette(imageView));
                    intent.putExtra(Constantes.INSTANCE.getFILME_ID(), movieDb.getId());
                    intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), movieDb.getTitle());
                    context.startActivity(intent);


                }
            });
            if (movieDb.getOriginalTitle() != null)
            holder.search_title_original.setText(movieDb.getOriginalTitle());
            if (movieDb.getVoteAverage() != 0)
            holder.search_voto_media.setText(Float.toString(movieDb.getVoteAverage()));
            if (movieDb.getTitle() != null)
            holder.search_nome.setText(movieDb.getTitle());
            if (movieDb.getReleaseDate() != null)
            holder.search_data_lancamento
                    .setText(movieDb.getReleaseDate().length() >= 4 ? movieDb.getReleaseDate().substring(0,4) : movieDb.getReleaseDate());
            return;
        }

        if (multis.get(position).getMediaType().equals(Multi.MediaType.TV_SERIES)) {
            final TvSeries series = (TvSeries) multis.get(position);
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + series.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(context, TvShowActivity.class);
                    int color = UtilsApp.loadPalette(imageView);
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                    intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), series.getId());
                    intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), series.getName());
                    context.startActivity(intent);
                }
            });

            if (series.getOriginalName() != null)
            holder.search_title_original.setText(series.getOriginalName());
            if (series.getVoteAverage() != 0 )
            holder.search_voto_media.setText(Float.toString(series.getVoteAverage()));
            if (series.getName() != null)
            holder.search_nome.setText(series.getName());
            if (series.getFirstAirDate() != null)
            holder.search_data_lancamento
                    .setText(series.getFirstAirDate().length() >= 4 ? series.getFirstAirDate().substring(0,4) : series.getFirstAirDate() );
            return;
        }

        if (multis.get(position).getMediaType().equals(Multi.MediaType.PERSON)) {
            final Person person = (Person) multis.get(position);
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + person.getProfilePath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ImageView imageView = (ImageView) view.findViewById(R.id.img_search);
                    Intent intent = new Intent(context, PersonActivity.class);
                    int color = UtilsApp.loadPalette(imageView);
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                    intent.putExtra(Constantes.INSTANCE.getPERSON_ID(), person.getId());
                    intent.putExtra(Constantes.INSTANCE.getNOME_PERSON(), person.getName());
                    context.startActivity(intent);
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

    class HolderSearch extends RecyclerView.ViewHolder {

        private ImageView poster, estrela;
        private TextView search_nome, search_data_lancamento, search_voto_media, search_title_original;

        HolderSearch(View itemView) {
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
