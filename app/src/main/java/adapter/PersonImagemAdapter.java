package adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FotoPersonActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Artwork;
import utils.Constantes;
import utils.UtilsFilme;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonImagemAdapter extends RecyclerView.Adapter<PersonImagemAdapter.PersonImageViewHolder> {
    private Context context;
    private List<Artwork> artworks;
    private int id_person;
    private String nome;
    private FirebaseAnalytics firebaseAnalytics;

    public PersonImagemAdapter(Context context, List<Artwork> artworks, int id_person, String nome) {
        this.context = context;
        this.artworks = artworks;
        this.id_person = id_person;
        this.nome = nome;
    }

    @Override
    public PersonImagemAdapter.PersonImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false);

        return new PersonImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonImagemAdapter.PersonImageViewHolder holder, final int position) {
        final Artwork artwork = artworks.get(position);
       // Log.d("PersonImagemAdapter", artwork.getFilePath());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(context, 3)) + artwork.getFilePath())
                .placeholder(R.drawable.person)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(holder.imageButton, new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.GONE);
                    }
                });

        holder.imageButton.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, FotoPersonActivity.class);
                intent.putExtra(Constantes.PERSON_ID, id_person);
                intent.putExtra(Constantes.NOME_PERSON, nome);
                intent.putExtra(Constantes.POSICAO, position);
                context.startActivity(intent);

                firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, FotoPersonActivity.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, id_person);
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, nome);
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.VIEW_ITEM, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (artworks != null && !artworks.isEmpty()) {
           // Log.d("Image", "Tamanho " + artworks.size());
            return artworks.size();
        }
        return 0;
    }


    class PersonImageViewHolder extends RecyclerView.ViewHolder {
       private ProgressBar progressBar;
        private ImageButton imageButton;

        PersonImageViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
            imageButton = (ImageButton) itemView.findViewById(R.id.img_poster_grid);
        }
    }
}
