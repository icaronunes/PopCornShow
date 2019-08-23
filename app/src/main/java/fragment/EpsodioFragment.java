package fragment;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import applicaton.BaseFragment;
import br.com.icaro.filme.R;
import domain.EpisodesItem;
import domain.FilmeService;
import domain.UserEp;
import domain.UserSeasons;
import info.movito.themoviedbapi.model.Credits;
import utils.Constantes;
import utils.UtilsApp;

import static java.lang.String.valueOf;


/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioFragment extends BaseFragment {

    private int tvshow_id, color, position, temporada_position;
    private Credits credits;
    private EpisodesItem episode;

    private TextView ep_title, ep_director, air_date, ep_write, ep_votos, ep_sinopse;
    private ImageView ep_image;
    private Button ep_rating_button;
    private UserEp userEp;
    private boolean seguindo;

    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private DatabaseReference databaseReference;
    private ValueEventListener userListener;
    private ValueEventListener epsListener;
    private float numero_rated;
    private ConstraintLayout constraintLayout;
    private UserSeasons seasons = null;

    public static Fragment newInstance(EpisodesItem tvEpisode, int tvshow_id,
                                       int color, boolean seguindo, int position, int temporada_position) {

        EpsodioFragment fragment = new EpsodioFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.INSTANCE.getEPSODIO(), tvEpisode);
        bundle.putInt(Constantes.INSTANCE.getTVSHOW_ID(), tvshow_id);
        bundle.putInt(Constantes.INSTANCE.getCOLOR_TOP(), color);
        bundle.putBoolean(Constantes.INSTANCE.getSEGUINDO(), seguindo);
        bundle.putInt(Constantes.INSTANCE.getPOSICAO(), position);
        bundle.putInt(Constantes.INSTANCE.getTEMPORADA_POSITION(), temporada_position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            episode = (EpisodesItem) getArguments().getSerializable(Constantes.INSTANCE.getEPSODIO());
            tvshow_id = getArguments().getInt(Constantes.INSTANCE.getTVSHOW_ID());
            color = getArguments().getInt(Constantes.INSTANCE.getCOLOR_TOP());
            seguindo = getArguments().getBoolean(Constantes.INSTANCE.getSEGUINDO());
            position = getArguments().getInt(Constantes.INSTANCE.getPOSICAO());
            temporada_position = getArguments().getInt(Constantes.INSTANCE.getTEMPORADA_POSITION());
        }

        mAuth = FirebaseAuth.getInstance();

        if (seguindo) {

            myRef = FirebaseDatabase.getInstance().getReference("users").child(mAuth.getCurrentUser().getUid())
                    .child("seguindo")
                    .child(valueOf(tvshow_id))
                    .child("seasons")
                    .child(valueOf(temporada_position))
                    .child("userEps")
                    .child(valueOf(position));

            databaseReference = FirebaseDatabase.getInstance()
                    .getReference("users")
                    .child(mAuth.getCurrentUser().getUid())
                    .child("seguindo")
                    .child(valueOf(tvshow_id))
                    .child("seasons")
                    .child(valueOf(temporada_position));

        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.epsodio_fragment, container, false);

        constraintLayout = (ConstraintLayout) view.findViewById(R.id.epsodio_ll);

        ep_title = (TextView) view.findViewById(R.id.ep_title);
        ep_director = (TextView) view.findViewById(R.id.ep_director);
        ep_write = (TextView) view.findViewById(R.id.ep_write);
        ep_votos = (TextView) view.findViewById(R.id.ep_votos);
        ep_sinopse = (TextView) view.findViewById(R.id.ep_sinopse);
        air_date = (TextView) view.findViewById(R.id.air_date);

        ep_image = (ImageView) view.findViewById(R.id.ep_image);
        ep_rating_button = (Button) view.findViewById(R.id.ep_rating_button);
        ep_rating_button.setTextColor(color);


        // setAdMob(view.findViewById(R.id.adView));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UtilsApp.isNetWorkAvailable(getActivity())) {
            //  new TvEpisodeAsync().execute();
        } else {
            snack();
        }

    }

    protected void snack() {
        Snackbar.make(constraintLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsApp.isNetWorkAvailable(getActivity())) {
                            //  new TvEpisodeAsync().execute();
                        } else {
                            snack();
                        }
                    }
                }).show();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListener();

        setAirDate();
        setVote();
        setImage();
        setSinopse();
        setName();

        if (episode.getAirDate() != null) {
            setButtonRating();
        }
    }

    private void setListener() {

        userListener = new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                userEp = dataSnapshot.getValue(UserEp.class);

                if (userEp != null) {
                    try {
                        if (userEp.isAssistido()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ep_rating_button.setBackground(getContext().getResources().getDrawable(R.drawable.button_visto, getActivity().getTheme()));
                                ep_rating_button.setText(getResources().getText(R.string.classificar_visto));
                                // TODO: Deveria usar getContext().getDrawable() ?
                            } else {
                                ep_rating_button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_visto));
                                ep_rating_button.setText(getResources().getText(R.string.classificar_visto));
                            }
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                ep_rating_button.setBackground(getContext().getResources().getDrawable(R.drawable.button_nao_visto, getActivity().getTheme()));
                                ep_rating_button.setText(getResources().getText(R.string.classificar));
                            } else {
                                ep_rating_button.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.button_nao_visto));
                                ep_rating_button.setText(getResources().getText(R.string.classificar));
                            }
                        }

                    } catch (NoSuchMethodError e) {
                        Toast.makeText(getContext(), R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        if (seguindo) {
            myRef.addValueEventListener(userListener);
        }

        epsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NotNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("userEps").child(valueOf(position)).child("nota").exists()) {
                    String nota = String.valueOf(dataSnapshot.child("userEps").child(valueOf(position)).child("nota").getValue());
                    numero_rated = Float.parseFloat(nota);
                    seasons = dataSnapshot.getValue(UserSeasons.class);
                }

            }

            @Override
            public void onCancelled(@NotNull DatabaseError databaseError) {
            }
        };
        if (seguindo) {
            databaseReference.addValueEventListener(epsListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null && epsListener != null && myRef != null && databaseReference != null) {
            myRef.removeEventListener(userListener);
            databaseReference.removeEventListener(epsListener);
        }
    }

    private void setButtonRating() {
        //Arrumar. Ta esquisito.

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            date = sdf.parse(episode.getAirDate() != null ? episode.getAirDate() : null);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (UtilsApp.verificaLancamento(date) && mAuth.getCurrentUser() != null && seguindo) {

            ep_rating_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog alertDialog = new Dialog(getContext());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.dialog_custom_rated);

                    Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                    Button nao_visto = (Button) alertDialog.findViewById(R.id.cancel_rated);

                    if (userEp != null) {
                        if (!userEp.isAssistido()) {
                            nao_visto.setVisibility(View.INVISIBLE);
                        }
                    } else {
                        nao_visto.setVisibility(View.INVISIBLE);
                    }

                    TextView title = (TextView) alertDialog.findViewById(R.id.rating_title);
                    title.setText(episode.getName() != null ? episode.getName() : "");
                    final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                    ratingBar.setRating(numero_rated);
                    int width = getResources().getDimensionPixelSize(R.dimen.popup_width);
                    int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                    alertDialog.getWindow().setLayout(width, height);
                    alertDialog.show();

                    nao_visto.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (seguindo) {
                                // Log.d(TAG, "não visto");

                                Map<String, Object> childUpdates = new HashMap<String, Object>();

                                childUpdates.put("/userEps/" + position + "/assistido", false);
                                childUpdates.put("/visto/", false);
                                childUpdates.put("/userEps/" + position + "/nota", 0);
                                databaseReference.updateChildren(childUpdates);
                            }
                            alertDialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(view1 -> {


                        Map<String, Object> childUpdates = new HashMap<String, Object>();

                        childUpdates.put("/userEps" + "/" + position + "/assistido", true);
                        childUpdates.put("/visto", TemporadaTodaAssistida());
                        childUpdates.put("/userEps/" + position + "/nota", ratingBar.getRating());
                        databaseReference.updateChildren(childUpdates);

                        alertDialog.dismiss();

                        new Thread(() -> FilmeService
                                .ratedTvshowEpsodioGuest(tvshow_id, seasons
                                        .getSeasonNumber(), position, (int) ratingBar
                                        .getRating(), getContext())).start();
                    });
                }
            });
        } else {
            ep_rating_button.setVisibility(View.GONE);
        }
    }

    private boolean TemporadaTodaAssistida() {
        for (UserEp userEp : seasons.getUserEps()) {
            if (episode.getId() != userEp.getId()) {
                if (!userEp.isAssistido()) {
                    return false;
                }
            }
        }
        return true;
    }

    private void setSinopse() {
        if (episode.getOverview() != null) {
            ep_sinopse.setText(episode.getOverview());
        }
    }

    private void setImage() {
        Picasso.get()
                .load(UtilsApp
                        .getBaseUrlImagem(UtilsApp.getTamanhoDaImagem(getContext(), 4)) + episode.getStillPath())
                .error(R.drawable.top_empty)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(ep_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError(Exception e) {
                    }

                });
    }

    private void setVote() {

        if (episode.getVoteAverage() > 0) {
            String votos = (String) String.valueOf(episode.getVoteAverage()).subSequence(0, 3);

            if (episode.getVoteAverage() < 10) {
                ep_votos.setText(votos + "/" + episode.getVoteCount());
            } else {
                votos = votos.replace(".", "");
                ep_votos.setText(votos + "/" + episode.getVoteCount());
            }
        } else {
//            linear_vote.setVisibility(View.GONE);
//            frame_meio_ep_baixo.setVisibility(View.GONE);
        }

    }

    private void setAirDate() {
        if (episode.getAirDate() != null && episode.getAirDate().equals("")) {
            air_date.setText(episode.getAirDate());
        } else {
           // linear_air_date.setVisibility(View.GONE);
           // frame_meio_ep_baixo.setVisibility(View.GONE);
        }
    }

    private void setName() {

        ep_title.setText(!episode.getName().isEmpty() ? episode.getName() : getContext().getString(R.string.sem_nome));
    }

}
