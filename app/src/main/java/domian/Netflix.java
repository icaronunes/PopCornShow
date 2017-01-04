package domian;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by icaro on 03/01/17.
 */

public class Netflix implements Serializable {

    /**
     * unit : 7474
     * show_id : 70153391
     * show_title : The Boondocks
     * release_year : 2005
     * rating : 4.0
     * category : TV Shows
     * show_cast : Regina King, John Witherspoon, Cedric Yarbrough, Gary Anthony Williams, Jill Talley, Gabby Soleil
     * director :
     * summary : Based on the comic strip by Aaron McGruder, this satirical animated series follows the socially conscious misadventures of Huey Freeman, a preternaturally smart 10-year-old who relocates from inner-city Chicago to the suburbs.
     * poster : http://netflixroulette.net/api/posters/70153391.jpg
     * mediatype : 1
     * runtime : 20 min
     */

    @SerializedName("unit")
    public int unit;
    @SerializedName("show_id")
    public int showId;
    @SerializedName("show_title")
    public String showTitle;
    @SerializedName("release_year")
    public String releaseYear;
    @SerializedName("rating")
    public String rating;
    @SerializedName("category")
    public String category;
    @SerializedName("show_cast")
    public String showCast;
    @SerializedName("director")
    public String director;
    @SerializedName("summary")
    public String summary;
    @SerializedName("poster")
    public String poster;
    @SerializedName("mediatype")
    public int mediatype;
    @SerializedName("runtime")
    public String runtime;
}
