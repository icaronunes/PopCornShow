package adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import utils.UtilsFilme;

/**
 * Created by icaro on 08/07/16.
 */
public class SearchAdapter extends BaseAdapter {

    List<MovieDb> movieDbList;
    Context context;

    public SearchAdapter(Context context, List<MovieDb> movieDbList) {

        this.context = context;
        this.movieDbList = movieDbList;
        Log.d("Construtor", String.valueOf("" + movieDbList == null));

    }

    @Override
    public int getCount() {
        if (movieDbList == null) {
            Log.d("getCount", String.valueOf("" + movieDbList == null));
            return 0;
        } else {
            return movieDbList.size();
        }

    }

    @Override
    public Object getItem(int i) {
        Log.d("getItem", String.valueOf(movieDbList.get(i).getTitle() == null));
        return movieDbList.get(i);
    }

    @Override
    public long getItemId(int i) {
        Log.d("getItemId", "" + i);
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        MovieDb movieDb = movieDbList.get(position);
        Log.d("id", "" + movieDb.getId());

        View layout = LayoutInflater.from(context).inflate(R.layout.search_list_adapter, viewGroup, false);
        ImageView imageView = (ImageView) layout.findViewById(R.id.img_search);
        TextView search_nome = (TextView) layout.findViewById(R.id.search_nome);
        TextView search_data_lancamento = (TextView) layout.findViewById(R.id.search_data_lancamento);
        TextView search_voto_media = (TextView) layout.findViewById(R.id.search_voto_media);
        TextView search_title_original = (TextView) layout.findViewById(R.id.search_title_original);
        search_nome.setText(movieDb.getTitle());
        Log.d("SearchAdapter", "similares_nome :" + movieDb.getTitle());
        search_data_lancamento.setText(movieDb.getReleaseDate());
        Log.d("SearchAdapter", "similares_data_lancamento :" + movieDb.getReleaseDate());
        search_title_original.setText(movieDb.getOriginalTitle());
        Log.d("SearchAdapter", "similares_data_lancamento :" + movieDb.getOriginalTitle());
        search_voto_media.setText(Float.toString(movieDb.getVoteAverage()));
        Picasso.with(context)
                .load(UtilsFilme.getBaseUrlImagem(2) + movieDb.getPosterPath())
                .placeholder(R.drawable.poster_empty)
                .into(imageView);
        Log.d("poster", "" + movieDb.getPosterPath());

        return layout;
    }
}
