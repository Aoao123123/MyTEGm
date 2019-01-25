package com.pengllrn.tegm.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.pengllrn.tegm.Aoao.AddingUrl;
import com.pengllrn.tegm.Aoao.DeviceProperty;
import com.pengllrn.tegm.R;
import com.pengllrn.tegm.adapter.MyPopuAdapter;
import com.pengllrn.tegm.bean.School;
import com.pengllrn.tegm.bean.Statistics;
import com.pengllrn.tegm.constant.Constant;
import com.pengllrn.tegm.gson.ParseJson;
import com.pengllrn.tegm.internet.OkHttp;
import com.pengllrn.tegm.utils.ActivityCollector;
import com.pengllrn.tegm.utils.FileCache;
import com.pengllrn.tegm.utils.SharedHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;
import okhttp3.FormBody;
import okhttp3.RequestBody;


public class StatisticsAct extends AppCompatActivity {

    private String applyUrl = Constant.URL_GET_SCHOOL_PROPERTY;

    private LinearLayout ll_school;
    private LinearLayout ll_statistics;
    private CheckBox cb_school;
    private CheckBox cb_statistics;
    private ColumnChartView columnchart;
    private TextView tv_info;

    private MyPopuAdapter myPopuAdapter;
    private ParseJson mParseJson = new ParseJson();
    private List<String> listSchool = new ArrayList<>();
    private List<String> listStatistics = new ArrayList<>();
    private List<School> list_School = new ArrayList<>();
    private SharedHelper sharedHelper = new SharedHelper(this);



    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            switch (msg.what) {
                case 0x2020:
                    String responseData = (msg.obj).toString();
                    System.out.println(responseData);
                    List<DeviceProperty> statisticses = new ArrayList<DeviceProperty>();
                    int statusValue = mParseJson.Json2SchoolProperty(responseData).getStatus();
                    if (statusValue == -5) {
                        Toast.makeText(getApplicationContext(),"已与服务器断开连接，请重新登录",Toast.LENGTH_SHORT).show();
                        ActivityCollector.finishAll();
                        sharedHelper.clear();
                        Intent intent = new Intent(StatisticsAct.this, LoginActivity.class);
                        startActivity(intent);
                    } else if (statusValue == -1) {
                        Toast.makeText(getApplicationContext(),"该学校还未报备设备财产统计",Toast.LENGTH_SHORT).show();
                        statisticses.clear();
                        setChartView(statisticses);
                    } else if (statusValue == 0) {
                        statisticses = mParseJson.Json2SchoolProperty(responseData).getProperty_info();
                        setChartView(statisticses);
                        tv_info.setText("统计信息如下：");
                    }
                    break;
                case 0x22:
                    Toast.makeText(getApplicationContext(), "服务器响应超时", Toast.LENGTH_SHORT).show();
                    break;
                case 0x30:
                    Toast.makeText(getApplicationContext(), "网络连接失败", Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.mipmap.back);
            actionBar.setTitle("");
        }

        ll_school = (LinearLayout) findViewById(R.id.ll_school_name);
        ll_statistics = (LinearLayout) findViewById(R.id.ll_statistics_name);
        cb_school = (CheckBox) findViewById(R.id.cb_school_name);
        cb_statistics = (CheckBox) findViewById(R.id.cb_statistics_name);
        columnchart = (ColumnChartView) findViewById(R.id.columnchart);
        tv_info = (TextView) findViewById(R.id.info);

