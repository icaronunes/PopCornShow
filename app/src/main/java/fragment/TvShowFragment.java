package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
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
import com.squareup.picasso.Picasso;

import java.util.List;

import activity.BaseActivity;
import activity.CrewsActivity;
import activity.ElencoActivity;
import activity.PersonActivity;
import activity.PosterGridActivity;
import activity.ProdutoraActivity;
import activity.SimilaresActivity;
import activity.TreilerActivity;
import br.com.icaro.filme.R;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import info.movito.themoviedbapi.model.tv.TvSeries;
import utils.Config;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.mil;
import static com.google.android.gms.cast.internal.zzl.pa;
import static com.squareup.picasso.Picasso.with;


/**
 * Created by icaro on 23/08/16.
 */
public class TvShowFragment extends Fragment {

    final String TAG = TvShowFragment.class.getName();
    int tipo;
    TvSeries series;

    TextView titulo, categoria, descricao, voto_media, voto_quantidade, produtora,
            original_title, spoken_languages, production_countries, end, status, temporada,
            popularity, lancamento, textview_crews, textview_elenco, textview_similares;
    ImageView icon_reviews, img_budget, icon_site, icon_collection, img_poster, img_star;
    LinearLayout linear_container;


    public static Fragment newInstance(int tipo, TvSeries series) {
        TvShowFragment fragment = new TvShowFragment();
        Bundle bundle = new Bundle();
        Log.d("TvShowFragment", "Series " + series.getName());
        bundle.putSerializable(Constantes.SERIE, series);
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
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
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
                } else {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_site));
                }
            }
        });


        img_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (series.getVoteCount() > 0) {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            series.getVoteCount()
                                    + " " + getString(R.string.person_vote));
                    //Sem FAB
                } else {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            series.getVoteCount()
                                    + " " + getString(R.string.no_vote));
                }
            }
        });


        textview_elenco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ElencoActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                Log.d("setOnClickListener", "" + series.getName());
                intent.putExtra(Constantes.NOME_FILME, series.getName());
                startActivity(intent);
                // ???????????????? Solicitando id de filme, necessario verificar o tipo;
            }
        });

        textview_crews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CrewsActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                Log.d("setOnClickListener", "" + series.getName());
                intent.putExtra(Constantes.NOME_TVSHOW, series.getName());
                startActivity(intent);
            }
        });

        textview_similares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SimilaresActivity.class);
                intent.putExtra(Constantes.TVSHOW_ID, series.getId());
                intent.putExtra(Constantes.NOME_TVSHOW, series.getName());
                startActivity(intent);
            }
        });

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
        }

    }

    private void setStatus() {
        if (series.getStatus() != null) {
            status.setText(series.getStatus());
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        switch (tipo) {

            case R.string.temporadas: {
                return null;//getViewTemporadas(inflater, container);
            }
            case R.string.informacoes: {
                return getViewInformacoes(inflater, container);
            }
        }
        return null;
    }

    private void setTemporada() {
        if (series.getNumberOfSeasons()  > 0){
            temporada.setText(String.valueOf(series.getNumberOfSeasons()));
        }
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
        textview_similares = (TextView) view.findViewById(R.id.textview_similares);
        original_title = (TextView) view.findViewById(R.id.original_title);
        spoken_languages = (TextView) view.findViewById(R.id.spoken_languages);
        production_countries = (TextView) view.findViewById(R.id.production_countries);
        popularity = (TextView) view.findViewById(R.id.popularity);
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
                    intent.putExtras(getArguments());
                    String transition = getString(R.string.poster_transition);
                    ActivityOptionsCompat compat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), img_poster, transition);
                    ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
                    Log.d("FilmeBottonFragment", "setPoster: -> " + series.getPosterPath());
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
            produtora.setTextColor(getResources().getColor(R.color.primary));

            final String linkProdutora = primeiraProdutora;
            produtora.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProdutoraActivity.class);
                    intent.putExtra(Constantes.PRODUTORA, linkProdutora);
                    intent.putExtra(Constantes.PRODUTORA_ID, series.getNetworks().get(0).getId());
                    startActivity(intent);
                }
            });
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
            Log.d("Produtores Paises", "" + series.getOriginCountry().get(0).toString());
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
                        Log.d("OnClick", youtube_key);
                        intent.putExtra(Constantes.YOU_TUBE_KEY, youtube_key);
                        if ((series.getOverview() != null)) {
                            intent.putExtra(Constantes.SINOPSE, series.getOverview());
                        }
                        startActivity(intent);

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
