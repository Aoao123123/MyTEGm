package com.pengllrn.tegm.Aoao;

import java.util.List;

/**
 * Created by pengllrn on 2019/1/22.
 */

public class DevicesInRoomStatus {

    private int status;
    private String roomname;
    private String buildingname;
    private List<DevicesInRoom> device_list;

    public int getStatus() {
        return status;
    }

    public String getRoomname() {
        return roomname;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public List<DevicesInRoom> getDevice_list() {
        return device_list;
    }

}
