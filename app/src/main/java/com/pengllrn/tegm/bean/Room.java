package com.pengllrn.tegm.bean;

/**
 * @author Administrator
 * @version $Rev$
 * @des ${UTODO}
 * @updateAuthor ${Author}$
 * @updateDate2017/9/27.
 */

public class Room {
    private String buildingname;
    private String roomname;
    private String buildingid;
    private int roomid;

    public Room(String buildingname, String roomname,int roomid) {
        this.buildingname = buildingname;
        this.roomname = roomname;
        this.roomid = roomid;
    }

    public String getBuildingname() {
        return buildingname;
    }

    public String getRoomname() {
        return roomname;
    }

    public int getRoomid() {
        return roomid;
    }

    public String getBuildingid() {
        return buildingid;
    }
}
