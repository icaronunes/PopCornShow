package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
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

import java.util.List;

import activity.PersonActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.people.PersonCast;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 22/02/17.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private Context context;
    private List<PersonCast> casts;

    public CastAdapter(FragmentActivity activity, List<PersonCast> cast) {
        context = activity;
        this.casts = cast;
    }

    @Override
    public CastViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_elenco, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CastViewHolder holder, final int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        final PersonCast personCast = casts.get(position);
        if (personCast.getName() != null || personCast.getCharacter() != null) {
            holder.textCastPersonagem.setText(personCast.getCharacter());
            holder.textCastNome.setText(personCast.getName());
            Picasso.with(context)
                    .load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 2)) + personCast.getProfilePath())
                    .placeholder(R.drawable.person)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        }
                    });

        } else {
            holder.textCastPersonagem.setVisibility(View.GONE);
            holder.imageView.setVisibility(View.GONE);
            holder.textCastNome.setVisibility(View.GONE);
            holder.textCastPersonagem.setVisibility(View.GONE);
            holder.progressBar.setVisibility(View.GONE);
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra(Constantes.PERSON_ID, personCast.getId());
                intent.putExtra(Constantes.NOME_PERSON, personCast.getName());
                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, personCast.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, personCast.getName());
                FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return casts.size();
    }

    public class CastViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        ImageView imageView;
        TextView textCastPersonagem, textCastNome;

        public CastViewHolder(View itemView) {
            super(itemView);
            textCastNome = (TextView) itemView.findViewById(R.id.textCastNomes);
            textCastPersonagem = (TextView) itemView.findViewById(R.id.textCastPersonagem);
            imageView = (ImageView) itemView.findViewById(R.id.imgPager);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progressBarCast);
        }
    }
}