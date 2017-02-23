package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import activity.BaseActivity;
import activity.CrewsActivity;
import activity.ElencoActivity;
import activity.PosterGridActivity;
import activity.ReviewsActivity;
import activity.SettingsActivity;
import activity.Site;
import activity.TemporadaActivity;
import adapter.CastAdapter;
import adapter.CrewAdapter;
import adapter.TemporadasAdapter;
import adapter.TrailerAdapter;
import br.com.icaro.filme.R;
import domain.Imdb;
import domain.Netflix;
import domain.UserTvshow;
import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbTvSeasons;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.tv.TvSeason;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Config;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.in_production;
import static br.com.icaro.filme.R.string.mil;
import static utils.UtilsFilme.setEp;
import static utils.UtilsFilme.setUserTvShow;


/**
 * Created by icaro on 23/08/16.
 */
public class TvShowFragment extends Fragment {

    private final String TAG = TvShowFragment.class.getName();

    private int tipo, color;
    private boolean seguindo;
    private TvSeries series;
    private Button seguir, imdb, tmdb, netflix_button;
    private TextView titulo, categoria, descricao, voto_media, produtora,
            original_title, production_countries, status, temporada,
            popularity, lancamento, textview_crews, textview_elenco;
    private ImageView icon_site, img_poster, img_star, icon_reviews;
    private FirebaseAuth mAuth;
    private DatabaseReference myRef;
    private UserTvshow userTvshow;
    private RecyclerView recyclerViewTemporada, recycle_tvshow_trailer, recycle_tvshow_elenco, recycle_tvshow_producao;
    private TemporadasAdapter adapter;
    private ValueEventListener postListener;
    private ProgressBar progressBar, progressBarTemporada;
    private Netflix netflix = null;
    private Imdb imdbDd = null;

