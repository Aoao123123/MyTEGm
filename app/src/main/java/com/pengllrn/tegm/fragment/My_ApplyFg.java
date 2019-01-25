package com.pengllrn.tegm.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.pengllrn.tegm.Aoao.AddingUrl;
import com.pengllrn.tegm.Aoao.DamageApplicationLists;
import com.pengllrn.tegm.Aoao.DamageApplicationListsAdapter;
import com.pengllrn.tegm.Aoao.DevicesUsageListsAdapter;
import com.pengllrn.tegm.R;
import com.pengllrn.tegm.activity.DeviceAlarmAct;
import com.pengllrn.tegm.activity.LoginActivity;
import com.pengllrn.tegm.activity.LookDevice;
import com.pengllrn.tegm.activity.MainActivity;
import com.pengllrn.tegm.adapter.MyApplyListAdapter;
import com.pengllrn.tegm.bean.ApplyCenterBean;
import com.pengllrn.tegm.activity.ApplyCenter;
import com.pengllrn.tegm.bean.School;
import com.pengllrn.tegm.constant.Constant;
import com.pengllrn.tegm.gson.ParseJson;
import com.pengllrn.tegm.internet.OkHttp;
import com.pengllrn.tegm.utils.ActivityCollector;
import com.pengllrn.tegm.utils.SharedHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.RequestBody;

/**
 * Created by pengl on 2018/1/21.
 */

public class My_ApplyFg extends Fragment {

    private String applyUrl = Constant.URL_DAMAGE_APPLICATION;
    private ApplyCenter applyCenter;
    private ListView my_apply_list;
    private ParseJson mParseJson = new ParseJson();
    private List<DamageApplicationLists> listDamageApplication = new ArrayList<DamageApplicationLists>();
    private List<School> listSchool = new ArrayList<School>();
    private SharedHelper sharedHelper = new SharedHelper(applyCenter);

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2020:
                    String responseData = (msg.obj).toString();
                    int statusValue = mParseJson.Json2DamageApplicationStatus(responseData).getStatus();
                    if (statusValue == -5) {
                        Toast.makeText(applyCenter,"已与服务器断开连接，请重新登录",Toast.LENGTH_SHORT).show();
                        ActivityCollector.finishAll();
                        sharedHelper.clear();
                        Intent intent = new Intent(applyCenter, LoginActivity.class);
                        startActivity(intent);
                    } else if (statusValue == 0) {
                        listDamageApplication = mParseJson.DamageApplicationListsPoint(responseData);
                        if (listDamageApplication != null) {
                            my_apply_list.setAdapter(new DamageApplicationListsAdapter(applyCenter, listDamageApplication, R.layout.item_apply_list));
                            applyCenter.save(responseData, "applicationList");
                        }
                    }
                    break;
                case 0x22:
                    Toast.makeText(applyCenter, "服务器响应超时", Toast.LENGTH_SHORT).show();
                    break;
                case 0x30:
                    Toast.makeText(applyCenter, "网络连接失败", Toast.LENGTH_SHORT).show();
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
            applyCenter = (ApplyCenter) context;
        } else
            applyCenter = (ApplyCenter) getActivity();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_apply, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        my_apply_list = (ListView) view.findViewById(R.id.my_apply_list);
//        SharedHelper sharedHelper = new SharedHelper(applyCenter);
//        String userid = sharedHelper.readbykey("userid");
//        if(!userid.equals("")){
//            OkHttp okHttp = new OkHttp(applyCenter, mHandler);
//            RequestBody requestBody = new FormBody.Builder().add("userid", userid).add("type","1").build();
//            okHttp.postDataFromInternet(applyUrl, requestBody);
//        }
        String data = applyCenter.read("schoolList");
        if (data != null && !data.equals("")) {
            listSchool = mParseJson.SchoolPoint(data);
            HashMap<String, String> hashMap;
            String damageapplicationUrl;
            if (listSchool != null) {
                for (int i = 0; i < listSchool.size(); i++) {
                    int schoolid = listSchool.get(i).getId();
                    hashMap = AddingUrl.createHashMap1("schoolid", String.valueOf(schoolid));
                    OkHttp okHttp = new OkHttp(applyCenter, mHandler);
                    damageapplicationUrl = AddingUrl.getUrl(applyUrl, hashMap);
                    okHttp.getDataFromInternet(damageapplicationUrl);
                    System.out.println("devicesusageUrl is " + damageapplicationUrl);
                }
            }
        }
    }

//    public List<DamageApplicationLists> DamageApplicationListsPoint(String json) {
//        try {
//            JSONObject jsonObject = new JSONObject(json);
//            JSONArray damageapplicationArray = jsonObject.getJSONArray("application_list");
//            for (int i = 0; i < damageapplicationArray.length(); i++) {
//                JSONObject jObject = damageapplicationArray.getJSONObject(i);
//                int deal_status = jObject.getInt("deal_status");
//                String name = jObject.getString("name");
//                int applicationid = jObject.getInt("applicationid");
//                String datetime = jObject.getString("datetime");
//                String deviceid = jObject.getString("deviceid");
//                String type = jObject.getString("type");
//                String devicenum = jObject.getString("devicenum");
//                listDamageApplication.add(new DamageApplicationLists(deal_status, name, applicationid, datetime, deviceid, type, devicenum));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return listDamageApplication;
//    }
}

