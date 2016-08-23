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
import com.squareup.picasso.Picasso;

import activity.FilmeActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonCrewsAdapter extends RecyclerView.Adapter<PersonCrewsAdapter.PersonCrewsViewHolder> {
    Context context;
    PersonCredits personCredits;
    PersonCredit movie;

    public PersonCrewsAdapter(Context context, PersonCredits personCredits) {

        this.context = context;
        this.personCredits = personCredits;
    }

    @Override
    public PersonCrewsAdapter.PersonCrewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false);
        PersonCrewsViewHolder holder = new PersonCrewsViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final PersonCrewsAdapter.PersonCrewsViewHolder holder, int position) {

        movie = personCredits.getCrew().get(position);
        final int id = movie.getId();

        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + movie.getPosterPath())
                .placeholder(R.drawable.poster_empty)
                .into(holder.poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.title.setVisibility(View.VISIBLE);
                        holder.title.setText(movie.getMovieTitle() + " - " +movie.getReleaseDate());
                    }
                });
        
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilmeActivity.class);
                ImageView imageView = (ImageView) view;
                int color = UtilsFilme.loadPalette(imageView);
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.FILME_ID, id);
                intent.putExtra(Constantes.NOME_FILME, movie.getMovieTitle());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (personCredits.getCrew() != null) {
            return personCredits.getCrew().size();
        }
        return 0;
    }


    public class PersonCrewsViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        ImageView poster;
        TextView title;

        public PersonCrewsViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.img_poster_grid);
            title = (TextView) itemView.findViewById(R.id.text_title_crew);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
        }
    }

}
