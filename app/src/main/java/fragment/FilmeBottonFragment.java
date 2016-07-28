package fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import activity.CrewsActivity;
import activity.ElencoActivity;
import activity.FilmeActivity;
import activity.PosterActivity;
import activity.PosterGridActivity;
import activity.ReviewsActivity;
import activity.TreilerActivity;
import adapter.CollectionPagerAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.Collection;
import info.movito.themoviedbapi.model.CollectionInfo;
import info.movito.themoviedbapi.model.Genre;
import info.movito.themoviedbapi.model.Language;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.model.ProductionCountry;
import info.movito.themoviedbapi.model.core.MovieResultsPage;
import info.movito.themoviedbapi.model.people.PersonCast;
import info.movito.themoviedbapi.model.people.PersonCrew;
import utils.Config;
import utils.Constantes;
import utils.UtilsFilme;

import static br.com.icaro.filme.R.id.imgPagerSimilares;
import static br.com.icaro.filme.R.string.nome;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.credits;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.releases;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.reviews;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.similar;
import static info.movito.themoviedbapi.TmdbMovies.MovieMethod.videos;


/**
 * Created by icaro on 03/07/16.
 */

public class FilmeBottonFragment extends Fragment {

    TextView titulo, categoria, time_filme, sinopse, voto_media, voto_quantidade, produtora,
            original_title, spoken_languages, production_countries, popularity, lancamento, textview_crews, textview_elenco;
    ImageView img_poster, img_star;
    int id_filme;
    MovieDb movieDb;
    ImageView icon_reviews, img_budget, icon_site, icon_collection,  imgPagerSimilares;
    LinearLayout linear_container;
    CollectionInfo info;
    MovieResultsPage similarMovies;
    TMDVAsync tmdvAsync;
    private int color_top;

    //************* Alguns metodos senco chamados 2 vezes

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        if (getArguments() != null) {
            id_filme = getArguments().getInt(Constantes.FILME_ID);
            getActivity().getIntent().getIntExtra(Constantes.ABA, 0);
            Log.d("FilmeBottonFragment", "onCreate -> " + id_filme);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_container_filme, container, false);

