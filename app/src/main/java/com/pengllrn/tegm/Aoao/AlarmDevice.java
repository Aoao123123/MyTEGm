package com.pengllrn.tegm.Aoao;

/**
 * Created by Aoao Bot on 2018/12/18.
 */

public class AlarmDevice {

    private String schoolid;
    private boolean alarmstart;
    private int ordernum;
    private String deviceid;
    private String roomid;
    private String alarmtype;
    private String devicenum;

    public AlarmDevice(String schoolid,boolean alarmstart,int ordernum,String deviceid,String roomid,String alarmtype,String devicenum) {
        this.schoolid = schoolid;
        this.alarmstart = alarmstart;
        this.ordernum = ordernum;
        this.deviceid = deviceid;
        this.roomid = roomid;
        this. alarmtype = alarmtype;
        this.devicenum = devicenum;
    }

    public String getSchoolid() {
        return schoolid;
    }

    public boolean getAlarmstart() {
        return alarmstart;
    }

    public int getOrdernum() {
        return ordernum;
    }

    public String getDeviceid() {
        return deviceid;
    }

    public String getRoomid() {
        return roomid;
    }

    public String getAlarmtype() {
        return alarmtype;
    }

    public String getDevicenum() {
        return devicenum;
    }
}
