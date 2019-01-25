package com.pengllrn.tegm.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.pengllrn.tegm.Aoao.AddingUrl;
import com.pengllrn.tegm.Aoao.DeviceInRoomFg;
import com.pengllrn.tegm.Aoao.RoomListsAdapter;
import com.pengllrn.tegm.R;
import com.pengllrn.tegm.activity.DeviceInRoom;
import com.pengllrn.tegm.activity.LoginActivity;
import com.pengllrn.tegm.activity.LookDevice;
import com.pengllrn.tegm.adapter.RoomListAdapter;
import com.pengllrn.tegm.bean.Room;
import com.pengllrn.tegm.bean.RoomList;
import com.pengllrn.tegm.constant.Constant;
import com.pengllrn.tegm.gson.ParseJson;
import com.pengllrn.tegm.internet.OkHttp;
import com.pengllrn.tegm.utils.ActivityCollector;
import com.pengllrn.tegm.utils.SharedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * @author Administrator
 * @version $Rev$
 * @des ${UTODO}
 * @updateAuthor ${Author}$
 * @updateDate2017/11/23.
 */

public class RoomListFg extends Fragment {
    private String applyUrl = Constant.URL_ROOM;
    private LookDevice loolDeviceActivity;
    private ListView list_gis;

    private ParseJson mParseJson = new ParseJson();
    private List<Room> listRoom;
    private List<Room> actuallistRoom;

    private String schoolid;
    private String buildingname;
    private int buildingid;

    private TextView textView1;
    private TextView textView2;
    private TextView textView3;
    private TextView textView4;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2020:
                    System.out.println("Get roomlists");
                    String responseData = (msg.obj).toString();
                    SharedHelper sharedHelper = new SharedHelper(loolDeviceActivity);
                    int statusValue = mParseJson.Json2RoomListsStatus(responseData).getStatus();
                    if (statusValue == -5) {
                        Toast.makeText(loolDeviceActivity,"已与服务器断开连接，请重新登录",Toast.LENGTH_SHORT).show();
                        ActivityCollector.finishAll();
                        sharedHelper.clear();
                        Intent intent = new Intent(loolDeviceActivity, LoginActivity.class);
                        startActivity(intent);
                    } else if (statusValue == 0) {
                        listRoom = mParseJson.Json2RoomListsStatus(responseData).getRoom_list();
                        list_gis.setAdapter(new RoomListsAdapter(loolDeviceActivity,
                                listRoom, R.layout.base_list_item));
                        list_gis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                                int roomid = listRoom.get(position).getRoomid();
                                Bundle bundle = new Bundle();
                                bundle.putInt("roomid", roomid);
                                DeviceInRoomFg deviceInRoomFg = new DeviceInRoomFg();
                                deviceInRoomFg.setArguments(bundle);
                                FragmentManager fragmentManager = loolDeviceActivity.getSupportFragmentManager();
                                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                fragmentTransaction.replace(R.id.fragment_list, deviceInRoomFg);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                        });
                    }
                    break;
                case 0x22:
                    Toast.makeText(loolDeviceActivity, "服务器响应超时", Toast.LENGTH_SHORT).show();
                    break;
                case 0x30:
                    Toast.makeText(loolDeviceActivity, "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context != null) {
            loolDeviceActivity = (LookDevice) context;
        }
        loolDeviceActivity = (LookDevice) getActivity();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        buildingid = getArguments().getInt("buildingid");
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle();
        list_gis = (ListView) view.findViewById(R.id.list_gis);
        String getroomlistUrl;
        HashMap<String,String> hashMap;
        OkHttp okHttp = new OkHttp(loolDeviceActivity, mHandler);
        hashMap = AddingUrl.createHashMap1("buildingid",String.valueOf(buildingid));
        getroomlistUrl = AddingUrl.getUrl(applyUrl,hashMap);
        okHttp.getDataFromInternet(getroomlistUrl);

//        RequestBody requestBody = new FormBody.Builder()
//                .add("type", "3")
//                .add("schoolid",schoolid)
//                .add("buildingname",buildingname)
//                .build();
//        okHttp.postDataFromInternet(applyUrl, requestBody);
    }

    public void setTitle() {
        textView1 = (TextView) loolDeviceActivity.findViewById(R.id.text1);
        textView2 = (TextView) loolDeviceActivity.findViewById(R.id.text2);
        textView3 = (TextView) loolDeviceActivity.findViewById(R.id.text3);
        textView4 = (TextView) loolDeviceActivity.findViewById(R.id.text4);
        textView1.setText("教学楼");
        textView2.setText("房间");
        textView3.setText("房间编号");
        textView4.setText("");
    }

}
