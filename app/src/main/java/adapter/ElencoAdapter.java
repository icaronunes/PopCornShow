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

import activity.ElencoActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.people.PersonCast;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.movieDb;

/**
 * Created by icaro on 24/07/16.
 */
public class ElencoAdapter extends RecyclerView.Adapter<ElencoAdapter.ElencoViewHolder> {

    Context context;
    List<PersonCast> casts;


    public ElencoAdapter(ElencoActivity elencoActivity, List<PersonCast> movieDb) {

        this.context = elencoActivity;
        this.casts = movieDb;
        Log.d("ElencoAdapter", "Tamanho " + casts.size());
    }

    @Override
    public ElencoAdapter.ElencoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elenco_list_adapter, parent, false);
        ElencoViewHolder viewHolder = new ElencoViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ElencoAdapter.ElencoViewHolder holder, int position) {
        PersonCast personCast = casts.get(position);
        holder.elenco_character.setText(personCast.getCharacter());

        holder.elenco_nome.setText(personCast.getName());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(2) + personCast.getProfilePath())
                .placeholder(R.drawable.person)
                .into(holder.img_elenco);

    }

    @Override
    public int getItemCount() {
        Log.d("ElencoAdapter", "Tamanho " + casts.size());
        return casts.size() > 0 ? casts.size() : 0;
    }

    public class ElencoViewHolder extends RecyclerView.ViewHolder {

        TextView elenco_nome, elenco_character;
        ImageView img_elenco;

        public ElencoViewHolder(View itemView) {
            super(itemView);
            elenco_nome = (TextView) itemView.findViewById(R.id.elenco_nome);
            elenco_character = (TextView) itemView.findViewById(R.id.elenco_character);
            img_elenco = (ImageView) itemView.findViewById(R.id.img_elenco);

        }
    }
}
