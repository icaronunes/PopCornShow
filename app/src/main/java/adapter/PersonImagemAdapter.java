package adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.FotoPersonActivity;
import br.com.icaro.filme.R;
import domain.person.ProfilesItem;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonImagemAdapter extends RecyclerView.Adapter<PersonImagemAdapter.PersonImageViewHolder> {
    private Context context;
    private List<ProfilesItem> artworks;
    private int id_person;
    private String nome;

    public PersonImagemAdapter(Context context, List<ProfilesItem> artworks, int id, String nome) {
        this.context = context;
        this.artworks = artworks;
        this.id_person = id;
        this.nome = nome;
    }

    @Override
    public PersonImagemAdapter.PersonImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.poster_grid_image, parent, false);

        return new PersonImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final PersonImagemAdapter.PersonImageViewHolder holder, int position) {
        final ProfilesItem artwork = artworks.get(position);

        Picasso.with(context).load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 3)) + artwork.getFilePath())
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
                intent.putExtra(Constantes.INSTANCE.getPERSON_ID(), id_person);
                intent.putExtra(Constantes.INSTANCE.getNOME_PERSON(), nome);
                intent.putExtra(Constantes.INSTANCE.getPOSICAO(), position);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (artworks != null && !artworks.isEmpty()) {
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
