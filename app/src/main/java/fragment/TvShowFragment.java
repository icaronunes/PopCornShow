package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.BaseActivity;
import activity.CrewsActivity;
import activity.ElencoActivity;
import activity.PersonActivity;
import activity.PosterGridActivity;
import activity.ReviewsActivity;
import activity.SettingsActivity;
import activity.SimilaresActivity;
import activity.Site;
import activity.TemporadaActivity;
import activity.TreilerActivity;
import adapter.TemporadasAdapter;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Config;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.in_production;
import static br.com.icaro.filme.R.string.mil;
import static br.com.icaro.filme.R.string.movieDb;
import static com.squareup.picasso.Picasso.with;


/**
 * Created by icaro on 23/08/16.
 */
public class TvShowFragment extends Fragment {

    final String TAG = TvShowFragment.class.getName();
    int tipo, color;
    TvSeries series;

    TextView titulo, categoria, descricao, voto_media, voto_quantidade, produtora,
            original_title, spoken_languages, production_countries, end, status, temporada,
            imdb, tmdb, popularity, lancamento, textview_crews, textview_elenco;
    ImageView icon_reviews, img_budget, icon_site, icon_collection, img_poster, img_star;
    LinearLayout linear_container;


    public static Fragment newInstance(int tipo, TvSeries series, int color) {
        TvShowFragment fragment = new TvShowFragment();
        Bundle bundle = new Bundle();
        Log.d("TvShowFragment", "Series " + series.getName());
        bundle.putSerializable(Constantes.SERIE, series);
        bundle.putInt(Constantes.COLOR_TOP, color);
        bundle.putInt(Constantes.ABA, tipo);
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
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (tipo == R.string.informacoes) {
            setSinopse();//Chamar depois? pelo metodo setTvShowInfomation?
            setTitulo();
            setCategoria();
            setLancamento();
            setProdutora();
            setHome();
            setVotoMedia();
            setOriginalTitle();
            setProductionCountries();
            setPopularity();
            setTemporada();
            setCast();
            setCrews();
            setTreiler();
            setAnimacao();
            setPoster();
            setStatus();


            icon_site.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d(TAG, "Home " + series.getHomepage());
                    if (series.getHomepage() != "" && series.getHomepage() != null) {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setData(Uri.parse(series.getHomepage()));
                        Log.d(TAG, "Home " + series.getHomepage());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_homepage" );
                        bundle.putString(FirebaseAnalytics.Param.DESTINATION, "Navegador");
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    } else {
                        BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                                getString(R.string.no_site));
                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_homepage" );
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
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_tmdb" );
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, Site.class.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });


