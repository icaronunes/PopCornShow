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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import filme.activity.FilmeActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonCrewsAdapter extends RecyclerView.Adapter<PersonCrewsAdapter.PersonCrewsViewHolder> {
    private Context context;
    private PersonCredits personCredits;
    private FirebaseAnalytics firebaseAnalytics;

    public PersonCrewsAdapter(Context context, PersonCredits personCredits) {

        this.context = context;
        this.personCredits = personCredits;
    }

    @Override
    public PersonCrewsAdapter.PersonCrewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crews_filmes_layout, parent, false);
        return new PersonCrewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonCrewsAdapter.PersonCrewsViewHolder holder, int position) {

        final PersonCredit movie = personCredits.getCrew().get(position);

        Picasso.with(context).load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3)) + movie.getPosterPath())
                .placeholder(R.drawable.poster_empty)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.poster, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.title.setVisibility(View.INVISIBLE);
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                        holder.title.setText(movie.getMovieTitle() + " - " +movie.getReleaseDate());
                        holder.title.setVisibility(View.VISIBLE);
                    }
                });
        
        holder.poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FilmeActivity.class);
                ImageView imageView = (ImageView) view;
                int color = UtilsApp.loadPalette(imageView);
                intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                intent.putExtra(Constantes.INSTANCE.getFILME_ID(), movie.getMovieId());
                intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), movie.getMovieTitle());
                context.startActivity(intent);
                firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonCrewsAdapter.class.getName());
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, FilmeActivity.class.getName());
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);

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


    class PersonCrewsViewHolder extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private ImageView poster;
        private TextView title;

        PersonCrewsViewHolder(View itemView) {
            super(itemView);
            poster = (ImageView) itemView.findViewById(R.id.img_poster_grid);
            title = (TextView) itemView.findViewById(R.id.text_title_crew);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
        }
    }

}
