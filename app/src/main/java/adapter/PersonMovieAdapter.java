package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import domain.person.CastItem;
import filme.activity.FilmeActivity;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonMovieAdapter extends RecyclerView.Adapter<PersonMovieAdapter.PersonMovieViewHolder> {
    private Context context;
    private List<CastItem> personCredits;

    public PersonMovieAdapter(Context context, List<CastItem> personCredits) {
        this.context = context;
        this.personCredits = personCredits;

    }

    @Override
    public PersonMovieAdapter.PersonMovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.person_movie_filmes_layout, parent, false);
        return new PersonMovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonMovieAdapter.PersonMovieViewHolder holder, int position) {

        final CastItem credit = personCredits.get(position);
        if (credit != null) {

            Picasso.with(context)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context , 2)) + credit.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.title.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.title.setVisibility(View.VISIBLE);
                            holder.title.setText(credit.getTitle() + "- " + credit.getReleaseDate());//TODO null e formatar texto
                        }
                    });

            holder.poster.setOnClickListener(view -> {
                Intent intent = new Intent(context, FilmeActivity.class);
                ImageView imageView = (ImageView) view;
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.loadPalette(imageView));
                intent.putExtra(Constantes.INSTANCE.getFILME_ID(), credit.getId());
                intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), credit.getTitle());
                context.startActivity(intent);
            });
        }

    }

    @Override
    public int getItemCount() {
        if (personCredits != null) {
            return personCredits.size();
        } else {
            return 0;
        }
    }

    class PersonMovieViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private ImageView poster;
        private TextView title;

        PersonMovieViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.img_poster_grid);
            title = (TextView) itemView.findViewById(R.id.text_title_crew);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
        }
    }
}
