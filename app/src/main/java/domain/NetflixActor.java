package domain;

import com.google.gson.annotations.SerializedName;

public class NetflixActor{

    /**
     * unit : 631
     * show_id : 445386
     * show_title : Dirty Dancing
     * release_year : 1987
     * rating : 3.8
     * category : Dramas
     * show_cast : Patrick Swayze, Jennifer Grey, Jerry Orbach, Cynthia Rhodes, Jack Weston, Jane Brucker, Kelly Bishop, Lonny Price, Max Cantor
     * director : Emile Ardolino
     * summary : Expecting the usual tedium of summer in the mountains with her family, 17-year-old Frances is surprised to find herself stepping into the shoes of a professional dancer -- and falling in love with the resort's free-spirited dance instructor.
     * poster : http://netflixroulette.net/api/posters/445386.jpg
     * mediatype : 0
     * runtime : 100 min
     */

    @SerializedName("unit")
    private int unit;
    @SerializedName("show_id")
    private int showId;
    @SerializedName("show_title")
    public String showTitle;
    @SerializedName("release_year")
    private String releaseYear;
    @SerializedName("rating")
    private String rating;
    @SerializedName("category")
    private String category;
    @SerializedName("show_cast")
    private String showCast;
    @SerializedName("director")
    public String director;
    @SerializedName("summary")
    private String summary;
    @SerializedName("poster")
    private String poster;
    @SerializedName("mediatype")
    public int mediatype;
    @SerializedName("runtime")
    private String runtime;

    public String getShowTitle() {
        return showTitle;
    }

    public void setShowTitle(String showTitle) {
        this.showTitle = showTitle;
    }

    public int getUnit() {
        return unit;
    }

    public void setUnit(int unit) {
        this.unit = unit;
    }

    public int getShowId() {
        return showId;
    }

    public void setShowId(int showId) {
        this.showId = showId;
    }

    public String getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(String releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getShowCast() {
        return showCast;
    }

    public void setShowCast(String showCast) {
        this.showCast = showCast;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public int getMediatype() {
        return mediatype;
    }

    public void setMediatype(int mediatype) {
        this.mediatype = mediatype;
    }

    public String getRuntime() {
        return runtime;
    }

    public void setRuntime(String runtime) {
        this.runtime = runtime;
    }

}
