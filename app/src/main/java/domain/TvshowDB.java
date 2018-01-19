package domain;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * Created by icaro on 20/11/16.
 */
@Keep
public class TvshowDB implements Serializable {


    private String poster;

    private String title;

    private int nota;

    private domain.tvshow.ExternalIds externalIds;

    private int id;


    public void setTitle(String title) {
        this.title = title;
    }

    public int getNota() {
        return nota;
    }


    public String getTitle() {
        return title;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public domain.tvshow.ExternalIds getExternalIds() {
        return externalIds;
    }

    public void setExternalIds(domain.tvshow.ExternalIds externalIds) {
        this.externalIds = externalIds;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
