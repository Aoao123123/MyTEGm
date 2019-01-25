package com.pengllrn.tegm.Aoao;

import com.pengllrn.tegm.bean.Room;

import java.util.List;

/**
 * Created by pengllrn on 2019/1/22.
 */

public class RoomListsStatus {

    private int status;
    private String buildingname;
    private List<Room> room_list;

    public int getStatus() {
        return status;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public List<Room> getRoom_list() {
        return room_list;
    }
}
