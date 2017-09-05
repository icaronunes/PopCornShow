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

import activity.FilmeActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCredit;
import info.movito.themoviedbapi.model.people.PersonCredits;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonMovieAdapter extends RecyclerView.Adapter<PersonMovieAdapter.PersonMovieViewHolder> {
    private Context context;
    private PersonCredits personCredits;

    public PersonMovieAdapter(Context context, PersonCredits personCredits) {

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

        final PersonCredit credit = personCredits.getCast().get(position);
        if (credit != null) {

           // Log.d("PersonMovieAdapter", "True - " + personCredits.getCast().get(position).getMovieTitle() + " " + credit.getPosterPath());
            Picasso.with(context)
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context , 2)) + credit.getPosterPath())
                    .error(R.drawable.poster_empty)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.poster, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            //Log.d("PersonMovieAdapter", "Sucesso");
                            holder.title.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                           // Log.d("PersonMovieAdapter", "ERRO " + credit.getMovieTitle());
                            holder.progressBar.setVisibility(View.INVISIBLE);
                            holder.title.setText(credit.getMovieTitle());
                        }
                    });

            holder.poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, FilmeActivity.class);
                    ImageView imageView = (ImageView) view;
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), UtilsApp.loadPalette(imageView));
                  //  Log.d("PersonMovieAdapter", "ID - " + credit.getMovieId());
                  //  Log.d("PersonMovieAdapter", "ID - " + credit.getMovieTitle());
                    intent.putExtra(Constantes.INSTANCE.getFILME_ID(), credit.getMovieId());
                    intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), credit.getMovieTitle());
                    context.startActivity(intent);

                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, FilmeActivity.class.getName() );
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, FilmeActivity.class.getName());
                    firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });
        }

    }

    @Override
    public int getItemCount() {
        if (personCredits.getCast() != null) {
           // Log.d("getItemCount", "Tamanho "+personCredits.getCast().size());
            return personCredits.getCast().size();
        }
        return 0;
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
