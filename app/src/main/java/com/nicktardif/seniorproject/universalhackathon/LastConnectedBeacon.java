package com.nicktardif.seniorproject.universalhackathon;

import java.util.Comparator;

/**
 * Created by tick on 3/28/15.
 */
public class LastConnectedBeacon {
    public String UUID;
    public int major;
    public int minor;
    public long lastConnectedTime;

    public LastConnectedBeacon(String UUID, int major, int minor, long lastConnectedTime) {
        this.UUID = UUID;
        this.major = major;
        this.minor = minor;
        this.lastConnectedTime = lastConnectedTime;
    }

    public String createBeaconID() {
        return UUID + Integer.toString(major) + Integer.toString(minor);
    }
}
