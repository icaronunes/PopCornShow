package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import domain.CastItem;
import pessoa.activity.PersonActivity;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 22/02/17.
 */
public class CastAdapter extends RecyclerView.Adapter<CastAdapter.CastViewHolder> {
    private Context context;
    private List<CastItem> casts;

    public CastAdapter(FragmentActivity activity, List<CastItem> cast) {
        context = activity;
        this.casts = cast;
    }


    @Override
    public CastViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_elenco, parent, false);
        return new CastViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CastViewHolder holder, final int position) {
        holder.progressBar.setVisibility(View.VISIBLE);
        final CastItem personCast = casts.get(position);
        if (personCast.getName() != null || personCast.getCharacter() != null) {
            holder.textCastPersonagem.setText(personCast.getCharacter());
            holder.textCastNome.setText(personCast.getName());
            
            Picasso.get()
                    .load(UtilsApp.INSTANCE.getBaseUrlImagem(UtilsApp.INSTANCE.getTamanhoDaImagem(context, 4)) + personCast.getProfilePath())
                    .placeholder(R.drawable.person)
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBar.setVisibility(View.INVISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
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

    class CastViewHolder extends RecyclerView.ViewHolder {
       private ProgressBar progressBar;
        private ImageView imageView;
        private TextView textCastPersonagem, textCastNome;

        CastViewHolder(View itemView) {
            super(itemView);
            textCastNome = itemView.findViewById(R.id.textCastNomes);
            textCastPersonagem = itemView.findViewById(R.id.textCastPersonagem);
            imageView = itemView.findViewById(R.id.imgPager);
            progressBar = itemView.findViewById(R.id.progressBarCast);
        }
    }
}
