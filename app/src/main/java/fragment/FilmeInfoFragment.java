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
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeThumbnailLoader;
import com.google.android.youtube.player.YouTubeThumbnailView;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crash.FirebaseCrash;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import activity.BaseActivity;
import activity.CrewsActivity;
import activity.ElencoActivity;
import activity.FilmeActivity;
import activity.PersonActivity;
import activity.PosterGridActivity;
import activity.ProdutoraActivity;
import activity.ReviewsActivity;
import activity.SettingsActivity;
import activity.SimilaresActivity;
import activity.Site;
import activity.TreilerActivity;
import adapter.CollectionPagerAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.ArtworkType;
import info.movito.themoviedbapi.model.Collection;
import info.movito.themoviedbapi.model.CollectionInfo;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.Language;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.Multi;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import utils.Config;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.string.mil;
import static com.squareup.picasso.Picasso.with;


/**
 * Created by icaro on 03/07/16.
 */

public class FilmeInfoFragment extends Fragment {

    TextView titulo, categoria, time_filme, descricao, voto_media,  produtora,
            original_title, spoken_languages, production_countries, imdb, tmdb,
            popularity, lancamento, textview_crews, textview_elenco, textview_similares;
    ImageView img_poster, img_star;
    MovieDb movieDb;
    ImageView icon_reviews, img_budget, icon_site, icon_collection, imgPagerSimilares;
    LinearLayout linear_container;
    CollectionInfo info;
    MovieResultsPage similarMovies;
    private int color_top;
    Bundle bundle;

