package domian;

import java.io.Serializable;
import java.util.List;

/**
 * Created by icaro on 03/11/16.
 */

public class UserSeasons implements Serializable {

    private List<UserEp> userEps;
    private int id;
    private int seasonNumber;

    public boolean isVisto() {
        return visto;
    }

    public void setVisto(boolean visto) {
        this.visto = visto;
    }

    private boolean visto;

    public UserSeasons() {
    }

    public List<UserEp> getUserEps() {
        return userEps;
    }

    public void setUserEps(List<UserEp> userEps) {
        this.userEps = userEps;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }


}
