package momomo00.receivinglocationupdates;

import android.location.Location;

import java.util.Date;

/**
 * Created by songo_000 on 2016/11/07.
 */

public interface MyLocationUpdateListener {
    void onLocationUpdate(Location location, Date date);
}