        titulo = (TextView) view.findViewById(R.id.titulo_text);
        categoria = (TextView) view.findViewById(R.id.categoria_filme);
        time_filme = (TextView) view.findViewById(R.id.time_filme);
        sinopse = (TextView) view.findViewById(R.id.descricao);
        voto_media = (TextView) view.findViewById(R.id.voto_media);
        voto_quantidade = (TextView) view.findViewById(R.id.voto_quantidade);
        produtora = (TextView) view.findViewById(R.id.produtora);
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
        lancamento = (TextView) view.findViewById(R.id.lancamento);
        textview_crews = (TextView) view.findViewById(R.id.textview_crews);
        textview_elenco = (TextView) view.findViewById(R.id.textview_elenco);


        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        tmdvAsync.cancel(true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("FilmeBottonFragment", "onActivityCreated -> " + id_filme);
        if (id_filme != 0) {
            tmdvAsync = new TMDVAsync();
            tmdvAsync.execute();
        }

        icon_reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieDb.getReviews().size() >= 1) {
                    Intent intent = new Intent(getContext(), ReviewsActivity.class);
                    intent.putExtra(Constantes.FILME_ID, id_filme);
                    intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                    startActivity(intent);
                } else {
                    Log.d("SetSnack", "" + movieDb.getBudget());
                    Snackbar.make(linear_container, getResources().getString(R.string.no_message)
                            , Snackbar.LENGTH_SHORT)
                            .show();
                }
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
                    Snackbar.make(linear_container, getResources().getString(R.string.orcamento_budget) + " " +
                                    getResources().getString(R.string.dollar)
                                    + " " + valor + " " + getResources().getString(R.string.milhoes_budget)
                            , Snackbar.LENGTH_LONG)
                            .show();
                } else {
                    Snackbar.make(linear_container, R.string.no_budget
                            , Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });

        icon_site.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (movieDb.getHomepage() != null) {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(movieDb.getHomepage()));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Snackbar.make(linear_container, R.string.no_site
                            , Snackbar.LENGTH_SHORT)
                            .show();
                }
            }
        });


        img_star.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (movieDb.getVoteCount() > 0) {
                    Snackbar.make(linear_container, movieDb.getVoteCount()
                                    + " " + getResources().getString(R.string.person_vote)
                            , Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    Snackbar.make(linear_container, R.string.no_vote
                            , Snackbar.LENGTH_SHORT)
                            .show();
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
                            info = FilmeService.getTmdbCollections()
                                    .getCollectionInfo(id, getResources().getString(R.string.IDIOMAS));
                            getCollection(info);
                        }
                    }.start();
                } else {
                    Snackbar.make(linear_container, R.string.collecion_off
                            , Snackbar.LENGTH_SHORT)
                            .show();
                }
            }

        });

        textview_elenco.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), ElencoActivity.class);
                intent.putExtra(Constantes.FILME_ID, id_filme);
                Log.d("setOnClickListener", "" + movieDb.getTitle());
                intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                startActivity(intent);
            }
        });

        textview_crews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), CrewsActivity.class);
                intent.putExtra(Constantes.FILME_ID, id_filme);
                Log.d("setOnClickListener", "" + movieDb.getTitle());
                intent.putExtra(Constantes.NOME_FILME, movieDb.getTitle());
                startActivity(intent);
            }
        });
    }

    private void getCollection(final CollectionInfo info) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                View dialog_collection = inflater.inflate(R.layout.dialog_collection, null);
                ViewPager pager = (ViewPager) dialog_collection.findViewById(R.id.viewpager_collection);
                pager.setAdapter(new CollectionPagerAdapter(info, getContext(), id_filme));
                builder.setView(dialog_collection);
                builder.show();
            }
        });
    }

    public void setSinopse() {
        Log.d("SetSinopse", "OverView" + movieDb.getOverview());
        if (movieDb.getOverview() != null) {

            sinopse.setText(movieDb.getOverview());
        } else {
            sinopse.setText(getResources().getString(R.string.sem_sinopse));
        }
    }

    private void setTicket() {
        Collection collection = movieDb.getBelongsToCollection();
        String data = movieDb.getReleaseDate();
        String query = collection.getTitle();
        StringBuilder stringBuilder = new StringBuilder("https://play.google.com/store/search?c=movies&q=");
        stringBuilder.append(query);
        Date date = new Date();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        format.format(date);
        Log.d("Format", format.format(date));
        Log.d("Format", movieDb.toString());
        if (data != null && data.contains(format.format(date))) {
            // http://www.ingresso.com/sao-paulo/espetaculo/espetaculo/cinema/futuro-junho
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
        if (movieDb.getPosterPath() != null) {
            Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + movieDb.getPosterPath()).into(img_poster);
            img_poster.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Intent intent = new Intent(getContext(), PosterActivity.class);
                    Intent intent = new Intent(getContext(), PosterGridActivity.class);
                    intent.putExtra(Constantes.FILME_ID, id_filme);
                    startActivity(intent);
//                    ActivityOptionsCompat opts = ActivityOptionsCompat.makeCustomAnimation(getActivity(),
//                            android.R.anim.fade_in, android.R.anim.fade_out);
//                    ActivityCompat.startActivity(getActivity(), intent, opts.toBundle());
                    Log.d("FilmeBottonFragment", "setPoster: -> " + id_filme);

                }
            });
        } else {
            img_poster.setImageResource(R.drawable.poster_empty);
        }
    }

    private void setProdutora() {
        if (!movieDb.getProductionCompanies().isEmpty()) {
            String sProdutora = movieDb.getProductionCompanies().get(0).getName();
            if (sProdutora.length() >= 27) {
                sProdutora = (String) sProdutora.subSequence(0, 27);
                sProdutora = sProdutora.concat("...");
            }
            produtora.setText(sProdutora);
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


            time_filme.setText(String.valueOf(horas + " " + getResources().getString(horas > 1 ? R.string.horas : R.string.hora)
                    + " " + minutos + " " + getResources().getString(R.string.minutos)));

            Log.d("setTimeFilme", String.valueOf(horas + " hrs " + minutos + getResources().getString(R.string.minutos)));
        } else {
            time_filme.setText(getResources().getString(R.string.tempo_nao_informado));
        }
    }

    private void setOriginalTitle() {
        if (movieDb.getOriginalTitle() != null) {
            original_title.setText(movieDb.getOriginalTitle());
        } else {
            original_title.setText(getResources().getString(R.string.original_title));
        }

    }

    private void setSpokenLanguages() {
        if (!movieDb.getSpokenLanguages().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("");
            List<Language> languages = movieDb.getSpokenLanguages();
            for (Language language : languages) {
                stringBuilder.append(language.getName() + " ");
                Log.d("Genero", " " + language.getName());
            }
            spoken_languages.setText(stringBuilder);
        } else {
            spoken_languages.setText(getResources().getString(R.string.não_informado));
        }
    }

    private void setProductionCountries() {

        if (!movieDb.getProductionCompanies().isEmpty()) {
            StringBuilder stringBuilder = new StringBuilder("");
            List<ProductionCountry> productionCompanyListanguages = movieDb.getProductionCountries();
            for (ProductionCountry productionCountry : productionCompanyListanguages) {
                stringBuilder.append(productionCountry.getName() + " ");
                Log.d("productionCompany", " " + productionCountry.getName());
            }
            production_countries.setText(stringBuilder);
        } else {

            production_countries.setText(getResources().getString(R.string.não_informado));
            Log.d("Produtores Paises", "" + movieDb.getProductionCountries());
        }

    }

    private void setPopularity() {

        ValueAnimator animatorCompat = ValueAnimator.ofFloat(1, movieDb.getPopularity());
        if (movieDb.getPopularity() > 0 ) {
            Log.d("POPULARIDADE", " " + movieDb.getPopularity());

            animatorCompat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    float valor = (Float) valueAnimator.getAnimatedValue();
                    String popularidade = String.valueOf(valor);

                    if (popularidade.charAt(0) == '0') {
                        popularidade = popularidade.substring(2, popularidade.length());
                        popularity.setText(popularidade + " " + getResources().getString(R.string.mil));


                    } else {
                        int posicao = popularidade.indexOf(".") + 2;
                        popularidade = popularidade.substring(0, posicao);
                        popularidade = popularidade.concat(" " + getActivity().getResources().getString(R.string.milhoes));
                        popularity.setText(popularidade);
                    }

                }
            });

            animatorCompat.setDuration(1000);
            animatorCompat.setTarget(voto_quantidade);

                animatorCompat.start();

        }

    }


    private void setCast() {
        if (movieDb.getCast().size() > 0) {
            int tamanho = movieDb.getCast().size() < 15 ? movieDb.getCast().size() : 15;
            textview_elenco.setVisibility(View.VISIBLE);
            for (int i = 0; i < tamanho; i++) {
                PersonCast personCast = movieDb.getCredits().getCast().get(i);
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
                    Picasso.with(getActivity())
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
                PersonCrew crew = movieDb.getCredits().getCrew().get(i);
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
                    Picasso.with(getActivity())
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
                linearLayout.addView(layoutScroll);
            }

        }

    }

    private void setSimilares() {
        if (similarMovies.getResults().size() > 0) {
            int tamanho = similarMovies.getResults().size() < 10 ? similarMovies.getResults().size() : 10;
            getView().findViewById(R.id.textview_similares).setVisibility(View.VISIBLE);
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
                    Picasso.with(getActivity())
                            .load(UtilsFilme.getBaseUrlImagem(1) + movie.getPosterPath())
                            .placeholder(getResources().getDrawable(R.drawable.poster_empty))
                            .into(imgPagerSimilares, new Callback() {
                                @Override
                                public void onSuccess() {
                                    loadPalette();
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

    private void loadPalette() {
        BitmapDrawable drawable = (BitmapDrawable) imgPagerSimilares.getDrawable();
        if (drawable != null) {
            Bitmap bitmap = drawable.getBitmap();
            Palette.Builder builder = new Palette.Builder(bitmap);
            Palette.Swatch swatch = builder.generate().getVibrantSwatch();
            if (swatch != null) {
               color_top = swatch.getRgb();
            }
        }
    }



    private void setLancamento() {
        if (movieDb.getReleaseDate() != null) {
            lancamento.setText(movieDb.getReleaseDate());
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
            Log.d("FilmeBottonFragment", "SetTreiler: -> " + movieDb.getVideos().size());
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

                    }
                });
                YouTubeThumbnailView thumbnailView = (YouTubeThumbnailView) linearteste.findViewById(R.id.youtube_view_thumbnail);
                thumbnailView.initialize(Config.YOUTUBE_API_KEY, OnInitializedListener(youtube_key));
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
        if (movieDb.getHomepage() != null) {
            icon_site.setImageResource(R.drawable.site_on);
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


    public class TMDVAsync extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            TmdbMovies movies = FilmeService.getTmdbMovies();
            Log.d("FilmeBottonFragment", "doInBackground: -> " + id_filme);
            movieDb = movies.getMovie(id_filme, getResources().getString(R.string.IDIOMAS)
                    , credits, releases, videos, reviews, similar);

            movieDb.getVideos().addAll(movies.getMovie(id_filme, "en", videos).getVideos());
            movieDb.getReviews().addAll(movies.getMovie(id_filme, "en", reviews).getReviews());
            Log.d("FilmeBottonFragment", "doInBackground: VIDEOS " + movieDb.getVideos().size());
            similarMovies = movies.getSimilarMovies(id_filme, getResources().getString(R.string.IDIOMAS), 1);
            Log.d("FilmeBottonFragment", "doInBackground: Similares " + similarMovies.getResults().size());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

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


        }
    }


}
