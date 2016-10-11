package fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import activity.PersonActivity;
import applicaton.FilmeApplication;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.Credits;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.tv.TvEpisode;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.id.ep_rating;
import static com.google.android.gms.wearable.DataMap.TAG;
import static utils.UtilsFilme.getContext;

/**
 * Created by icaro on 27/08/16.
 */
public class EpsodioFragment extends Fragment {

    int tvshow_id, color;
    Credits credits;
    TvEpisode episode;

    String nome_serie;
    LinearLayout linear_director, linear_air_date, linear_write, linear_vote;
    FrameLayout frame_meio_ep_cima, frame_meio_ep_baixo;
    TextView ep_title, ep_tvshow, ep_director, air_date, ep_write, ep_votos, ep_sinopse;
    ImageView ep_image;
    RatingBar ep_ratingBar;
    Button ep_rating_button;


    public static Fragment newInstance(TvEpisode tvEpisode, String nome_serie, int tvshow_id, int color) {

        EpsodioFragment fragment = new EpsodioFragment();
        Bundle bundle = new Bundle();
        Log.d("TvShowFragment", "Series " + tvEpisode.getName());
        bundle.putSerializable(Constantes.EPSODIO, tvEpisode);
        bundle.putInt(Constantes.TVSHOW_ID, tvshow_id);
        bundle.putInt(Constantes.COLOR_TOP, color);
        bundle.putString(Constantes.NOME_TVSHOW, nome_serie);
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
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setAirDate();
        setVote();
        setImage();
        setTvshow();
        setSinopse();
        setName();
        if (!episode.getAirDate().isEmpty()) {
            setButtonRating();
        }
    }


    private void setButtonRating() {
        //Arrumar. Ta esquisito.

        Date date = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = sdf.parse(episode.getAirDate() != null ? episode.getAirDate() : null );
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (UtilsFilme.verificaLancamento(date) && FilmeApplication.getInstance().isLogado()) {


            ep_rating_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final Dialog alertDialog = new Dialog(getContext());
                    alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    alertDialog.setContentView(R.layout.adialog_custom_rated);

                    Button ok = (Button) alertDialog.findViewById(R.id.ok_rated);
                    final RatingBar ratingBar = (RatingBar) alertDialog.findViewById(R.id.ratingBar_rated);
                    int width = getResources().getDimensionPixelSize(R.dimen.popup_width); //Criar os Dimen do layout do login - 300dp - 300dp ??
                    int height = getResources().getDimensionPixelSize(R.dimen.popup_height_rated);

                    alertDialog.getWindow().setLayout(width, height);
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(),
                            android.R.style.Theme_Material_Dialog);
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Log.d(TAG, "Adialog Rated");

                            progressDialog.setIndeterminate(true);
                            progressDialog.setMessage("Salvando...");
                            progressDialog.show();

                            new Thread() {
                                boolean status = false;

                                @Override
                                public void run() {
                                    if (UtilsFilme.isNetWorkAvailable(getContext())) {
                                        status = FilmeService
                                                .setRatedTvShowEpsodio(tvshow_id, episode.getSeasonNumber(),
                                                        episode.getEpisodeNumber(), ratingBar.getRating());
                                        try {
                                            if (getActivity() != null) { // usada para não crash quando activity e destruida antes do fim do metodo
                                                Thread.sleep(200);
                                                if (getActivity() != null) {
                                                    getActivity().runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Toast.makeText(getContext(), getResources().getString(R.string.tvshow_rated), Toast.LENGTH_SHORT)
                                                                    .show();

                                                            progressDialog.dismiss();
                                                        }
                                                    });
                                                }
                                            }
                                        } catch (InterruptedException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }.start();
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
        } else {
            ep_rating_button.setVisibility(View.GONE);
        }
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

        Log.d("Entrou", UtilsFilme.getBaseUrlImagem(5) + episode.getStillPath());
        Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(5) + episode.getStillPath())
                .error(R.drawable.top_empty)
                .into(ep_image, new Callback() {
                    @Override
                    public void onSuccess() {

                    }

                    @Override
                    public void onError() {
                        Log.d("Entrou", "");

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
        if (episode.getAirDate() != null && episode.getAirDate() != "") {
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
                    Log.d("EpsodioFragment", crew.getName() + " - " + crew.getJob());
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.epsodio_fragment, container, false);


        linear_air_date = (LinearLayout) view.findViewById(R.id.linear_air_date);
        linear_director = (LinearLayout) view.findViewById(R.id.linear_director);
        linear_write = (LinearLayout) view.findViewById(R.id.linear_write);
        linear_vote = (LinearLayout) view.findViewById(R.id.linear_vote);
        frame_meio_ep_cima = (FrameLayout) view.findViewById(R.id.linear_meio_ep_cima);
        frame_meio_ep_baixo = (FrameLayout) view.findViewById(R.id.linear_meio_ep_baixo);

        ep_title = (TextView) view.findViewById(R.id.ep_title);
        ep_tvshow = (TextView) view.findViewById(R.id.ep_tvshow);
        ep_director = (TextView) view.findViewById(R.id.ep_director);
        ep_write = (TextView) view.findViewById(R.id.ep_write);
        ep_votos = (TextView) view.findViewById(R.id.ep_votos);
        ep_sinopse = (TextView) view.findViewById(R.id.ep_sinopse);
        air_date = (TextView) view.findViewById(R.id.air_date);

        ep_image = (ImageView) view.findViewById(R.id.ep_image);
        ep_ratingBar = (RatingBar) view.findViewById(ep_rating);
        ep_rating_button = (Button) view.findViewById(R.id.ep_rating_button);
        ep_rating_button.setTextColor(color);

        AdView adview = (AdView) view.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice("AC98C820A50B4AD8A2106EDE96FB87D4")  // An example device ID
                .build();
        adview.loadAd(adRequest);

        new TvEpisodeAsync().execute();
        return view;
    }

    private class TvEpisodeAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("TvEpisodeAsync", "" + tvshow_id + " " + episode.getSeasonNumber() + " " + episode.getEpisodeNumber());

            credits = FilmeService.getTmdbTvEpisodes()
                    .getCredits(tvshow_id, episode.getSeasonNumber(), episode.getEpisodeNumber(), "en");
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setDirector();
            setWrite();
        }
    }

}
