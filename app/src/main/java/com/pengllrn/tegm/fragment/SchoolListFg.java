package com.pengllrn.tegm.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.pengllrn.tegm.Aoao.DevicesUsageLists;
import com.pengllrn.tegm.Aoao.DevicesUsageListsAdapter;
import com.pengllrn.tegm.R;
import com.pengllrn.tegm.activity.LoginActivity;
import com.pengllrn.tegm.activity.LookDevice;
import com.pengllrn.tegm.activity.MainActivity;
import com.pengllrn.tegm.adapter.SchoolListAdapter;
import com.pengllrn.tegm.bean.School;
import com.pengllrn.tegm.constant.Constant;
import com.pengllrn.tegm.gson.ParseJson;
import com.pengllrn.tegm.internet.OkHttp;
import com.pengllrn.tegm.utils.ActivityCollector;
import com.pengllrn.tegm.utils.SharedHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * @author Administrator
 * @version $Rev$
 * @des ${UTODO}
 * @updateAuthor ${Author}$
 * @updateDate2017/11/23.
 */

public class SchoolListFg extends Fragment {
    private String applyUrl = Constant.URL_DEVICE_USAGE;
    private LookDevice lookDeviceActivity;
    private ListView list_gis;

    private ParseJson mParseJson = new ParseJson();
    private List<School> listSchool;
    private boolean flag = false;
    private List<DevicesUsageLists> listDevicesUsage = new ArrayList<DevicesUsageLists>();

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
                    SharedHelper sharedHelper = new SharedHelper(lookDeviceActivity);
                    String responseData = (msg.obj).toString();
                    int statusValue = mParseJson.Json2DeviceUsageStatus(responseData).getStatus();
                    if (statusValue == -5) {
                        Toast.makeText(lookDeviceActivity,"已与服务器断开连接，请重新登录",Toast.LENGTH_SHORT).show();
                        ActivityCollector.finishAll();
                        sharedHelper.clear();
                        Intent intent = new Intent(lookDeviceActivity, LoginActivity.class);
                        startActivity(intent);
                    } else if (statusValue == 0) {
                        listDevicesUsage = mParseJson.DevicesUsagePoint(responseData);
                        if (listDevicesUsage != null) {
                            list_gis.setAdapter(new DevicesUsageListsAdapter(lookDeviceActivity,listDevicesUsage,R.layout.base_list_item));
                            setListListener(listDevicesUsage);
                        }
                    }
                    break;
                case 0x22:
                    Toast.makeText(lookDeviceActivity, "服务器响应超时", Toast.LENGTH_SHORT).show();
                    break;
                case 0x30:
                    Toast.makeText(lookDeviceActivity, "网络连接失败", Toast.LENGTH_SHORT).show();
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
            lookDeviceActivity = (LookDevice) context;
        }else {
            lookDeviceActivity = (LookDevice) getActivity();
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);
        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setTitle();
        list_gis = (ListView) view.findViewById(R.id.list_gis);
        String data = lookDeviceActivity.read("schoolList");
        if (data != null && !data.equals("")) {
            listSchool = mParseJson.SchoolPoint(data);
            HashMap<String,String> hashMap;
            String devicesusageUrl;
//            if(listSchool!=null) {
//                list_gis.setAdapter(new SchoolListAdapter(lookDeviceActivity,
//                        listSchool, R.layout.base_list_item));
//                setListListener(listSchool);
//            }
//        }else {
//            OkHttp okHttp = new OkHttp(lookDeviceActivity, mHandler);
//            RequestBody requestBody = new FormBody.Builder().add("type", "1").build();
//            okHttp.postDataFromInternet(applyUrl, requestBody);
//        }
           listDevicesUsage.clear();
           if (listSchool != null) {
               for (int i = 0;i < listSchool.size();i++) {
                   int schoolid = listSchool.get(i).getId();
                   hashMap = AddingUrl.createHashMap1("schoolid",String.valueOf(schoolid));
                   devicesusageUrl = AddingUrl.getUrl(applyUrl,hashMap);
                   OkHttp okHttp = new OkHttp(lookDeviceActivity,mHandler);
                   okHttp.getDataFromInternet(devicesusageUrl);
                   System.out.println("devicesusageUrl is " + devicesusageUrl);
               }
           }
        }
    }

//    public void setListListener(final List<School> listSchool){
//        list_gis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                String schoolid=listSchool.get(position).getId();
//                Bundle bundle = new Bundle();
//                bundle.putString("schoolid",schoolid);
//                BuildingListFg buildingListFg=new BuildingListFg();
//                buildingListFg.setArguments(bundle);
//                FragmentManager fragmentManager = lookDeviceActivity.getSupportFragmentManager();
//                FragmentTransaction transaction = fragmentManager.beginTransaction();
//                transaction.add(R.id.fragment_list, buildingListFg);
//                transaction.addToBackStack(null);
//                transaction.commit();
//            }
//        });
//    }
    public void setListListener(final List<DevicesUsageLists> l) {
        list_gis.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView,View view,int position,long id) {
                String schoolid = l.get(position).getSchoolid();
                Bundle bundle = new Bundle();
                bundle.putString("schoolid",schoolid);
                BuildingListFg buildingListFg = new BuildingListFg();
                buildingListFg.setArguments(bundle);
                FragmentManager fragmentManager = lookDeviceActivity.getSupportFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_list, buildingListFg);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });
    }
    public void setTitle() {
        textView1 = (TextView) lookDeviceActivity.findViewById(R.id.text1);
        textView2 = (TextView) lookDeviceActivity.findViewById(R.id.text2);
        textView3 = (TextView) lookDeviceActivity.findViewById(R.id.text3);
        textView4 = (TextView) lookDeviceActivity.findViewById(R.id.text4);
        textView1.setText("名称");
        textView2.setText("设备总数");
        textView3.setText("正在使用");
        textView4.setText("使用率");
    }
}
