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

import activity.PersonActivity;
import activity.PersonPopularActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.TmdbPeople;
import info.movito.themoviedbapi.model.people.Person;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 04/10/16.
 */
public class PersonPopularAdapter extends RecyclerView.Adapter<PersonPopularAdapter.HolderPersonPopular> {

    private Context context;
    private TmdbPeople.PersonResultsPage personResultsPage;

    public PersonPopularAdapter(PersonPopularActivity personPopular, TmdbPeople.PersonResultsPage personResultsPage) {
        context = personPopular;
        this.personResultsPage = personResultsPage;
    }

    @Override
    public HolderPersonPopular onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_person_popular, parent, false);
        return new HolderPersonPopular(view);
    }

    @Override
    public void onBindViewHolder(final HolderPersonPopular holder, final int position) {
        if (personResultsPage != null) {

            final Person person = personResultsPage.getResults().get(position);
            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + person.getProfilePath())
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.img_person, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.GONE);
                            holder.text_person_name.setText(person.getName());
                            holder.text_person_name.setVisibility(View.VISIBLE);
                            holder.img_person.setImageResource(R.drawable.person);
                        }
                    });

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, PersonActivity.class);
                    intent.putExtra(Constantes.PERSON_ID, person.getId());
                    intent.putExtra(Constantes.NOME_PERSON, person.getName());
                    context.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {

        return personResultsPage.getResults().size();
    }

    class HolderPersonPopular extends RecyclerView.ViewHolder {

        private ProgressBar progressBar;
        private ImageView img_person;
        private TextView text_person_name;

        HolderPersonPopular(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
            text_person_name = (TextView) itemView.findViewById(R.id.text_person_name);
            img_person = (ImageView) itemView.findViewById(R.id.img_popular_person);
        }
    }
}
