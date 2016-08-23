package fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import activity.Site;
import adapter.PersonCrewsAdapter;
import adapter.PersonImagemAdapter;
import adapter.PersonMovieAdapter;
import br.com.icaro.filme.R;
import domian.FilmeService;
import info.movito.themoviedbapi.model.Artwork;
import info.movito.themoviedbapi.model.people.PersonCredits;
import info.movito.themoviedbapi.model.people.PersonPeople;
import utils.Constantes;
import utils.UtilsFilme;

import static domian.FilmeService.getTmdbPerson;

/**
 * Created by icaro on 18/08/16.
 */
public class PersonFragment extends Fragment {

    TextView nome_person, birthday, dead, homepage, biografia, aka, conhecido, place_of_birth, sem_filmes, sem_fotos, sem_crews;
    ImageView imageView, imageButtonWiki;
    RecyclerView recyclerViewMovie, recyclerViewImagem, RecyclerViewCrews;

    int tipo, id_person;
    ProgressBar progressBar;
    PersonPeople personPeople;
    PersonCredits personCredits;
    List<Artwork> artworks;
    String TAG = "PersonFragment";

    public static PersonFragment newInstance(int aba, int id_person) {
        Log.d("PersonFragment", "newInstance");
        Bundle args = new Bundle();
        args.putInt(Constantes.ABA, aba);
        args.putInt(Constantes.PERSON_ID, id_person);
        PersonFragment f = new PersonFragment();
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        if (getArguments() != null) {
            tipo = getArguments().getInt(Constantes.ABA);
            id_person = getArguments().getInt(Constantes.PERSON_ID);
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        new PersonAsync().execute();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        switch (tipo) {

            case R.string.movie: {
                return getViewPersonMovie(inflater, container);
            }
            case R.string.crews: {
                return getViewPersonCrews(inflater, container);
            }
            case R.string.person: {
                return getViewPerson(inflater, container);
            }
            case R.string.imagem_person: {
                return getViewPersonImage(inflater, container);
            }
        }
        return null;
    }

    private View getViewPersonImage(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.activity_person_imagem, container, false);
        recyclerViewImagem = (RecyclerView) view.findViewById(R.id.recycleView_person_imagem);
        sem_fotos = (TextView) view.findViewById(R.id.sem_fotos);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        recyclerViewImagem.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewImagem.setHasFixedSize(true);
        recyclerViewImagem.setHasFixedSize(true);

        return view;
    }

    private View getViewPersonCrews(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.activity_person_crews, container, false);
        RecyclerViewCrews = (RecyclerView) view.findViewById(R.id.recycleView_person_crews);
        sem_crews = (TextView) view.findViewById(R.id.sem_crews);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        RecyclerViewCrews.setLayoutManager(new GridLayoutManager(getContext(), 2));
        RecyclerViewCrews.setHasFixedSize(true);
        RecyclerViewCrews.setHasFixedSize(true);

