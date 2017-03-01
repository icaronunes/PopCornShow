package domain;

import android.support.annotation.Keep;

import java.io.Serializable;

/**
 * Created by icaro on 26/09/16.
 */

@Keep
public class TopMain implements Serializable {

    int id;
    String nome;
    String imagem;
    String mediaType;

    public String getImagem() {
        return imagem;
    }

    public void setImagem(String imagem) {
        this.imagem = imagem;
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

    public String getMediaType() {
        return mediaType;
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType;
    }
}
