package com.nicktardif.seniorproject.universalhackathon;

import java.util.List;

/**
 * Created by tick on 3/28/15.
 */
public class SeenBeaconResponse {
    public boolean success;
    public String fresh;
    public String old;

    public SeenBeaconResponse(boolean success, String fresh, String old) {
        this.success = success;
        this.fresh = fresh;
        this.old = old;
    }
}