        return view;
    }

    private View getViewPerson(LayoutInflater inflater, ViewGroup container) {
        Log.d(TAG, "getViewPerson");
        View view = inflater.inflate(R.layout.activity_person_perfil, container, false);
        nome_person = (TextView) view.findViewById(R.id.nome_person);
        birthday = (TextView) view.findViewById(R.id.birthday);
        dead = (TextView) view.findViewById(R.id.dead);
        homepage = (TextView) view.findViewById(R.id.person_homepage);
        biografia = (TextView) view.findViewById(R.id.person_biogragia);
        imageView = (ImageView) view.findViewById(R.id.image_person);
        aka = (TextView) view.findViewById(R.id.aka);
        imageButtonWiki = (ImageView) view.findViewById(R.id.person_wiki);
        conhecido = (TextView) view.findViewById(R.id.conhecido);
        place_of_birth = (TextView) view.findViewById(R.id.place_of_birth);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);

        return view;
    }

    private View getViewPersonMovie(LayoutInflater inflater, ViewGroup container) {

        View view = inflater.inflate(R.layout.activity_person_movies, container, false);
        recyclerViewMovie = (RecyclerView) view.findViewById(R.id.recycleView_person_movies);
        sem_filmes = (TextView) view.findViewById(R.id.sem_filmes);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        recyclerViewMovie.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerViewMovie.setHasFixedSize(true);

        return view;
    }

    private void setPersonInformation(final PersonPeople information) {
        if (!information.getName().isEmpty() && information.getName().length() > 1) {
            nome_person.setText(information.getName());
            nome_person.setVisibility(View.VISIBLE);
        }
        if (!information.getBirthday().isEmpty() && information.getBirthday().length() > 1) {
            birthday.setText(information.getBirthday());
            birthday.setVisibility(View.VISIBLE);
        }

        if (!information.getDeathday().isEmpty() && information.getDeathday().length() > 1) {
            dead.setText(" - " + information.getDeathday());
            dead.setVisibility(View.VISIBLE);
        }

        if (!information.getHomepage().isEmpty() && information.getHomepage().length() > 5) {
            String site = information.getHomepage();
            site = site.replace("http://", "");

            homepage.setText(site);
            homepage.setVisibility(View.VISIBLE);

            homepage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getContext(), Site.class);
                    intent.putExtra(Constantes.SITE, information.getHomepage());
                    startActivity(intent);
                }
            });

        } else {
            homepage.setVisibility(View.GONE);
        }

        if (!information.getBirthplace().isEmpty()) {
            place_of_birth.setText(information.getBirthplace());
            place_of_birth.setVisibility(View.VISIBLE);
        }

        if (!information.getAka().isEmpty()) {

            for (String nome : information.getAka()) {
                if (nome.length() > 2) {
                    aka.setText(nome + " ");
                    conhecido.setVisibility(View.VISIBLE);
                    aka.setVisibility(View.VISIBLE);
                }
            }
        }
        if (!information.getBiography().isEmpty()) {
            biografia.setText(information.getBiography());
        } else {
            biografia.setText(R.string.sem_biografia);
        }

        if (!information.getName().isEmpty()) {
            imageButtonWiki.setVisibility(View.VISIBLE);

            imageButtonWiki.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String BASEWIKI = "https://pt.wikipedia.org/wiki/";
                    String site;
                    Intent intent = new Intent(getContext(), Site.class);
                    String nome = information.getName();
                    site = BASEWIKI.concat(nome.replace(" ", "_"));

                    intent.putExtra(Constantes.SITE, site);
                    startActivity(intent);
                }
            });
        }

        Picasso.with(getContext()).load(UtilsFilme.getBaseUrlImagem(2) + information.getProfilePath())
                .placeholder(R.drawable.person)
                .into(imageView);
        imageView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void setPersonMovies(PersonCredits personCredits) {
        if (personCredits.getCast() == null || personCredits.getCast().isEmpty()) {
            sem_filmes.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            recyclerViewMovie.setAdapter(new PersonMovieAdapter(getContext(), personCredits));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setPersonCrews(PersonCredits personCredits) {
        personCredits = removerDuplicados(personCredits);
        if (personCredits.getCrew() == null || personCredits.getCrew().isEmpty()) {
            sem_crews.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            RecyclerViewCrews.setAdapter(new PersonCrewsAdapter(getContext(), removerDuplicados(personCredits)));
            progressBar.setVisibility(View.GONE);
        }
    }

    private void setPersonImagem(List<Artwork> artworks) {
        if (artworks.isEmpty() || artworks == null) {
            sem_fotos.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.GONE);
        } else {
            recyclerViewImagem.setAdapter(new PersonImagemAdapter(getContext(), artworks, id_person, personPeople.getName()));
            progressBar.setVisibility(View.GONE);
        }
    }

    /*Refazer metodo. Deve haver jeito melhor*/
    private PersonCredits removerDuplicados(PersonCredits credits) {
        PersonCredits temp = credits;
        for (int i = 0; i <= personCredits.getCrew().size(); i++) {
            for (int l = i + 1; l < personCredits.getCrew().size(); l++) {
                if (personCredits.getCrew().get(i).getId() == personCredits.getCrew().get(l).getId()) {
                    temp.getCrew().remove(l);
                }
            }
        }
        return temp;
    }

    public class PersonAsync extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.d("PersonAsync", "" + id_person);
            personPeople = getTmdbPerson()
                    .getPersonInfo(id_person, "&language=pt");
            artworks = FilmeService.getTmdbPerson().getPersonImages(id_person);
            personCredits = FilmeService.getTmdbPerson().getPersonCredits(id_person);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (tipo == R.string.person) {
                setPersonInformation(personPeople);
            }
            if (tipo == R.string.movie) {
                setPersonMovies(personCredits);
            }
            if (tipo == R.string.crews) {
                setPersonCrews(personCredits);
            }
            if (tipo == R.string.imagem_person) {
                setPersonImagem(artworks);
            }
        }
    }
}


