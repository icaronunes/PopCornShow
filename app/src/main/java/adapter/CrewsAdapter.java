package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import activity.CrewsActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCrew;
import utils.UtilsFilme;

/**
 * Created by icaro on 24/07/16.
 */
public class CrewsAdapter extends RecyclerView.Adapter<CrewsAdapter.CrewsViewHolder> {
    Context context;
    List<PersonCrew> crews;

    public CrewsAdapter(CrewsActivity crewsActivity, List<PersonCrew> crew) {


        this.context = crewsActivity;
        this.crews = crew;
        Log.d("ElencoAdapter", "Tamanho " + crews.size());
    }

    @Override
    public CrewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.crews_list_adapter, parent, false);
        CrewsViewHolder viewHolder = new CrewsViewHolder(view);
        return viewHolder;
    }


    @Override
    public void onBindViewHolder(CrewsViewHolder holder, int position) {
        PersonCrew personCrew = crews.get(position);
        holder.crew_character.setText(personCrew.getDepartment() + " " + personCrew.getJob());

        holder.crew_nome.setText(personCrew.getName());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + personCrew.getProfilePath())
                .placeholder(R.drawable.person)
                .into(holder.img_crew);

    }

    @Override
    public int getItemCount() {
        Log.d("ElencoAdapter", "Tamanho " + crews.size());
        return crews.size() > 0 ? crews.size() : 0;
    }

    public class CrewsViewHolder extends RecyclerView.ViewHolder {

        TextView crew_nome, crew_character;
        ImageView img_crew;

        public CrewsViewHolder(View itemView) {
            super(itemView);
            crew_nome = (TextView) itemView.findViewById(R.id.crew_nome);
            crew_character = (TextView) itemView.findViewById(R.id.crew_character);
            img_crew = (ImageView) itemView.findViewById(R.id.img_crew);

        }
    }
}

