package domian;

import com.google.gson.annotations.SerializedName;

import java.util.Date;
import java.util.List;

/**
 * Created by icaro on 03/10/16.
 */
public class Lista  {

    @SerializedName("items")
    public List<ItemsLista> items;
    @SerializedName("created_by")
    private String createdBy;
    @SerializedName("description")
    private String description;
    @SerializedName("favorite_count")
    private int favoriteCount;
    @SerializedName("id")
    private String id;
    @SerializedName("item_count")
    private int itemCount;
    @SerializedName("iso_639_1")
    private String iso6391;
    @SerializedName("name")
    private String name;
    @SerializedName("poster_path")
    private Object posterPath;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getFavoriteCount() {
        return favoriteCount;
    }

    public void setFavoriteCount(int favoriteCount) {
        this.favoriteCount = favoriteCount;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getItemCount() {
        return itemCount;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public String getIso6391() {
        return iso6391;
    }

    public void setIso6391(String iso6391) {
        this.iso6391 = iso6391;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(Object posterPath) {
        this.posterPath = posterPath;
    }

    public List<ItemsLista> getItems() {
        return items;
    }

    public void setItems(List<ItemsLista> items) {
        this.items = items;
    }

}