        columnchart.setZoomEnabled(true);
        columnchart.setInteractive(false);
        initDatas();
    }

    @Override
    protected void onResume() {
        super.onResume();
        myPopuAdapter = new MyPopuAdapter(this);

        setCbListener();
        if (list_School.size() > 0) {
            cb_school.setText(listSchool.get(0));
            cb_statistics.setText(listStatistics.get(0));
            getData(listSchool.get(0), listStatistics.get(0));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_statistics,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    private void setCbListener() {
        //学校LinearLayout点击监听器
        ll_school.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_school.isChecked()) cb_school.setChecked(false);
                else cb_school.setChecked(true);
            }
        });
        //统计选项LinearLayout点击监听器
        ll_statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (cb_statistics.isChecked()) cb_statistics.setChecked(false);
                else cb_statistics.setChecked(true);
            }
        });
        //学校CheckBox的监听
        cb_school.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myPopuAdapter.filterTabToggle(isChecked, ll_school, listSchool, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        myPopuAdapter.hidePopListView();
                        cb_school.setText(listSchool.get(position));
                        getData(cb_school.getText().toString(), cb_statistics.getText().toString());
                    }
                }, cb_school, cb_statistics);
            }
        });

        //统计选项CheckBox的监听
        cb_statistics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myPopuAdapter.filterTabToggle(isChecked, ll_statistics, listStatistics, new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        myPopuAdapter.hidePopListView();
                        cb_statistics.setText(listStatistics.get(position));
                        getData(cb_school.getText().toString(), cb_statistics.getText().toString());
                    }
                }, cb_statistics, cb_school);
            }
        });

    }

    private String read(String filename) {
        FileCache fileCache = new FileCache(getApplication());
        return fileCache.readFromCacheDir(filename);
    }

    private void initDatas() {
        listStatistics.add("采购统计");
        listStatistics.add("库存统计");
        listStatistics.add("价值统计");
        listStatistics.add("报废统计");

        String data = read("schoolList");
        if (data != null && !data.equals("")) {
            list_School = mParseJson.SchoolPoint(data);
            if (list_School != null) {
                for (int i = 0; i < list_School.size(); i++) {
                    listSchool.add(list_School.get(i).getSchoolname());
                }
            }
        }
    }

    private void getData(String school, String statistics) {
        tv_info.setText("正在查询...");
        String type = "";
        int schoolid = 0;
        switch (statistics) {
            case "采购统计":
                type = "1";
                break;
            case "库存统计":
                type = "2";
                break;
            case "价值统计":
                type = "3";
                break;
            case "报废统计":
                type = "4";
                break;
            default:
                type = "0";
                break;

        }
        for (int i = 0; i < list_School.size(); i++) {
            if (school.equals(list_School.get(i).getSchoolname())) {
                schoolid = list_School.get(i).getId();
                break;
            }
        }
        if (!school.equals("") || type.equals("0")) {
            //访问网络
            OkHttp okHttp = new OkHttp(getApplicationContext(), mHandler);
            String getschoolpropertyurl;
            HashMap<String,String> hashMap;
            hashMap = AddingUrl.createHashMap1("schoolid",String.valueOf(schoolid));
            getschoolpropertyurl = AddingUrl.getUrl(applyUrl,hashMap);
            okHttp.getDataFromInternet(getschoolpropertyurl);
//            RequestBody requestBody = new FormBody.Builder().add("school", schoolid).add("type", type).build();
//            okHttp.postDataFromInternet(applyUrl, requestBody);
        }
    }

    private void setChartView(List<DeviceProperty> list) {
        //每个集合显示几条柱子
        int numSubcolumns = 1;
        //显示多少个集合
        int numColumns = list.size();
        //保存所有的柱子
        List<Column> columns = new ArrayList<Column>();
        //保存每个竹子的值
        List<SubcolumnValue> values;
        List<AxisValue> axisXValues = new ArrayList<AxisValue>();
        //对每个集合的柱子进行遍历
        for (int i = 0; i < numColumns; ++i) {

            values = new ArrayList<SubcolumnValue>();
            //循环所有柱子（list）
            for (int j = 0; j < numSubcolumns; ++j) {
                //创建一个柱子，然后设置值和颜色，并添加到list中
                values.add(new SubcolumnValue(list.get(i).getValue(), ChartUtils.pickColor()));
                //设置X轴的柱子所对应的属性名称
                axisXValues.add(new AxisValue(i).setLabel(list.get(i).getDevice_type()));
            }
            //将每个属性的拥有的柱子，添加到Column中
            Column column = new Column(values);
            //是否显示每个柱子的Lable
            column.setHasLabels(true);
            //设置每个柱子的Lable是否选中，为false，表示不用选中，一直显示在柱子上
            column.setHasLabelsOnlyForSelected(false);
            //将每个属性得列全部添加到List中
            columns.add(column);
        }
        //设置Columns添加到Data中
        ColumnChartData columnChartData = new ColumnChartData(columns);
        //设置X轴显示在底部，并且显示每个属性的Lable，字体颜色为黑色，X轴的名字为“学历”，每个柱子的Lable斜着显示，距离X轴的距离为8
        columnChartData.setAxisXBottom(new Axis(axisXValues).setHasLines(true).setTextColor(Color.BLACK).setHasTiltedLabels(true).setMaxLabelChars(8));
        //属性值含义同X轴
        columnChartData.setAxisYLeft(new Axis().setHasLines(true).setName("数量").setTextColor(Color.BLACK).setMaxLabelChars(5));
        //最后将所有值显示在View中
        columnchart.setColumnChartData(columnChartData);

    }

}
