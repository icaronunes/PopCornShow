package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
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
    Context context;
    List<Artwork> artworks;
    int id_person;
    String nome;

    public PersonImagemAdapter(Context context, List<Artwork> artworks, int id_person, String nome) {
        this.context = context;
        this.artworks = artworks;
        this.id_person = id_person;
        this.nome = nome;
    }

    @Override
    public PersonImagemAdapter.PersonImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false);
        PersonImageViewHolder holder = new PersonImageViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(final PersonImagemAdapter.PersonImageViewHolder holder, final int position) {
        final Artwork artwork = artworks.get(position);
        Log.d("PersonImagemAdapter", artwork.getFilePath());
        Picasso.with(context).load(UtilsFilme.getBaseUrlImagem(3) + artwork.getFilePath())
                .placeholder(R.drawable.person)
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

            }
        });
    }

    @Override
    public int getItemCount() {
        if (artworks != null && !artworks.isEmpty()) {
            Log.d("Image", "Tamanho " + artworks.size());
            return artworks.size();
        }
        return 0;
    }


    public class PersonImageViewHolder extends RecyclerView.ViewHolder {
        ProgressBar progressBar;
        ImageButton imageButton;

        public PersonImageViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_poster_grid);
            imageButton = (ImageButton) itemView.findViewById(R.id.img_poster_grid);
        }
    }
}
