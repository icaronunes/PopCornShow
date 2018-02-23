package domain;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * Created by icaro on 20/11/16.
 */
@Keep
public class FilmeDB implements Serializable {

    private String poster;
    private String idImdb;
    private String title;
    private int id;

    private float nota;


    public void setTitle(String title) {
        this.title = title;
    }
    public float getNota() {
        return nota;
    }


    public String getTitle() {
        return title;
    }

    public void setNota(float nota) {
        this.nota = nota;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }


    public String getIdImdb() {
        return idImdb;
    }

    public void setIdImdb(String idImdb) {
        this.idImdb = idImdb;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
