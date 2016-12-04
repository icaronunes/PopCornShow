package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import activity.FilmeActivity;
import activity.TvShowActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 25/09/16.
 */

/**
 * Created by icaro on 18/08/16.
 */
public class PersontvAdapter extends RecyclerView.Adapter<PersontvAdapter.PersonTvViewHolder> {
    Context context;
    PersonCredits personCredits;

    public PersontvAdapter(Context context, PersonCredits personCredits) {

        this.context = context;
        this.personCredits = personCredits;

    }

    @Override
    public PersonTvViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.person_movie_filmes_layout, parent, false);
        PersonTvViewHolder holder = new PersonTvViewHolder(view);
        return holder;
    }


    @Override
    public void onBindViewHolder(final PersontvAdapter.PersonTvViewHolder holder, int position) {

        final PersonCredit credit = personCredits.getCast().get(position);
        if (credit != null) {

           // Log.d("PersonMovieAdapter", "True - " + personCredits.getCast().get(position).getMovieTitle() + " " + credit.getPosterPath());
            Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + credit.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .into(holder.poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                           // Log.d("PersonMovieAdapter", "Sucesso");
                            holder.title.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                          //  Log.d("PersonMovieAdapter", "ERRO " + credit.getMovieTitle());
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.title.setVisibility(View.VISIBLE);
                            holder.title.setText("N/A");
                        }
                    });

            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, TvShowActivity.class);
                    ImageView imageView = (ImageView) view;
                    int color = UtilsFilme.loadPalette(imageView);
                    intent.putExtra(Constantes.COLOR_TOP, color);
                   // Log.d("PersonMovieAdapter", "ID - " + credit.getMovieId());
                   // Log.d("PersonMovieAdapter", "ID - " + credit.getMovieTitle());
                    intent.putExtra(Constantes.TVSHOW_ID, credit.getMovieId());
                    intent.putExtra(Constantes.NOME_TVSHOW, credit.getMovieTitle());
                    context.startActivity(intent);

                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, FilmeActivity.class.getName());
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, FilmeActivity.class.getName());
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (personCredits.getCast() != null) {
           // Log.d("getItemCount", "Tamanho " + personCredits.getCast().size());
            return personCredits.getCast().size();
        }
        return 0;
    }

    public class PersonTvViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;
        ImageView poster;
        TextView title;

        public PersonTvViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.img_poster_grid);
            title = (TextView) itemView.findViewById(R.id.text_title_crew);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
        }
    }
}

