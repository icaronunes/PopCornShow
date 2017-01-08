package adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import domain.Netflix;

/**
 * Created by icaro on 08/01/17.
 */
public class ActorNetflixAdapter extends RecyclerView.Adapter<ActorNetflixAdapter.ActorNetflixViewHolder> {

    private Context actorNetflix = null;
    private List<Netflix>  netflixActors = null;

    public ActorNetflixAdapter(Context actorNetflix, List<Netflix> netflixActors) {
        this.actorNetflix = actorNetflix;
        this.netflixActors = netflixActors;

    }

    @Override
    public ActorNetflixViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(actorNetflix).inflate(R.layout.actornetflix_list_adapter, parent, false);
        return new ActorNetflixViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ActorNetflixViewHolder holder, int position) {
        final Netflix netflix = netflixActors.get(position);

        Picasso.with(actorNetflix).load(netflix.poster)
                .error(R.drawable.poster_empty)
                .into(holder.img, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.GONE);
                holder.title.setText(netflix.showTitle);
            }
        });


        holder.img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (netflix.showId != 0) {
                    String url = "https://www.netflix.com/title/" + netflix.showId;
                    Uri webpage = Uri.parse(url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                    actorNetflix.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        if (netflixActors != null) {
            return netflixActors.size();
        } else {
            return 0;
        }
    }

    public class ActorNetflixViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        ProgressBar progressBar;
        TextView title;

        public ActorNetflixViewHolder(View itemView) {
            super(itemView);

            img = (ImageView) itemView.findViewById(R.id.img_actor_netflix);
            title = (TextView) itemView.findViewById(R.id.title_actor_netflix);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress);
        }
    }
}
