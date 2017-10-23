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
import domain.person.CrewItem;
import filme.activity.FilmeActivity;
import tvshow.activity.TvShowActivity;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonCrewsAdapter extends RecyclerView.Adapter<PersonCrewsAdapter.PersonCrewsViewHolder> {
    private Context context;
    private List<CrewItem> personCredits;


    public PersonCrewsAdapter(Context context, List<CrewItem> personCredits) {

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

        final CrewItem item = personCredits.get(position);

            Picasso.with(context).load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3)) + item.getPosterPath())
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
                            StringBuilder data = new StringBuilder();
                            if (item.getReleaseDate() != null && !item.getReleaseDate().isEmpty()) {
                                data.append(" - ");
                                data.append(item.getReleaseDate().length() >= 4  ? item.getReleaseDate().substring(0, 4) : "");
                            }
                            holder.title.setText(item.getTitle() + data  );
                            holder.title.setVisibility(View.VISIBLE);
                        }
                    });

            holder.poster.setOnClickListener(view -> {

                if (item.getMediaType().equals("movie")) {
                    Intent intent = new Intent(context, FilmeActivity.class);
                    ImageView imageView = (ImageView) view;
                    int color = UtilsApp.loadPalette(imageView);
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                    intent.putExtra(Constantes.INSTANCE.getFILME_ID(), item.getId());
                    intent.putExtra(Constantes.INSTANCE.getNOME_FILME(), item.getTitle());
                    context.startActivity(intent);
                } else if (item.getMediaType().equals("tv")){
                    Intent intent = new Intent(context, TvShowActivity.class);
                    ImageView imageView = (ImageView) view;
                    int color = UtilsApp.loadPalette(imageView);
                    intent.putExtra(Constantes.INSTANCE.getCOLOR_TOP(), color);
                    intent.putExtra(Constantes.INSTANCE.getTVSHOW_ID(), item.getId());
                    intent.putExtra(Constantes.INSTANCE.getNOME_TVSHOW(), item.getTitle());
                    context.startActivity(intent);
                }
            });

    }

    @Override
    public int getItemCount() {
        if (personCredits != null) {
            return personCredits.size();
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
