package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.PersonActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCast;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 24/07/16.
 */
public class ElencoAdapter extends RecyclerView.Adapter<ElencoAdapter.ElencoViewHolder> {

    private final FirebaseAnalytics mFirebaseAnalytics;
    private Context context;
    private List<PersonCast> casts;


    public ElencoAdapter(Context elencoActivity, List<PersonCast> casts) {

        this.context = elencoActivity;
        this.casts = casts;
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(elencoActivity);
       // Log.d("ElencoAdapter", "Tamanho " + casts.size());
    }

    @Override
    public ElencoAdapter.ElencoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.elenco_list_adapter, parent, false);
        return new ElencoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ElencoAdapter.ElencoViewHolder holder, int position) {
        final PersonCast personCast = casts.get(position);
        holder.elenco_character.setText(personCast.getCharacter());

        holder.elenco_nome.setText(personCast.getName());
        Picasso.with(context).load(UtilsApp
                .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + personCast.getProfilePath())
                .placeholder(R.drawable.person)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.img_elenco);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.ITEM_ID, String.valueOf(personCast.getId()));
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, personCast.getName());
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra(Constantes.INSTANCE.getPERSON_ID(), personCast.getId());
                intent.putExtra(Constantes.INSTANCE.getNOME_PERSON(), personCast.getName());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
      //  Log.d("ElencoAdapter", "Tamanho " + casts.size());
        if (casts != null){
            return  casts.size();
        } else {
            return 0;
        }
    }

    class ElencoViewHolder extends RecyclerView.ViewHolder {

        private TextView elenco_nome, elenco_character;
        private ImageView img_elenco;

        ElencoViewHolder(View itemView) {
            super(itemView);
            elenco_nome = (TextView) itemView.findViewById(R.id.elenco_nome);
            elenco_character = (TextView) itemView.findViewById(R.id.elenco_character);
            img_elenco = (ImageView) itemView.findViewById(R.id.img_elenco);
        }
    }
}
