package domain;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * Created by icaro on 03/11/16.
 */

@Keep
public class UserEp implements Serializable{

    private int id;
    private Integer seasonNumber;
    private int episodeNumber;
    private boolean assistido = false;
    private float nota;

    public UserEp() {

    }

    public int getId() {
        return id;
    }


    public void setId(int id) {
        this.id = id;
    }

    public Integer getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(Integer seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public boolean isAssistido() {
        return assistido;
    }

    public void setAssistido(boolean assistido) {
        this.assistido = assistido;
    }

    public float getNota() {
        return nota;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }
}