    //************* Alguns metodos senco chamados 2 vezes

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Bundle bundle = getArguments();
            movieDb = (MovieDb) bundle.getSerializable(Constantes.FILME);
            similarMovies = (MovieResultsPage) bundle.getSerializable(Constantes.SIMILARES);
            Log.d("FilmeInfoFragment", "onCreate");
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_filme, container, false);

        titulo = (TextView) view.findViewById(R.id.titulo_text);
        categoria = (TextView) view.findViewById(R.id.categoria_filme);
        time_filme = (TextView) view.findViewById(R.id.time_filme);
        descricao = (TextView) view.findViewById(R.id.descricao);
        voto_media = (TextView) view.findViewById(R.id.voto_media);
        textview_similares = (TextView) view.findViewById(R.id.textview_similares);
        produtora = (TextView) view.findViewById(R.id.produtora);
        original_title = (TextView) view.findViewById(R.id.original_title);
        spoken_languages = (TextView) view.findViewById(R.id.spoken_languages);
        production_countries = (TextView) view.findViewById(R.id.production_countries);
        popularity = (TextView) view.findViewById(R.id.popularity);
        img_poster = (ImageView) view.findViewById(R.id.img_poster);
        img_star = (ImageView) view.findViewById(R.id.img_star);
        imdb = (TextView) view.findViewById(R.id.imdb_site);
        tmdb = (TextView) view.findViewById(R.id.tmdb_site);
        icon_reviews = (ImageView) view.findViewById(R.id.icon_reviews);
        img_budget = (ImageView) view.findViewById(R.id.img_budget);
        icon_collection = (ImageView) view.findViewById(R.id.icon_collection);
        icon_site = (ImageView) view.findViewById(R.id.icon_site);
        linear_container = (LinearLayout) view.findViewById(R.id.linear_container);
        lancamento = (TextView) view.findViewById(R.id.lancamento);
        textview_crews = (TextView) view.findViewById(R.id.textview_crews);
        textview_elenco = (TextView) view.findViewById(R.id.textview_elenco);
        Log.d("FilmeInfoFragment", "onCreateView");

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTitulo();
        setCategoria();
        setLancamento();
        setTimeFilme();
        setProdutora();
        setSinopse();
        setPoster();
        setBuget();
        setHome();
        setVotoMedia();
        setOriginalTitle();
        setSpokenLanguages();
        setProductionCountries();
        setPopularity();
        setReviews();
        setCollectoin();
        setCast();
        setCrews();
        setTreiler();
        setSimilares();
        setAnimacao();

        icon_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieDb.getReviews().size() >= 1) {
                    Intent intent = new Intent(getContext(), ReviewsActivity.class);
                    intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                    intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());

                    startActivity(intent);

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_reviews" );
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, ReviewsActivity.class.getName());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                } else {
                    Log.d("SetSnack", "" + movieDb.getBudget());
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_message));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_reviews_SnackBar" );
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            }
        });

        imdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Site.class);
                intent.putExtra(Constantes.SITE,
                        "https:www.imdb.com/title/" + movieDb.getImdbID() + "/");
                startActivity(intent);

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_imdb" );
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, Site.class.getName());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        tmdb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Site.class);
                intent.putExtra(Constantes.SITE,
                        "https://www.themoviedb.org/movie/" + movieDb.getId() + "/");
                Log.d("TMDB",  "https://www.themoviedb.org/movie/" + movieDb.getId() + "/" );
                startActivity(intent);

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_tmdb" );
                bundle.putString(FirebaseAnalytics.Param.DESTINATION, Site.class.getName());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
            }
        });

        img_budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieDb.getBudget() > 0) {
                    Log.d("SetSnack", "" + movieDb.getBudget());
                    String valor = String.valueOf(movieDb.getBudget());
                    valor.length();
                    valor = valor.substring(0, valor.length() - 6);
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            getString(R.string.orcamento_budget) + " " +
                                    getString(R.string.dollar)
                                    + " " + valor + " " + getString(R.string.milhoes_budget));

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_budget" );
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "SnackBar_Sucesso");
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_budget));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_budget" );
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "SnackBar_sem_valor");
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            }
        });

        icon_site.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d("FilmeInfoFragment", "Home " + movieDb.getHomepage());
                if (movieDb.getHomepage() != "" && movieDb.getHomepage() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(movieDb.getHomepage()));
                    Log.d("FilmeInfoFragment", "Home " + movieDb.getHomepage());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_homepage" );
                    bundle.putString(FirebaseAnalytics.Param.DESTINATION, "Navegador");
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            getString(R.string.no_site));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_homepage" );
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, "Sem homepage");
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            }
        });


        img_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieDb.getVoteCount() > 0) {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            movieDb.getVoteCount()
                                    + " " + getString(R.string.person_vote));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star" );
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star_SnackBar" );
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            movieDb.getVoteCount()
                                    + " " + getString(R.string.no_vote));
                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star" );
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "SnarBar_sem_informaçao" );
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            }
        });

        icon_collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieDb.getBelongsToCollection() != null) {
                    final int id = movieDb.getBelongsToCollection().getId();
                    new Thread() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                            boolean idioma_padrao = sharedPref.getBoolean(SettingsActivity.PREF_IDIOMA_PADRAO, true);
                            if (idioma_padrao) {
                                info = FilmeService.getTmdbCollections()
                                        .getCollectionInfo(id, Locale.getDefault().getLanguage()+"-"+Locale.getDefault().getCountry() + ",en,null");
                                getCollection(info);
                            } else {
                                info = FilmeService.getTmdbCollections()
                                        .getCollectionInfo(id, "en");
                                getCollection(info);
                            }
                        }
                    }.start();

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "icon_star" );
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "SnarBar_sem_informaçao" );
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                } else {
                    BaseActivity.SnackBar(getActivity().findViewById(R.id.fab_menu_filme),
                            getString(R.string.collecion_off));
                }
            }

        });

        textview_elenco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ElencoActivity.class);
                intent.putExtra(Constantes.ID, movieDb.getId());
                intent.putExtra(Constantes.MEDIATYPE, movieDb.getMediaType());
                Log.d("setOnClickListener", "" + movieDb.getTitle());
                intent.putExtra(Constantes.NOME, movieDb.getTitle());
                startActivity(intent);

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, ElencoActivity.class.getName() );
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        });

        textview_crews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CrewsActivity.class);
                intent.putExtra(Constantes.ID, movieDb.getId());
                Log.d("setOnClickListener", "" + movieDb.getTitle());
                intent.putExtra(Constantes.MEDIATYPE, movieDb.getMediaType());
                intent.putExtra(Constantes.NOME, movieDb.getTitle());
                startActivity(intent);


                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, CrewsActivity.class.getName() );
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        });

        textview_similares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), SimilaresActivity.class);
                intent.putExtra(Constantes.FILME_ID, movieDb.getId());
                intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                startActivity(intent);

                bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, SimilaresActivity.class.getName() );
                bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

            }
        });
    }

    private void getCollection(final CollectionInfo info) {
        if (info.getParts().size() != 0) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialog_collection = inflater.inflate(R.layout.dialog_collection, null);
                    ViewPager pager = (ViewPager) dialog_collection.findViewById(R.id.viewpager_collection);
                    pager.setAdapter(new CollectionPagerAdapter(info, getContext(), movieDb.getId()));
                    builder.setView(dialog_collection);
                    builder.show();

                }
            });
        } else {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getContext(), R.string.sem_informacao_colletion, Toast.LENGTH_SHORT).show();
                }
            });
        }

        bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, "Collection" );
        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
    }

    public void setSinopse() {
        Log.d("SetSinopse", "OverView" + movieDb.getOverview());
        if (movieDb.getOverview() != null) {

            descricao.setText(movieDb.getOverview());
        } else {
            descricao.setText(getString(R.string.sem_sinopse));
        }
    }

    private void setTicket() {
        Collection collection = movieDb.getBelongsToCollection();
        String data = movieDb.getReleaseDate();
        String query = collection.getTitle();
        StringBuilder stringBuilder = new StringBuilder("https://play.google.com/store/search?c=tvSeries&q=");
        stringBuilder.append(query);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.format(date);
        Log.d("Format", format.format(date));
        Log.d("Format", movieDb.toString());
        if (data != null && data.contains(format.format(date))) {
            stringBuilder.append(" " + data.substring(0, 4));

        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(String.valueOf(stringBuilder)));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.d("icon_collection", stringBuilder.toString());
            startActivity(intent);
        }
        //   https://play.google.com/store/search?c=movies&q=finding%20dory%20201
    }


    public void setBuget() {

        if (movieDb.getBudget() > 0) {
            img_budget.setImageResource(R.drawable.orcamento);
        } else {
            img_budget.setImageResource(R.drawable.orcamento2);

        }

    }

    public void setAnimacao() {
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
        if (movieDb.getPosterPath() != null && movieDb.getImages(ArtworkType.POSTER).size() > 0) {
            Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + movieDb.getPosterPath()).into(img_poster);
            img_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), PosterGridActivity.class);
                    String transition = getString(R.string.poster_transition);
                    intent.putExtra(Constantes.FILME, movieDb);
                    ActivityOptionsCompat compat = ActivityOptionsCompat
                            .makeSceneTransitionAnimation(getActivity(), img_poster, transition);
                    ActivityCompat.startActivity(getActivity(), intent, compat.toBundle());
                    Log.d("FilmeInfoFragment", "setPoster: -> " + movieDb.getId());

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PosterGridActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                }
            });
        } else {
            img_poster.setImageResource(R.drawable.poster_empty);
        }
    }

    private void setProdutora() {
        String primeiraProdutora;
        if (!movieDb.getProductionCompanies().isEmpty()) {
            primeiraProdutora = movieDb.getProductionCompanies().get(0).getName();
            if (primeiraProdutora.length() >= 27) {
                primeiraProdutora = (String) primeiraProdutora.subSequence(0, 27);
                primeiraProdutora = primeiraProdutora.concat("...");
            }
            produtora.setText(primeiraProdutora);
            produtora.setTextColor(getResources().getColor(R.color.primary));

            final String finalPrimeiraProdutora = primeiraProdutora;
            produtora.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), ProdutoraActivity.class);
                    intent.putExtra(Constantes.PRODUTORA, finalPrimeiraProdutora);// não usado
                    intent.putExtra(Constantes.PRODUTORA_ID, movieDb.getProductionCompanies().get(0).getId());
                    intent.putExtra(Constantes.MEDIATYPE, Multi.MediaType.MOVIE);// Não usado
                    startActivity(intent);

                    bundle = new Bundle();
                    bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, ProdutoraActivity.class.getName());
                    bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                    bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);
                }
            });
        } else {
            getView().findViewById(R.id.label_produtora).setVisibility(View.GONE);
        }
    }


    private void setVotoMedia() {
        if (movieDb.getVoteAverage() > 0) {
            img_star.setImageResource(R.drawable.icon_star);
            voto_media.setText(Float.toString(movieDb.getVoteAverage()));

        } else {
            img_star.setImageResource(R.drawable.icon_star_off);
            voto_media.setText(R.string.valor_zero);
            voto_media.setTextColor(getResources().getColor(R.color.blue));
        }
    }


    private void setCategoria() {

        List<Genre> genres = movieDb.getGenres();
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

    private void setTitulo() {
        if (movieDb.getTitle() != null) {
            titulo.setText(movieDb.getTitle());
        }
    }

    private void setTimeFilme() {
        Log.d("setTimeFilme", String.valueOf(movieDb.getRuntime()));
        if (movieDb.getRuntime() > 0) {
            int horas = 0;
            int minutos;
            int tempo = movieDb.getRuntime();

            while (tempo > 60) {
                horas++;
                tempo = tempo - 60;
            }
            minutos = tempo;
            time_filme.setText(String.valueOf(horas + " " + getString(horas > 1 ? R.string.horas : R.string.hora)
                    + " " + minutos + " " + getString(R.string.minutos)));//

            Log.d("setTimeFilme", String.valueOf(horas + " hrs " + minutos + getString(R.string.minutos)));
        } else {
            time_filme.setText(getString(R.string.tempo_nao_informado));
        }
    }

    private void setOriginalTitle() {
        if (movieDb.getOriginalTitle() != null) {
            original_title.setText(movieDb.getOriginalTitle());
        } else {
            original_title.setText(getString(R.string.original_title));
        }

    }

    private void setSpokenLanguages() {
        if (!movieDb.getSpokenLanguages().isEmpty()) {
            List<Language> languages = movieDb.getSpokenLanguages();
            if (languages.size() > 0) {
                spoken_languages.setText(languages.get(0).getName()); //????????????
            }
        } else {
            spoken_languages.setText(getString(R.string.não_informado));
        }
    }

    private void setProductionCountries() {

        if (!movieDb.getProductionCountries().isEmpty()) {
            List<ProductionCountry> productionCountries = movieDb.getProductionCountries();
            production_countries.setText(productionCountries.get(0).getName());
        } else {

            production_countries.setText(getString(R.string.não_informado));
            Log.d("Produtores Paises", "" + movieDb.getProductionCountries());
        }

    }

    private void setPopularity() {

        ValueAnimator animatorCompat = ValueAnimator.ofFloat(1, movieDb.getPopularity());
        if (movieDb.getPopularity() > 0) {
            Log.d("POPULARIDADE", " " + movieDb.getPopularity());

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


    private void setCast() {
        if (movieDb.getCast().size() > 0 && isAdded()) {
            int tamanho = movieDb.getCast().size() < 15 ? movieDb.getCast().size() : 15;
            textview_elenco.setVisibility(View.VISIBLE);
            for (int i = 0; i < tamanho; i++) {
                final PersonCast personCast = movieDb.getCredits().getCast().get(i);
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

                        bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
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
        if (movieDb.getCredits().getCrew().size() > 0) {
            int tamanho = movieDb.getCredits().getCrew().size() < 15 ? movieDb.getCredits().getCrew().size() : 15;
            textview_crews.setVisibility(View.VISIBLE);
            Log.d("setCrews", "Tamanho " + movieDb.getCredits().getCrew().size());
            for (int i = 0; i < tamanho; i++) {
                final PersonCrew crew = movieDb.getCredits().getCrew().get(i);
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

                        bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, crew.getId());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, crew.getName());
                        FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                    }
                });
                linearLayout.addView(layoutScroll);
            }

        }

    }

    private void setSimilares() {
        if (similarMovies.getResults().size() > 0 && similarMovies.getResults() != null) {
            int tamanho = similarMovies.getResults().size() < 10 ? similarMovies.getResults().size() : 10;
            getView().findViewById(R.id.textview_similares).setVisibility(View.VISIBLE);
            textview_similares.setTextColor(getResources().getColor(R.color.primary));
            for (int i = 0; i < tamanho; i++) {
                final MovieDb movie = similarMovies.getResults().get(i);

                View view = getActivity().getLayoutInflater().inflate(R.layout.scroll_similares, (ViewGroup) getView(), false);
                LinearLayout linearLayout = (LinearLayout) getView().findViewById(R.id.scroll_similares_linerlayout);
                View layoutScroll = view.findViewById(R.id.scroll_similares_linear);

                ProgressBar progressBarSimilares = (ProgressBar) view.findViewById(R.id.progressBarSimilares);
                TextView textSimilares = (TextView) view.findViewById(R.id.textSimilaresNome);
                imgPagerSimilares = (ImageView) view.findViewById(R.id.imgPagerSimilares);


                if (movie.getTitle() != null && movie.getPosterPath() != null) {
                    if (movie.getTitle().length() > 21) {
                        String title = movie.getTitle().substring(0, 18);
                        title = title.concat("...");
                        textSimilares.setText(title);

                    } else {
                        textSimilares.setText(movie.getTitle());
                    }
                    with(getActivity())
                            .load(UtilsFilme.getBaseUrlImagem(1) + movie.getPosterPath())
                            .placeholder(getResources().getDrawable(R.drawable.poster_empty))
                            .into(imgPagerSimilares, new Callback() {
                                @Override
                                public void onSuccess() {
                                    color_top = UtilsFilme.loadPalette(imgPagerSimilares);
                                    //loadPalette();
                                }

                                @Override
                                public void onError() {

                                }
                            });
                    progressBarSimilares.setVisibility(View.INVISIBLE);

                    imgPagerSimilares.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(getContext(), FilmeActivity.class);
                            intent.putExtra(Constantes.COLOR_TOP, color_top);
                            intent.putExtra(Constantes.NOME_FILME, movie.getTitle());
                            intent.putExtra(Constantes.FILME_ID, movie.getId());
                            startActivity(intent);

                            bundle = new Bundle();
                            bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, PersonActivity.class.getName());
                            bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movie.getId());
                            bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movie.getTitle());
                            FirebaseAnalytics.getInstance(getContext()).logEvent(FirebaseAnalytics.Event.SELECT_CONTENT, bundle);

                        }
                    });

                } else {
                    textSimilares.setVisibility(View.GONE);
                    progressBarSimilares.setVisibility(View.GONE);
                    imgPagerSimilares.setVisibility(View.GONE);
                }
                linearLayout.addView(layoutScroll);
            }
        }
    }


    private void setLancamento() {
        if (movieDb.getReleaseDate() != null) {
            for (int i = 0; i < movieDb.getReleases().size(); i++) {
                if (Locale.getDefault().getCountry().equalsIgnoreCase(movieDb.getReleases().get(i).getCountry())) {
                    lancamento.setText(movieDb.getReleases().get(i).getReleaseDate());
                    // Adicionar Botão de comprar depois
                    break;
                }
            }
        }
    }

    private void setReviews() {

        if (movieDb.getReviews().size() >= 1) {
            icon_reviews.setImageResource(R.drawable.icon_reviews);
        } else {
            icon_reviews.setImageResource(R.drawable.icon_no_reviews);
        }

    }

    private void setTreiler() {

        if (movieDb.getVideos().size() > 0) {
            int tamanho = movieDb.getVideos().size();
            Log.d("FilmeInfoFragment", "SetTreiler: -> " + movieDb.getVideos().size());
            for (int i = 0; i < tamanho; i++) {
                Log.d("SetTreiler", "" + movieDb.getVideos().get(i).getKey());
                final String youtube_key = movieDb.getVideos().get(i).getKey();
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
                        if ((movieDb.getOverview() != null)) {
                            intent.putExtra(Constantes.SINOPSE, movieDb.getOverview());
                        }
                        startActivity(intent);

                        bundle = new Bundle();
                        bundle.putString(FirebaseAnalytics.Event.SELECT_CONTENT, TreilerActivity.class.getName());
                        bundle.putInt(FirebaseAnalytics.Param.ITEM_ID, movieDb.getId());
                        bundle.putString(FirebaseAnalytics.Param.ITEM_NAME, movieDb.getTitle());
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
        if (movieDb.getHomepage() != null) {
            if (movieDb.getHomepage().length() > 5) {
                Log.d("SETHOME", movieDb.getHomepage());
                icon_site.setImageResource(R.drawable.site_on);
            } else {
                icon_site.setImageResource(R.drawable.site_off);
            }
        } else {
            icon_site.setImageResource(R.drawable.site_off);
        }
    }

    private void setCollectoin() {
        if (movieDb.getBelongsToCollection() != null) {

            icon_collection.setImageResource(R.drawable.collection_on);
        } else {

            icon_collection.setImageResource(R.drawable.collection_off);
        }
    }


}
