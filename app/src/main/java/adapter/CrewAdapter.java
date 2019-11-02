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

import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import br.com.icaro.filme.R;
import domain.CrewItem;
import pessoa.activity.PersonActivity;
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
                    .load(UtilsApp.INSTANCE.getBaseUrlImagem(UtilsApp.INSTANCE.getTamanhoDaImagem(context, 4)) + crew.getProfilePath())
                    .placeholder(R.drawable.person)
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
                intent.putExtra(Constantes.PERSON_ID, crew.getId());
                intent.putExtra(Constantes.NOME_PERSON, crew.getName());
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

             textCrewJob = itemView.findViewById(R.id.textCrewJob);
             textCrewNome = itemView.findViewById(R.id.textCrewNome);
             imgPagerCrews = itemView.findViewById(R.id.imgPagerCrews);
             progressBarCrew = itemView.findViewById(R.id.progressBarCrews);
        }
    }
}
