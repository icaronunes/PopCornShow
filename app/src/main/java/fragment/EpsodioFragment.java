package fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.crash.FirebaseCrash;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import activity.PersonActivity;
import br.com.icaro.filme.R;
import domain.FilmeService;
import domain.UserEp;
import domain.UserSeasons;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import utils.Constantes;
import utils.UtilsFilme;

import static java.lang.String.valueOf;


/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioFragment extends Fragment {

    final String TAG = this.getClass().getName();

    private int tvshow_id, color, position, temporada_position;
    private Credits credits;
    private TvEpisode episode;

   private String nome_serie;
    private LinearLayout linear_director, linear_air_date, linear_write, linear_vote;
    private FrameLayout frame_meio_ep_cima, frame_meio_ep_baixo;
    private TextView ep_title, ep_tvshow, ep_director, air_date, ep_write, ep_votos, ep_sinopse;
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
    private LinearLayout relativeLayout;
    private UserSeasons seasons;

    public static Fragment newInstance(TvEpisode tvEpisode, String nome_serie, int tvshow_id,
                                       int color, boolean seguindo, int position, UserSeasons seasons, int temporada_position) {

        EpsodioFragment fragment = new EpsodioFragment();
        Bundle bundle = new Bundle();
       // Log.d("TvShowFragment", "Series " + tvEpisode.getName());
        bundle.putSerializable(Constantes.EPSODIO, tvEpisode);
        bundle.putInt(Constantes.TVSHOW_ID, tvshow_id);
        bundle.putInt(Constantes.COLOR_TOP, color);
        bundle.putString(Constantes.NOME_TVSHOW, nome_serie);
        bundle.putBoolean(Constantes.SEGUINDO, seguindo);
        bundle.putInt(Constantes.POSICAO, position);
        bundle.putSerializable(Constantes.USER, seasons);
        bundle.putInt(Constantes.TEMPORADA_POSITION, temporada_position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            episode = (TvEpisode) getArguments().getSerializable(Constantes.EPSODIO);
            nome_serie = getArguments().getString(Constantes.NOME_TVSHOW);
            tvshow_id = getArguments().getInt(Constantes.TVSHOW_ID);
            color = getArguments().getInt(Constantes.COLOR_TOP);
            seguindo = getArguments().getBoolean(Constantes.SEGUINDO);
            position = getArguments().getInt(Constantes.POSICAO);
            temporada_position = getArguments().getInt(Constantes.TEMPORADA_POSITION);
            seasons = (UserSeasons) getArguments().getSerializable(Constantes.USER);
        }

        FirebaseAnalytics mFirebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());
        Bundle bundle = getArguments();
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


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

        linear_air_date = (LinearLayout) view.findViewById(R.id.linear_air_date);
        linear_director = (LinearLayout) view.findViewById(R.id.linear_director);
        linear_write = (LinearLayout) view.findViewById(R.id.linear_write);
        linear_vote = (LinearLayout) view.findViewById(R.id.linear_vote);
        frame_meio_ep_cima = (FrameLayout) view.findViewById(R.id.linear_meio_ep_cima);
        frame_meio_ep_baixo = (FrameLayout) view.findViewById(R.id.linear_meio_ep_baixo);
        relativeLayout = (LinearLayout) view.findViewById(R.id.epsodio_ll);

        ep_title = (TextView) view.findViewById(R.id.ep_title);
        ep_tvshow = (TextView) view.findViewById(R.id.ep_tvshow);
        ep_director = (TextView) view.findViewById(R.id.ep_director);
        ep_write = (TextView) view.findViewById(R.id.ep_write);
        ep_votos = (TextView) view.findViewById(R.id.ep_votos);
        ep_sinopse = (TextView) view.findViewById(R.id.ep_sinopse);
        air_date = (TextView) view.findViewById(R.id.air_date);

        ep_image = (ImageView) view.findViewById(R.id.ep_image);
        ep_rating_button = (Button) view.findViewById(R.id.ep_rating_button);
        ep_rating_button.setTextColor(color);

//        AdView adview = (AdView) view.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
//                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
//                .build();
//        adview.loadAd(adRequest);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (UtilsFilme.isNetWorkAvailable(getActivity())) {
            new TvEpisodeAsync().execute();
        } else {
            snack();
        }

    }

    protected void snack() {
        Snackbar.make(relativeLayout, R.string.no_internet, Snackbar.LENGTH_INDEFINITE)
                .setAction(R.string.retry, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (UtilsFilme.isNetWorkAvailable(getActivity())) {
                            new TvEpisodeAsync().execute();
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
        setTvshow();
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
                    try{
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

                } catch (NoSuchMethodError e){
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
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.child("userEps").child(valueOf(position)).child("nota").exists()) {
                    String nota = String.valueOf(dataSnapshot.child("userEps").child(valueOf(position)).child("nota").getValue());
                    numero_rated = Float.parseFloat(nota);
                    //Log.d(TAG, "Mudou");
                    //Log.d(TAG, dataSnapshot.getKey());
                    seasons = dataSnapshot.getValue(UserSeasons.class);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        if (seguindo) {
            databaseReference.addValueEventListener(epsListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (userListener != null && epsListener != null && myRef != null && databaseReference != null)  {
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

        if (UtilsFilme.verificaLancamento(date) && mAuth.getCurrentUser() != null && seguindo) {

            ep_rating_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final Dialog alertDialog = new Dialog(getContext());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.adialog_custom_rated);

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
                                childUpdates.put("/userEps/" + position + "/nota", 0 );
                                databaseReference.updateChildren(childUpdates);
                            }
                            alertDialog.dismiss();
                        }
                    });

                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {


                            Map<String, Object> childUpdates = new HashMap<String, Object>();

                            childUpdates.put("/userEps"+"/" + position + "/assistido", true);
                            childUpdates.put("/visto", TemporadaTodaAssistida());
                            childUpdates.put("/userEps/" + position + "/nota", ratingBar.getRating() );
                            databaseReference.updateChildren(childUpdates);

                            alertDialog.dismiss();


                        }
                    });
                }
            });
        } else {
            ep_rating_button.setVisibility(View.GONE);
        }
    }

    private boolean TemporadaTodaAssistida() {
       // Log.d(TAG, "tamanho EPS - " +episode.getId());
        for (UserEp userEp : seasons.getUserEps()) {
          //  Log.d(TAG, "tamanho UserEPs ID - " +userEp.getId());
            if (episode.getId() != userEp.getId()) {
             //   Log.d(TAG, "TemporadaTodaAssistida - Diferente");
                if (!userEp.isAssistido()) {
                 //   Log.d(TAG, "TemporadaTodaAssistida - false");
                    return false;
                }
            }
        }
       // Log.d(TAG, "TemporadaTodaAssistida - true");
        return true;
    }

    private void setTvshow() {
        if (nome_serie != null) {
            ep_tvshow.setText(nome_serie);
        } else {
            ep_tvshow.setVisibility(View.GONE);
        }
    }

    private void setSinopse() {
        if (episode.getOverview() != null) {
            ep_sinopse.setText(episode.getOverview());
        }
    }

    private void setImage() {


        Picasso.with(getContext())
                .load(UtilsFilme
                .getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(getContext(), 4)) + episode.getStillPath())
                .error(R.drawable.top_empty)
                .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                .into(ep_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {

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
            linear_vote.setVisibility(View.GONE);
            frame_meio_ep_baixo.setVisibility(View.GONE);
        }

    }

    private void setAirDate() {
        if (episode.getAirDate() != null && episode.getAirDate().equals("")) {
            air_date.setText(episode.getAirDate());
        } else {
            linear_air_date.setVisibility(View.GONE);
            frame_meio_ep_baixo.setVisibility(View.GONE);
        }
    }

    private void setWrite() {
        boolean visible = true;
        if (credits.getCrew() != null) {
            for (final PersonCrew crew : credits.getCrew()) {
                // Log.d("EpsodioFragment", crew.getName()+ " - "+ crew.getJob());
                if (crew.getJob().contains("Writer") && crew.getName() != "") {
                    ep_write.setText(crew.getName());
                    visible = false;

                    ep_write.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), PersonActivity.class);
                            intent.putExtra(Constantes.PERSON_ID, crew.getId());
                            intent.putExtra(Constantes.NOME_PERSON, crew.getName());
                            getContext().startActivity(intent);
                        }
                    });

                    return;
                }
            }
        }

        if (visible) {
            linear_write.setVisibility(View.GONE);
            frame_meio_ep_baixo.setVisibility(View.GONE);
        }

    }

    private void setName() {

        ep_title.setText(!episode.getName().isEmpty() ? episode.getName() : getContext().getString(R.string.sem_nome));
    }

    private void setDirector() {
        boolean visible = true;
        if (credits.getCrew() != null) {
            for (final PersonCrew crew : credits.getCrew()) {
                if (crew.getJob().contains("Director") && crew.getName() != "") {
                    ep_director.setText(crew.getName());

                    visible = false;


                    ep_director.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), PersonActivity.class);
                            intent.putExtra(Constantes.PERSON_ID, crew.getId());
                            intent.putExtra(Constantes.NOME_PERSON, crew.getName());
                            getContext().startActivity(intent);
                        }
                    });

                    return;
                }
            }
        }

        if (visible) {
            linear_director.setVisibility(View.GONE);
            frame_meio_ep_cima.setVisibility(View.GONE);
        }

    }


    private class TvEpisodeAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
          //  Log.d(TAG, "" + tvshow_id + " " + episode.getSeasonNumber() + " " + episode.getEpisodeNumber());
            try {
                credits = FilmeService.getTmdbTvEpisodes()
                        .getCredits(tvshow_id, episode.getSeasonNumber(), episode.getEpisodeNumber(), "en");
                return null;
            } catch (Exception e){
                FirebaseCrash.report(e);
                if (getActivity() != null)
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), R.string.ops, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (credits != null) {
                setDirector();
                setWrite();
            }
        }
    }

}
