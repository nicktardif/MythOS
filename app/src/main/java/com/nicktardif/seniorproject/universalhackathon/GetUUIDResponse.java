package com.nicktardif.seniorproject.universalhackathon;

/**
 * Created by tick on 3/28/15.
 */
public class GetUUIDResponse {
    public boolean success;
    public String uuid;
    public int length;

    public GetUUIDResponse(boolean success, String uuid, int length) {
        this.success = success;
        this.uuid = uuid;
        this.length = length;
    }

    @Override
    public String toString() {
        return "GetUUIDResponse{" +
                "success=" + success +
                ", uuid='" + uuid + '\'' +
                ", length=" + length +
                '}';
    }
}