    public static Fragment newInstance(int tipo, TvSeries series, int color, boolean seguindo, Netflix netflix, Imdb imdb) {
        TvShowFragment fragment = new TvShowFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constantes.SERIE, series);
        bundle.putInt(Constantes.COLOR_TOP, color);
        bundle.putInt(Constantes.ABA, tipo);
        bundle.putSerializable(Constantes.USER, seguindo);
        bundle.putSerializable(Constantes.NETFLIX, netflix);
        bundle.putSerializable(Constantes.IMDB, imdb);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);
            series = (TvSeries) getArguments().getSerializable(Constantes.SERIE);
            color = getArguments().getInt(Constantes.COLOR_TOP);
            seguindo = getArguments().getBoolean(Constantes.USER);
            netflix = (Netflix) getArguments().getSerializable(Constantes.NETFLIX);
            imdbDd = (Imdb) getArguments().getSerializable(Constantes.IMDB);
        }
        //Validar se esta logado. Caso não, não precisa instanciar nada.

        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (tipo == R.string.informacoes) {
            isSeguindo();
            setSinopse();//Chamar depois? pelo metodo setTvShowInfomation?
            setTitulo();
            setCategoria();
            setLancamento();
            setProdutora();
            setNetflix();
            setHome();
            setVotoMedia();
            setOriginalTitle();
            setProductionCountries();
            setPopularity();
            setTemporada();
            setCast();
            setCrews();
            setTrailer();
            setPoster();
            setStatus();
            setAnimacao();
            setProgressBar();


            icon_site.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Log.d(TAG, "Home " + series.getHomepage());
                    if (series.getHomepage() != "" && series.getHomepage() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(series.getHomepage()));
                        // Log.d(TAG, "Home " + series.getHomepage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_homepage");
                        bundle.putString(FirebaseAnalytics.Param.DESTINATION, "Navegador");
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    } else {
                        BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                                getString(R.string.no_site));
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_homepage");
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sem homepage");
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    }
                }
            });

            imdb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (series.getExternalIds().getImdbId() != null) {
                        Intent intent = new Intent(getActivity(), Site.class);
                        intent.putExtra(Constantes.SITE,
                                "https:www.imdb.com/title/" + series.getExternalIds().getImdbId() + "/");

                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_imdb");
                        bundle.putString(FirebaseAnalytics.Param.DESTINATION, Site.class.getName());
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    }
                }
            });

            tmdb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), Site.class);
                    intent.putExtra(Constantes.SITE,
                            "https://www.themoviedb.org/tv/" + series.getId() + "/");
                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_tmdb");
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, Site.class.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });

            netflix_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (netflix == null) {
                        return;
                    }

                    if (netflix.showId != 0) {
                        String url = "https://www.netflix.com/title/" + netflix.showId;
                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        startActivity(intent);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }

                    } else {
                        String url = "https://www.netflix.com/search?q=" + series.getName();
                        url = Normalizer.normalize(url, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        Uri webpage = Uri.parse(url);
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                }
            });


            img_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getMediaNotas() > 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        LayoutInflater inflater = getActivity().getLayoutInflater();
                        View layout = inflater.inflate(R.layout.layout_notas, null);


                        if (imdbDd != null) {
                            ((TextView) layout
                                    .findViewById(R.id.nota_imdb)).setText(imdbDd.getImdbRating() != null ? imdbDd.getImdbRating() + "/10" :
                                    "- -");
                            ((TextView) layout
                                    .findViewById(R.id.nota_metacritic)).setText(imdbDd.getMetascore() != null ? imdbDd.getMetascore() + "/100" :
                                    "- -");
                            ((TextView) layout
                                    .findViewById(R.id.nota_tomatoes)).setText(imdbDd.getTomatoRating() != null ? imdbDd.getTomatoRating() + "/10" :
                                    "- -");
                        }

                        if (series != null)
                            ((TextView) layout
                                    .findViewById(R.id.nota_tmdb)).setText(String.valueOf(series.getVoteAverage() != 0 ? series.getVoteAverage() + "/10" :
                                    "- -"));

                        if (netflix != null) {
                            ((TextView) layout
                                    .findViewById(R.id.nota_netflix)).setText(String.valueOf(netflix.rating != null ? netflix.rating + "/5" :
                                    "- -"));
                        }

                        ((ImageView) layout.findViewById(R.id.image_netflix)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (netflix == null) {
                                    return;
                                }

                                if (netflix.showId != 0) {
                                    String url = "https://www.netflix.com/title/" + netflix.showId;
                                    Uri webpage = Uri.parse(url);
                                    Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                                    startActivity(intent);

                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "link netflix");
                                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, url);
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, netflix.showTitle);
                                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);


                                }
                            }
                        });

                        ((ImageView) layout.findViewById(R.id.image_metacritic)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (imdbDd == null) {
                                    return;
                                }

                                if (imdbDd.getType() != null) {

                                    String nome = imdbDd.getTitle().replace(" ", "-").toLowerCase();
                                    nome = UtilsFilme.removerAcentos(nome);
                                    String url = "http://www.metacritic.com/tv/" + nome;

                                    Intent intent = new Intent(getActivity(), Site.class);
                                    intent.putExtra(Constantes.SITE, url);
                                    startActivity(intent);

                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "link metacritic");
                                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, url);
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, imdbDd.getTitle());
                                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                }
                            }
                        });

                        ((ImageView) layout.findViewById(R.id.image_tomatoes)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (imdbDd == null) {
                                    return;
                                }

                                if (imdbDd.getType() != null) {

                                    String nome = imdbDd.getTitle().replace(" ", "_").toLowerCase();
                                    nome = UtilsFilme.removerAcentos(nome);
                                    String url = "https://www.rottentomatoes.com/tv/" + nome;
                                    Intent intent = new Intent(getActivity(), Site.class);
                                    intent.putExtra(Constantes.SITE, url);
                                    startActivity(intent);

                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "link rottentomatoes");
                                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, url);
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, netflix.showTitle);
                                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                                }
                            }
                        });

                        ((ImageView) layout.findViewById(R.id.image_imdb)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (imdbDd == null) {
                                    return;
                                }

                                if (imdbDd.getType() != null) {

                                    String url = "http://www.imdb.com/title/" + imdbDd.getImdbID();
                                    Intent intent = new Intent(getActivity(), Site.class);
                                    intent.putExtra(Constantes.SITE, url);
                                    startActivity(intent);

                                    Bundle bundle = new Bundle();
                                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "link imdb");
                                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, url);
                                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, imdbDd.getTitle());
                                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                                }
                            }
                        });

                        ((ImageView) layout.findViewById(R.id.image_tmdb)).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (series == null) {
                                    return;
                                }
                                String url = "https://www.themoviedb.org/tv/" + series.getId();
                                Intent intent = new Intent(getActivity(), Site.class);
                                intent.putExtra(Constantes.SITE, url);
                                startActivity(intent);

                                Bundle bundle = new Bundle();
                                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "link themoviedb");
                                bundle.putString(FirebaseAnalytics.Param.DESTINATION, url);
                                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                            }
                        });

                        //REFAZER METODOS - MUITO GRANDE.

                        builder.setView(layout);
                        builder.show();

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star");
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    } else {
                        BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                                getString(R.string.no_vote));

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star");
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "SnarBar_sem_informaçao");
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    }
                }
            });


            textview_elenco.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ElencoActivity.class);
                    intent.putExtra(Constantes.ID, series.getId());
                    intent.putExtra(Constantes.MEDIATYPE, series.getMediaType());
                    intent.putExtra(Constantes.NOME, series.getName());
                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, ElencoActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });

            textview_crews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), CrewsActivity.class);
                    intent.putExtra(Constantes.ID, series.getId());
                    intent.putExtra(Constantes.MEDIATYPE, series.getMediaType());
                    intent.putExtra(Constantes.NOME, series.getName());
                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, CrewsActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });


            icon_reviews.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (series.getExternalIds().getImdbId() != null) {
                        Intent intent = new Intent(getContext(), ReviewsActivity.class);
                        intent.putExtra(Constantes.FILME_ID, series.getExternalIds().getImdbId());
                        intent.putExtra(Constantes.NOME_FILME, series.getName());
                        intent.putExtra(Constantes.MEDIATYPE, series.getMediaType().name());
                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_reviews");
                        bundle.putString(FirebaseAnalytics.Param.DESTINATION, ReviewsActivity.class.getName());
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                    } else {
                        if (getActivity() != null) {
                            Toast.makeText(getActivity(), R.string.ops, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

        }

    }

    private void setProgressBar() {
        progressBar.setVisibility(View.GONE);
    }


    private void isSeguindo() {

        if (mAuth.getCurrentUser() != null) {

            if (seguindo) {
                seguir.setText(R.string.seguindo);
            } else {
                seguir.setText(R.string.seguir);
            }
        } else {
            setStatusButton();
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (mAuth.getCurrentUser() != null) {
            postListener = new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        userTvshow = dataSnapshot.getValue(UserTvshow.class);

                        if (getView() != null) {
                            userTvshow = dataSnapshot.getValue(UserTvshow.class);
                            // Log.w(TAG, "Passou");
                            recyclerViewTemporada = (RecyclerView) getView().getRootView().findViewById(R.id.temporadas_recycle);
                            adapter = new TemporadasAdapter(getActivity(), series, onClickListener(), color, userTvshow);
                            recyclerViewTemporada.setAdapter(adapter);
                            if (progressBarTemporada != null) {
                                //  Log.w(TAG, "Mudou - GONE");
                                progressBarTemporada.setVisibility(View.INVISIBLE);
                            }
                        }

                    } else {

                        if (getView() != null) {
                            //  Log.w(TAG, "Passou");
                            userTvshow = null; // ??????????
                            recyclerViewTemporada = (RecyclerView) getView().getRootView().findViewById(R.id.temporadas_recycle);
                            recyclerViewTemporada.setAdapter(new TemporadasAdapter(getActivity(), series, onClickListener(), color, userTvshow));
                            if (progressBarTemporada != null) {
                                //Log.w(TAG, "Mudou - GONE");
                                progressBarTemporada.setVisibility(View.INVISIBLE);
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {


                }
            };

            myRef.child(mAuth.getCurrentUser()
                    .getUid()).child("seguindo").child(String.valueOf(series.getId()))
                    .addValueEventListener(postListener);
        }
    }

    private void setStatus() {
        if (series.getStatus() != null) {
            status.setTextColor(color);

            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
            FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            if (idioma_padrao) {

                if (series.getStatus().equals("Returning Series")) {
                    status.setText(R.string.returnin_series);
                    bundle.putString("status_da_serie", getString(R.string.returnin_series));
                }
                if (series.getStatus().equals("Ended")) {
                    status.setText(R.string.ended);
                    bundle.putString("status_da_serie", getString(R.string.ended));
                }
                if (series.getStatus().equals("Canceled")) {
                    status.setText(R.string.canceled);
                    bundle.putString("status_da_serie", getString(R.string.canceled));
                }
                if (series.getStatus().equals("In Production")) {
                    status.setText(in_production);
                    bundle.putString("status_da_serie", getString(in_production));
                }
            } else {
                status.setText(series.getStatus());
                bundle.putString("status_da_serie", series.getStatus() + " Idioma Original");
            }
            FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }

    private void setStatusButton() {
        seguir.setTextColor(color);
        seguir.setEnabled(false);

        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
        if (idioma_padrao) {

            seguir.setText(getResources().getText(R.string.sem_login));

        } else {
            seguir.setText(series.getStatus());
            bundle.putString("status_da_serie", series.getStatus() + " Idioma Original");
        }
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        switch (tipo) {

            case R.string.temporadas: {
                return getViewTemporadas(inflater, container);
            }
            case R.string.informacoes: {
                return getViewInformacoes(inflater, container);
            }
        }
        return null;
    }

    private View getViewTemporadas(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.temporadas, container, false);
        progressBarTemporada = (ProgressBar) view.findViewById(R.id.progressBarTemporadas);
        recyclerViewTemporada = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerViewTemporada.setHasFixedSize(true);
        recyclerViewTemporada.setItemAnimator(new DefaultItemAnimator());
        recyclerViewTemporada.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mAuth.getCurrentUser() == null) {
            adapter = new TemporadasAdapter(getActivity(), series, onClickListener(), color, userTvshow);
            recyclerViewTemporada.setAdapter(adapter);
            if (progressBarTemporada != null) {
                // Log.w(TAG, "Mudou - GONE");
                progressBarTemporada.setVisibility(View.INVISIBLE);
            }
        }

        return view;
    }

    private void setTemporada() {
        if (series.getNumberOfSeasons() > 0) {
            temporada.setText(String.valueOf(series.getNumberOfSeasons()));
        }
    }

    private TemporadasAdapter.TemporadasOnClickListener onClickListener() {
        return new TemporadasAdapter.TemporadasOnClickListener() {
            @Override
            public void onClickTemporada(View view, int position, int color) {

                Intent intent = new Intent(getContext(), TemporadaActivity.class);
                intent.putExtra(Constantes.NOME, getString(R.string.temporada) + " " + series.getSeasons().get(position).getSeasonNumber());
                intent.putExtra(Constantes.TEMPORADA_ID, series.getSeasons().get(position).getSeasonNumber());
                intent.putExtra(Constantes.TEMPORADA_POSITION, position);
                intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                intent.putExtra(Constantes.COLOR_TOP, color);
                getContext().startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TemporadaActivity.class.getName());
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }

            @Override
            public void onClickCheckTemporada(View view, final int position) {

                if (isVisto(position)) {
                    Toast.makeText(getContext(), R.string.marcado_nao_assistido_temporada, Toast.LENGTH_SHORT).show();
                    final String user = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
                    final String id_serie = String.valueOf(series.getId());
                    Map<String, Object> childUpdates = new HashMap<String, Object>();

                    childUpdates.put("/" + user + "/seguindo/" + id_serie + "/seasons/" + position + "/visto", false);
                    setStatusEps(position, false);
                    childUpdates.put("/" + user + "/seguindo/" + id_serie + "/seasons/" + position + "/userEps", userTvshow.getSeasons().get(position).getUserEps());

                    myRef.updateChildren(childUpdates);
                    // Log.d(TAG, "desvisto");

                } else {
                    Toast.makeText(getContext(), R.string.marcado_assistido_temporada, Toast.LENGTH_SHORT).show();
                    final String user = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "";
                    final String id_serie = String.valueOf(userTvshow.getId());


                    Map<String, Object> childUpdates = new HashMap<String, Object>();
                    childUpdates.put("/" + user + "/seguindo/" + id_serie + "/seasons/" + position + "/visto", true);
                    setStatusEps(position, true);
                    //Fazer metodo para verificar, se 'quer' marcas temporadas anteriores.
                    childUpdates.put("/" + user + "/seguindo/" + id_serie + "/seasons/" + position + "/userEps", userTvshow.getSeasons().get(position).getUserEps());

                    myRef.updateChildren(childUpdates);

                    // Log.d(TAG, "visto");
                }
            }
        };
    }

    private boolean isVisto(int position) {
        if (userTvshow.getSeasons() != null) {
            if (userTvshow.getSeasons().get(position) != null) {
                return userTvshow.getSeasons().get(position).isVisto();
            } else {
                return false;
            }
        } else {
            return false; //segurança
        }
    }

    private void setStatusEps(int position, boolean status) {
        if (userTvshow != null) {
            if (userTvshow.getSeasons().get(position).getUserEps() != null)
                for (int i = 0; i < userTvshow.getSeasons().get(position).getUserEps().size(); i++) {
                    userTvshow.getSeasons().get(position).getUserEps().get(i).setAssistido(status);
                }
        }
    }

    private View getViewInformacoes(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.tvshow_info, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        titulo = (TextView) view.findViewById(R.id.titulo_tvshow);
        categoria = (TextView) view.findViewById(R.id.categoria_tvshow);
        descricao = (TextView) view.findViewById(R.id.descricao);
        status = (TextView) view.findViewById(R.id.status);
        temporada = (TextView) view.findViewById(R.id.temporadas);
        lancamento = (TextView) view.findViewById(R.id.lancamento);
        voto_media = (TextView) view.findViewById(R.id.voto_media);
        produtora = (TextView) view.findViewById(R.id.produtora);
        original_title = (TextView) view.findViewById(R.id.original_title);
        production_countries = (TextView) view.findViewById(R.id.production_countries);
        popularity = (TextView) view.findViewById(R.id.popularity);
        imdb = (Button) view.findViewById(R.id.imdb_site);
        tmdb = (Button) view.findViewById(R.id.tmdb_site);
        netflix_button = (Button) view.findViewById(R.id.netflix);
        img_poster = (ImageView) view.findViewById(R.id.img_poster);
        img_star = (ImageView) view.findViewById(R.id.img_star);
        icon_site = (ImageView) view.findViewById(R.id.icon_site);
        textview_crews = (TextView) view.findViewById(R.id.textview_crews);
        textview_elenco = (TextView) view.findViewById(R.id.textview_elenco);
        icon_reviews = (ImageView) view.findViewById(R.id.icon_reviews);
        seguir = (Button) view.findViewById(R.id.seguir);
        recycle_tvshow_trailer = (RecyclerView) view.findViewById(R.id.recycle_tvshow_trailer);
        recycle_tvshow_elenco = (RecyclerView) view.findViewById(R.id.recycle_tvshow_elenco);
        recycle_tvshow_producao = (RecyclerView) view.findViewById(R.id.recycle_tvshow_producao);


        seguir.setOnClickListener(ListenerSeguir());


        return view;
    }

    private View.OnClickListener ListenerSeguir() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (getView() != null) {
                    progressBarTemporada = (ProgressBar) getView().getRootView().findViewById(R.id.progressBarTemporadas);
                    progressBarTemporada.setVisibility(View.VISIBLE);
                }

                if (!seguindo) {
                    // Log.d(TAG, "incluir");
                    seguindo = !seguindo;
                    isSeguindo();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (UtilsFilme.isNetWorkAvailable(getActivity())) {
                                TmdbTvSeasons tvSeasons = new TmdbApi(Config.TMDB_API_KEY).getTvSeasons();

                                userTvshow = setUserTvShow(series);

                                for (int i = 0; i < series.getSeasons().size(); i++) {
                                    TvSeason tvS = series.getSeasons().get(i);
                                    TvSeason tvSeason = tvSeasons.getSeason(series.getId(), tvS.getSeasonNumber(), "en", TmdbTvSeasons.SeasonMethod.images); //?
                                    userTvshow.getSeasons().get(i).setUserEps(setEp(tvSeason));
                                }

                                myRef.child(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "")
                                        .child("seguindo")
                                        .child(String.valueOf(series.getId()))
                                        .setValue(userTvshow)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (!task.isSuccessful()) {
                                                    seguir.setText(R.string.seguir);
                                                    Toast.makeText(getActivity(), R.string.erro_seguir, Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });

                            }
                        }
                    }).start();

                } else {
                    // Log.d(TAG, "delete");

                    AlertDialog dialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.title_delete)
                            .setMessage(R.string.msg_parar_seguir)
                            .setNegativeButton(R.string.no, null)
                            .setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    progressBarTemporada.setVisibility(View.GONE);
                                }
                            })
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    myRef.child(mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "")
                                            .child("seguindo")
                                            .child(String.valueOf(series.getId()))
                                            .removeValue();
                                    seguindo = !seguindo;
                                    isSeguindo();
                                    progressBarTemporada.setVisibility(View.GONE);
                                }
                            }).create();

                    dialog.show();
                }
            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (myRef != null && mAuth.getCurrentUser() != null) {
            myRef.removeEventListener(postListener);
        }
    }

    private void setSinopse() {
        // Log.d("SetSinopse", "OverView" + series.getOverview());
        if (series.getOverview() == null || series.getOverview().equals("")) {
            descricao.setText(getString(R.string.sem_sinopse));
        } else {
            descricao.setText(series.getOverview());
        }
    }

    public void setNetflix() {

        if (netflix != null) {
            //Log.d(TAG, "setNetflix: "+netflix.showId);
            if (netflix.showId != 0) {
                netflix_button.setText(R.string.ver_netflix);
            } else {
                netflix_button.setText(R.string.procurar_netflix);
            }
        } else {
            netflix_button.setText(R.string.procurar_netflix);
        }
    }

    private void setAnimacao() {

        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(img_star, "alpha", 0, 1)
                .setDuration(2000);
        ObjectAnimator alphaMedia = ObjectAnimator.ofFloat(voto_media, "alpha", 0, 1)
                .setDuration(2300);
        ObjectAnimator alphaSite = ObjectAnimator.ofFloat(icon_site, "alpha", 0, 1)
                .setDuration(3000);
        ObjectAnimator alphaReviews = ObjectAnimator.ofFloat(icon_reviews, "alpha", 0, 1)
                .setDuration(3250);
        animatorSet.playTogether(alphaStar, alphaMedia, alphaSite, alphaReviews);
        animatorSet.start();
    }

    private void setPoster() {
        if (series.getPosterPath() != null) {
            Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(UtilsFilme.getTamanhoDaImagem(getContext(), 2) ) + series.getPosterPath())
                    .memoryPolicy(MemoryPolicy.NO_STORE, MemoryPolicy.NO_CACHE)
                    .networkPolicy(NetworkPolicy.NO_CACHE, NetworkPolicy.NO_STORE)
                    .into(img_poster);
            img_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), PosterGridActivity.class);
                    intent.putExtra(Constantes.SERIE, series);
                    String transition = getString(R.string.poster_transition);
                    ActivityOptionsCompat compat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), img_poster, transition);
                    ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
                    // Log.d("FilmeInfoFragment", "setPoster: -> " + series.getPosterPath());

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PosterGridActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    //Necessario verificar tipo - filme ou serie;

                }
            });
        } else {
            img_poster.setImageResource(R.drawable.poster_empty);
        }
    }

    private void setProdutora() {
        String primeiraProdutora;
        if (!series.getNetworks().isEmpty()) {
            primeiraProdutora = series.getNetworks().get(0).getName();
            if (primeiraProdutora.length() >= 27) {
                primeiraProdutora = (String) primeiraProdutora.subSequence(0, 27);
                primeiraProdutora = primeiraProdutora.concat("...");
            }
            produtora.setText(primeiraProdutora);
        }
    }

    private void setCategoria() {

        List<Genre> genres = series.getGenres();
        StringBuilder stringBuilder = new StringBuilder("");
        // Log.d("getGeneros", "" + genres.size());
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                stringBuilder.append(" | " + genre.getName());
                // Log.d("Genero", " " + genre.getName());
            }
        }
        categoria.setText(stringBuilder.toString());
    }

    @SuppressWarnings("deprecation")
    private void setVotoMedia() {
        float nota = getMediaNotas();
        if (nota > 0) {
            img_star.setImageResource(R.drawable.icon_star);
            NumberFormat formatter = new DecimalFormat("0.0");
            voto_media.setText(formatter.format(nota));

        } else {
            img_star.setImageResource(R.drawable.icon_star_off);
            voto_media.setText(R.string.valor_zero);
            voto_media.setTextColor(getResources().getColor(R.color.blue));
        }
    }

    private void setTitulo() {
        if (series.getName() != null) {
            titulo.setText(series.getName());
        }
    }

    private void setOriginalTitle() {
        if (series.getOriginalName() != null) {
            original_title.setText(series.getOriginalName());
        } else {
            original_title.setText(getString(R.string.original_title));
        }

    }

    private void setProductionCountries() {

        if (!series.getOriginCountry().isEmpty()) {
            production_countries.setText(series.getOriginCountry().get(0));

        } else {
            production_countries.setText(getString(R.string.não_informado));
        }

    }

    private void setPopularity() {

        ValueAnimator animatorCompat = ValueAnimator.ofFloat(1, series.getPopularity());
        if (series.getPopularity() > 0) {
            // Log.d("POPULARIDADE", " " + series.getPopularity());

            animatorCompat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float valor = (Float) valueAnimator.getAnimatedValue();
                    String popularidade = String.valueOf(valor);

                    if (popularidade.charAt(0) == '0' && isAdded()) {
                        popularidade = popularidade.substring(2, popularidade.length());
                        popularity.setText(popularidade + " " + getString(mil));

                    } else {

                        int posicao = popularidade.indexOf(".") + 2;
                        popularidade = popularidade.substring(0, posicao);
                        String milhoes = null;
                        if (isAdded()) {
                            milhoes = getString(R.string.milhoes);
                        }
                        popularidade = popularidade.concat(" " + milhoes);
                        popularity.setText(popularidade);
                    }

                }
            });

            animatorCompat.setDuration(900);
            //animatorCompat.setTarget(voto_quantidade);
            animatorCompat.setTarget(popularity);
            if (isAdded()) {
                animatorCompat.start();
            }
        }

    }

    public float getMediaNotas() {
        float imdb = 0, tmdb = 0, metascore = 0, tomato = 0;
        int tamanho = 0;

        if (series != null)
            if (series.getVoteAverage() > 0) {
                try {
                    tmdb = series.getVoteAverage();
                    // Log.d(TAG, " tmdb "+ tmdb);
                    tamanho++;
                } catch (Exception e) {
                   // Log.d(TAG, e.getMessage());
                }
            }

        if (imdbDd != null) {
            if (imdbDd.getImdbRating() != null) {
                if (!imdbDd.getImdbRating().isEmpty()) {
                    try {
                        imdb = Float.parseFloat(imdbDd.getImdbRating());
                        // Log.d(TAG, " imdb " + imdb);
                        tamanho++;
                    } catch (Exception e) {
                       // Log.d(TAG, e.getMessage());
                    }
                }
            }

            if (imdbDd.getMetascore() != null) {
                if (!imdbDd.getMetascore().isEmpty()) {
                    try {
                        float meta = Float.parseFloat(imdbDd.getMetascore());
                        float nota = meta / 10;
                        metascore = nota;
                        // Log.d(TAG, " MetaScore " + metascore);
                        tamanho++;
                    } catch (Exception e) {
                       // Log.d(TAG, e.getMessage());
                    }
                }
            }

            if (imdbDd.getTomatoRating() != null) {
                if (!imdbDd.getTomatoRating().isEmpty()) {
                    try {
                        tomato = Float.parseFloat(imdbDd.getTomatoRating());
                      //  Log.d(TAG, " tomato " + tomato);
                        tamanho++;
                    } catch (Exception e) {
                      //  Log.d(TAG, e.getMessage());
                    }
                }
            }
        }

        float media = (tmdb + imdb + metascore + tomato) / tamanho;

        return media;
    }

    private void setCast(){
        if (series.getCredits().getCast().size() > 0) {
            textview_elenco.setVisibility(View.VISIBLE);
            recycle_tvshow_elenco.setHasFixedSize(true);
            recycle_tvshow_elenco.setItemAnimator(new DefaultItemAnimator());
            recycle_tvshow_elenco.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recycle_tvshow_elenco
                    .setAdapter(new CastAdapter(getActivity(), series.getCredits().getCast()));
        }
    }

    private void setCrews(){
        if (series.getCredits().getCrew().size() > 0) {
            textview_crews.setVisibility(View.VISIBLE);
            recycle_tvshow_producao.setHasFixedSize(true);
            recycle_tvshow_producao.setItemAnimator(new DefaultItemAnimator());
            recycle_tvshow_producao.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recycle_tvshow_producao
                    .setAdapter(new CrewAdapter(getActivity(), series.getCredits().getCrew()));
        }
    }

    private void setLancamento() {
        String inicio = null;
        if (series.getFirstAirDate() != null) {
            inicio = (String) series.getFirstAirDate().subSequence(0, 4);
        }
        if (series.getLastAirDate() != null) {
            lancamento.setText(inicio + " - " + series.getLastAirDate().substring(0, 4));
        } else {
            lancamento.setText(inicio);
        }

    }

    private void setTrailer(){
        if (series.getVideos().size() > 0) {
            recycle_tvshow_trailer.setHasFixedSize(true);
            recycle_tvshow_trailer.setItemAnimator(new DefaultItemAnimator());
            recycle_tvshow_trailer.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
            recycle_tvshow_trailer
                    .setAdapter(new TrailerAdapter(getActivity(), series.getVideos(), series.getOverview() != null ? series.getOverview() : ""));
        }

    }

    private void setHome() {
        if (series.getHomepage() != null) {
            if (series.getHomepage().length() > 5) {
                // Log.d("SETHOME", series.getHomepage());
                icon_site.setImageResource(R.drawable.site_on);
            } else {
                icon_site.setImageResource(R.drawable.site_off);
            }
        } else {
            icon_site.setImageResource(R.drawable.site_off);
        }
    }
}
