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

import pessoa.activity.PersonActivity;
import br.com.icaro.filme.R;
import domain.CrewItem;
import utils.Constantes;
import utils.UtilsApp;

/**
 * Created by icaro on 22/02/17.
 */
public class CrewAdapter extends RecyclerView.Adapter<CrewAdapter.CrewViewHolder> {
    private Context context;
    private List<CrewItem> crews;

    public CrewAdapter(FragmentActivity activity, List<CrewItem> crews) {
        context = activity;
        this.crews = crews;
    }


    @Override
    public CrewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.scroll_crews, parent, false);
        return new CrewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CrewViewHolder holder, final int position) {
        holder.progressBarCrew.setVisibility(View.VISIBLE);
        final CrewItem crew = crews.get(position);

        if (crew.getName() != null && crew.getJob() != null) {
            holder.textCrewJob.setText(crew.getJob());
            holder.textCrewNome.setText(crew.getName());
            Picasso.get()
                    .load(UtilsApp.getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(context, 2)) + crew.getProfilePath())
                    .placeholder(R.drawable.person)
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(holder.imgPagerCrews, new Callback() {
                        @Override
                        public void onSuccess() {
                            holder.progressBarCrew.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Exception e) {
                            holder.progressBarCrew.setVisibility(View.GONE);
                        }

                    });
        } else {
            holder.textCrewJob.setVisibility(View.GONE);
            holder.textCrewNome.setVisibility(View.GONE);
            holder.progressBarCrew.setVisibility(View.GONE);
            holder.imgPagerCrews.setVisibility(View.GONE);
        }

        holder.imgPagerCrews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PersonActivity.class);
                intent.putExtra(Constantes.INSTANCE.getPERSON_ID(), crew.getId());
                intent.putExtra(Constantes.INSTANCE.getNOME_PERSON(), crew.getName());
                context.startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, crew.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, crew.getName());
                FirebaseAnalytics.getInstance(context).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return crews.size();
    }

    class CrewViewHolder extends RecyclerView.ViewHolder {
        private TextView textCrewJob, textCrewNome;
        private ImageView imgPagerCrews;
        private ProgressBar progressBarCrew;

        CrewViewHolder(View itemView) {
            super(itemView);

             textCrewJob = (TextView) itemView.findViewById(R.id.textCrewJob);
             textCrewNome = (TextView) itemView.findViewById(R.id.textCrewNome);
             imgPagerCrews = (ImageView) itemView.findViewById(R.id.imgPagerCrews);
             progressBarCrew = (ProgressBar) itemView.findViewById(R.id.progressBarCrews);
        }
    }
}
