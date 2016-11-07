package domian;

import java.io.Serializable;
import java.util.List;

import info.movito.themoviedbapi.model.ExternalIds;

/**
 * Created by icaro on 02/11/16.
 */

public class UserTvshow implements Serializable {


    public UserTvshow() {
    }

    private String nome;

    private int id;

    private int numberOfEpisodes;

    private int numberOfSeasons;


    private List<UserSeasons> seasons;

    private ExternalIds externalIds;


    public int getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(int numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }


    public ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public List<UserSeasons> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<UserSeasons> seasons) {
        this.seasons = seasons;
    }
}