            img_star.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (series.getVoteCount() > 0) {
                        BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                                series.getVoteCount()
                                        + " " + getString(R.string.person_vote));

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star" );
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star_SnackBar" );
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    } else {
                        BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                                series.getVoteCount()
                                        + " " + getString(R.string.no_vote));

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star" );
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "SnarBar_sem_informaçao" );
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
                    Log.d("setOnClickListener", "" + series.getName());
                    intent.putExtra(Constantes.NOME, series.getName());
                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, ElencoActivity.class.getName() );
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
                    Log.d("setOnClickListener", "" + series.getName());
                    intent.putExtra(Constantes.NOME, series.getName());
                    startActivity(intent);

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, CrewsActivity.class.getName() );
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });


        }

    }

    private void setStatus() {
        if (series.getStatus() != null) {
            status.setTextColor(color);
            Log.d("setStatus", series.getStatus());
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
            Bundle bundle = new Bundle();
            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
            FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            if (idioma_padrao) {

                if (series.getStatus().equals("Returning Series")) {
                    status.setText(R.string.returnin_series);
                    bundle.putString("status_da_serie", getString(R.string.returnin_series) );
                }
                if (series.getStatus().equals("Ended")) {
                    status.setText(R.string.ended);
                    bundle.putString("status_da_serie", getString(R.string.ended) );
                }
                if (series.getStatus().equals("Canceled")) {
                    status.setText(R.string.canceled);
                    bundle.putString("status_da_serie", getString(R.string.canceled) );
                }
                if (series.getStatus().equals("In Production")) {
                    status.setText(in_production);
                    bundle.putString("status_da_serie", getString(in_production) );
                }
            } else {
                status.setText(series.getStatus());
                bundle.putString("status_da_serie", series.getStatus() + " Idioma Original");
            }
            FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
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
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.temporadas_recycle);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(new TemporadasAdapter(getActivity(), series, onClickListener(), color));

        return view;
    }

    private void setTemporada() {
        if (series.getNumberOfSeasons() > 0) {
            temporada.setText(String.valueOf(series.getNumberOfSeasons()));
        }
    }

    private TemporadasAdapter.TemporadaOnClickListener onClickListener() {
        return new TemporadasAdapter.TemporadaOnClickListener() {
            @Override
            public void onClickTemporada(View view, int position, int color) {
                Log.d("onClickMovieListener", "id " + series.getId());
                Log.d("onClickMovieListener", "season " + series.getSeasons().get(position).getSeasonNumber());
                Intent intent = new Intent(getContext(), TemporadaActivity.class);
                intent.putExtra(Constantes.NOME, getString(R.string.temporada) + " " + series.getSeasons().get(position).getSeasonNumber());
                intent.putExtra(Constantes.TEMPORADA_ID, series.getSeasons().get(position).getSeasonNumber());
                intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                intent.putExtra(Constantes.COLOR_TOP, color);
                intent.putExtra(Constantes.NOME_TVSHOW, series.getName());
                getContext().startActivity(intent);

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TemporadaActivity.class.getName() );
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        };
    }

    private View getViewInformacoes(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.tvshow_info, container, false);

        titulo = (TextView) view.findViewById(R.id.titulo_tvshow);
        categoria = (TextView) view.findViewById(R.id.categoria_tvshow);
        descricao = (TextView) view.findViewById(R.id.descricao);
        end = (TextView) view.findViewById(R.id.end);
        status = (TextView) view.findViewById(R.id.status);
        temporada = (TextView) view.findViewById(R.id.temporadas);
        lancamento = (TextView) view.findViewById(R.id.lancamento);
        voto_media = (TextView) view.findViewById(R.id.voto_media);
        voto_quantidade = (TextView) view.findViewById(R.id.voto_quantidade);
        produtora = (TextView) view.findViewById(R.id.produtora);
        original_title = (TextView) view.findViewById(R.id.original_title);
        spoken_languages = (TextView) view.findViewById(R.id.spoken_languages);
        production_countries = (TextView) view.findViewById(R.id.production_countries);
        popularity = (TextView) view.findViewById(R.id.popularity);
        imdb = (TextView) view.findViewById(R.id.imdb_site);
        tmdb = (TextView) view.findViewById(R.id.tmdb_site);
        img_poster = (ImageView) view.findViewById(R.id.img_poster);
        img_star = (ImageView) view.findViewById(R.id.img_star);
        icon_reviews = (ImageView) view.findViewById(R.id.icon_reviews);
        img_budget = (ImageView) view.findViewById(R.id.img_budget);
        icon_collection = (ImageView) view.findViewById(R.id.icon_collection);
        icon_site = (ImageView) view.findViewById(R.id.icon_site);
        linear_container = (LinearLayout) view.findViewById(R.id.linear_container);
        textview_crews = (TextView) view.findViewById(R.id.textview_crews);
        textview_elenco = (TextView) view.findViewById(R.id.textview_elenco);

        return view;
    }


    private void setSinopse() {
        Log.d("SetSinopse", "OverView" + series.getOverview());
        if (series.getOverview() != null) {

            descricao.setText(series.getOverview());
        } else {
            descricao.setText(getString(R.string.sem_sinopse));
        }
    }

    private void setAnimacao() {
        AnimatorSet animatorSet = new AnimatorSet();
        ObjectAnimator alphaStar = ObjectAnimator.ofFloat(img_star, "alpha", 0, 1)
                .setDuration(2000);
        ObjectAnimator alphaMedia = ObjectAnimator.ofFloat(voto_media, "alpha", 0, 1)
                .setDuration(2300);
        ObjectAnimator alphaBuget = ObjectAnimator.ofFloat(img_budget, "alpha", 0, 1)
                .setDuration(2500);
        ObjectAnimator alphaReviews = ObjectAnimator.ofFloat(icon_reviews, "alpha", 0, 1)
                .setDuration(2800);
        ObjectAnimator alphaSite = ObjectAnimator.ofFloat(icon_site, "alpha", 0, 1)
                .setDuration(3000);
        ObjectAnimator alphaCollecton = ObjectAnimator.ofFloat(icon_collection, "alpha", 0, 1)
                .setDuration(3300);
        animatorSet.playTogether(alphaStar, alphaBuget, alphaMedia, alphaReviews, alphaSite, alphaCollecton);
        animatorSet.start();
    }

    private void setPoster() {
        if (series.getPosterPath() != null) {
            Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + series.getPosterPath()).into(img_poster);
            img_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), PosterGridActivity.class);
                    intent.putExtra(Constantes.SERIE, series);
                    String transition = getString(R.string.poster_transition);
                    ActivityOptionsCompat compat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), img_poster, transition);
                    ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
                    Log.d("FilmeInfoFragment", "setPoster: -> " + series.getPosterPath());

                    Bundle bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PosterGridActivity.class.getName() );
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
        Log.d("getGeneros", "" + genres.size());
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                stringBuilder.append(" | " + genre.getName());
                Log.d("Genero", " " + genre.getName());
            }
        }
        categoria.setText(stringBuilder.toString());
    }

    private void setVotoMedia() {
        if (series.getVoteAverage() > 0) {
            img_star.setImageResource(R.drawable.icon_star);
            voto_media.setText(Float.toString(series.getVoteAverage()));

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
            production_countries.setText(series.getOriginCountry().get(0).toString());

        } else {
            production_countries.setText(getString(R.string.não_informado));
        }

    }

    private void setPopularity() {

        ValueAnimator animatorCompat = ValueAnimator.ofFloat(1, series.getPopularity());
        if (series.getPopularity() > 0) {
            Log.d("POPULARIDADE", " " + series.getPopularity());

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
            animatorCompat.setTarget(voto_quantidade);
            if (isAdded()) {
                animatorCompat.start();
            }
        }

    }

    private void setCast() {
        if (series.getCredits().getCast().size() > 0 && isAdded()) {
            int tamanho = series.getCredits().getCast().size() < 15 ? series.getCredits().getCast().size() : 15;
            textview_elenco.setVisibility(View.VISIBLE);
            for (int i = 0; i < tamanho; i++) {
                final PersonCast personCast = series.getCredits().getCast().get(i);
                View view = getActivity().getLayoutInflater().inflate(R.layout.scroll_elenco, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_elenco_linerlayout);
                View linearInterno = view.findViewById(R.id.scroll_elenco_linearlayout);

                TextView textCastNome = (TextView) linearInterno.findViewById(R.id.textCastNomes);
                TextView textCastPersonagem = (TextView) linearInterno.findViewById(R.id.textCastPersonagem);
                ImageView imageView = (ImageView) view.findViewById(R.id.imgPager);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBarCast);

                progressBar.setVisibility(View.VISIBLE);
                if (personCast.getName() != null || personCast.getCharacter() != null) {
                    textCastPersonagem.setText(personCast.getCharacter());
                    textCastNome.setText(personCast.getName());
                    with(getActivity())
                            .load(UtilsFilme.getBaseUrlImagem(3) + personCast.getProfilePath())
                            .placeholder(R.drawable.person)
                            .into(imageView);
                    progressBar.setVisibility(View.INVISIBLE);
                } else {
                    textCastPersonagem.setVisibility(View.GONE);
                    imageView.setVisibility(View.GONE);
                    textCastNome.setVisibility(View.GONE);
                    textCastPersonagem.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                }

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), PersonActivity.class);
                        intent.putExtra(Constantes.PERSON_ID, personCast.getId());
                        intent.putExtra(Constantes.NOME_PERSON, personCast.getName());
                        getContext().startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName() );
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, personCast.getId());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, personCast.getName());
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    }
                });

                linearLayout.addView(linearInterno);

            }
        }
    }

    private void setCrews() {
        if (series.getCredits().getCrew().size() > 0) {
            int tamanho = series.getCredits().getCrew().size() < 15 ? series.getCredits().getCrew().size() : 15;
            textview_crews.setVisibility(View.VISIBLE);
            Log.d("setCrews", "Tamanho " + series.getCredits().getCrew().size());
            for (int i = 0; i < tamanho; i++) {
                final PersonCrew crew = series.getCredits().getCrew().get(i);
                View view = getActivity().getLayoutInflater().inflate(R.layout.scroll_crews, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_crew_liner);
                View layoutScroll = view.findViewById(R.id.scroll_crews_linearlayout);

                TextView textCrewJob = (TextView) view.findViewById(R.id.textCrewJob);
                TextView textCrewNome = (TextView) view.findViewById(R.id.textCrewNome);
                ImageView imgPagerCrews = (ImageView) view.findViewById(R.id.imgPagerCrews);
                ProgressBar progressBarCrew = (ProgressBar) view.findViewById(R.id.progressBarCrews);

                progressBarCrew.setVisibility(View.VISIBLE);
                if (crew.getName() != null && crew.getJob() != null) {
                    textCrewJob.setText(crew.getJob());
                    textCrewNome.setText(crew.getName());
                    with(getActivity())
                            .load(UtilsFilme.getBaseUrlImagem(1) + crew.getProfilePath())
                            .placeholder(getResources().getDrawable(R.drawable.person))
                            .into(imgPagerCrews);
                    progressBarCrew.setVisibility(View.INVISIBLE);
                } else {
                    textCrewJob.setVisibility(View.GONE);
                    textCrewNome.setVisibility(View.GONE);
                    progressBarCrew.setVisibility(View.GONE);
                    imgPagerCrews.setVisibility(View.GONE);
                }

                imgPagerCrews.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(getContext(), PersonActivity.class);
                        intent.putExtra(Constantes.PERSON_ID, crew.getId());
                        intent.putExtra(Constantes.NOME_PERSON, crew.getName());
                        getContext().startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName() );
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, crew.getId());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, crew.getName());
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    }
                });
                linearLayout.addView(layoutScroll);
            }

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

    private void setTreiler() {

        if (series.getVideos().size() > 0) {
            int tamanho = series.getVideos().size();
            Log.d("TAG", "SetTreiler: -> " + series.getVideos().size());
            for (int i = 0; i < tamanho; i++) {
                Log.d("SetTreiler", "" + series.getVideos().get(i).getKey());
                final String youtube_key = series.getVideos().get(i).getKey();
                View view = getActivity().getLayoutInflater().inflate(R.layout.scroll_treiler, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_treiler_linerlayout);
                View linearteste = view.findViewById(R.id.scroll_treiler_linearlayout);

                final ImageView play_view = (ImageView) view.findViewById(R.id.play_treiler_img);
                play_view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Intent intent = new Intent(getActivity(), TreilerActivity.class);
                        intent.putExtra(Constantes.YOU_TUBE_KEY, youtube_key);
                        if ((series.getOverview() != null)) {
                            intent.putExtra(Constantes.SINOPSE, series.getOverview());
                        }
                        startActivity(intent);

                        Bundle bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TreilerActivity.class.getName());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, series.getId());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, series.getName());
                        bundle.putString("Endereço do youtube", youtube_key);
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    }
                });
                YouTubeThumbnailView thumbnailView = (YouTubeThumbnailView) linearteste.findViewById(R.id.youtube_view_thumbnail);
                thumbnailView.initialize(Config.YOUTUBE_API_KEY, OnInitializedListener(youtube_key));
                Log.d("OnClick", youtube_key);
                //Acontence erros - Necessario corrigir
                linearLayout.addView(linearteste);
            }

        }
    }


    private YouTubeThumbnailView.OnInitializedListener OnInitializedListener(final String youtube_key) {
        return new YouTubeThumbnailView.OnInitializedListener() {

            @Override
            public void onInitializationSuccess(YouTubeThumbnailView youTubeThumbnailView, YouTubeThumbnailLoader youTubeThumbnailLoader) {
                youTubeThumbnailLoader.setVideo(youtube_key);
            }

            @Override
            public void onInitializationFailure(YouTubeThumbnailView youTubeThumbnailView, YouTubeInitializationResult youTubeInitializationResult) {
                FirebaseCrash.report(new Exception("Erro em \"onInitializationFailure\" dentro de "+this.getClass()));
            }
        };
    }

    private void setHome() {
        if (series.getHomepage() != null) {
            if (series.getHomepage().length() > 5) {
                Log.d("SETHOME", series.getHomepage());
                icon_site.setImageResource(R.drawable.site_on);
            } else {
                icon_site.setImageResource(R.drawable.site_off);
            }
        } else {
            icon_site.setImageResource(R.drawable.site_off);
        }
    }
}
