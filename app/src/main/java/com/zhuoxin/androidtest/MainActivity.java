package com.zhuoxin.androidtest;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhuoxin.androidtest.adapter.MyPagerAdapter;
import com.zhuoxin.androidtest.adapter.WeatherAdapter;
import com.zhuoxin.androidtest.bean.Bean;
import com.zhuoxin.androidtest.bean.Data;
import com.zhuoxin.androidtest.bean.TempBean;
import com.zhuoxin.androidtest.view.MyMarkerView;
import com.zhuoxin.androidtest.view.xlistview.XListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.viewpager)
    ViewPager mViewpager;


    @Bind(R.id.listView)
    XListView mListView;
    @Bind(R.id.dot_layout)
    LinearLayout mDotLayout;
    private OkHttpClient mOkHttpClient;
    private UUID mUUID;
    private String uuidString;
    //存放图片集合
    List<String> picUrl = new ArrayList<>();
    //存放轮播图控件
    private List<View> viewList = new ArrayList<View>();
    //存放实体类
    private List<Bean> mList = new ArrayList<Bean>();
    //存放温度
    private List<String> mTempList = new ArrayList<String>();


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            int index = mViewpager.getCurrentItem();
            mViewpager.setCurrentItem(index + 1);
            mHandler.sendEmptyMessageDelayed(1, 3000);
        }

    };
    private MyPagerAdapter mMyPagerAdapter;
    public static TempBean mTempBean;
    private ArrayList<Float> mFloats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mUUID = UUID.randomUUID();
        uuidString = mUUID.toString();

        //加载温度
        initTemp();


        //mListView.addHeaderView(mView);


        new Thread(new Runnable() {
            @Override
            public void run() {
                //加载数据
                initData();
            }
        }).start();


        new Thread(new Runnable() {
            @Override
            public void run() {
                initWeather();
            }
        }).start();
    }

    /**
     * 初始化所有的点
     */
    private void initDotLayout() {
        mDotLayout.removeAllViews();
        for (int i = 0; i < picUrl.size(); i++) {
            View dotView = new View(this);
            LinearLayout.LayoutParams mLayoutParams = new LinearLayout.LayoutParams(16, 16);
            if (i > 0) {
                mLayoutParams.leftMargin = 10;
            }
            dotView.setLayoutParams(mLayoutParams);
            mDotLayout.addView(dotView);
        }
    }

    private LineData makeLineData(int count) {
        ArrayList<String> x = new ArrayList<String>();

        for (int i = 0; i < count; i++) {
            // x轴显示的数据
            x.add("x" + i);
        }


        // y轴的数据
        ArrayList<Entry> y = new ArrayList<Entry>();
        for (int i = 0; i < count; i++) {
            Entry mEntry = new Entry(i, mFloats.get(i));
            y.add(mEntry);

        }

        LineDataSet mDataSet = new LineDataSet(y, "测试数据");



        ArrayList<LineDataSet> mLineDataSets = new ArrayList<LineDataSet>();
        mLineDataSets.add(mDataSet);

        LineData mLineData = new LineData(mDataSet);


        return mLineData;
    }

    private void initTemp() {
        //创建OkHttpClient对象
        OkHttpClient mClient = new OkHttpClient();


        Request.Builder requestBuilder = new Request.Builder().url("http://cloud.bmob.cn/6112dd9a882286d2/androidtest2?uuid=" + uuidString);

        Request request = requestBuilder.build();

        //请求队列
        Call call = mClient.newCall(request);
        call.enqueue(new Callback() {
            //请求失败回调
            @Override
            public void onFailure(Call call, IOException e) {

            }

            //请求成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("tttttttt", response + "");
                try {

                    JSONObject mJSONObject = new JSONObject(response.body().string());

//                    JSONArray mData = mJSONObject.getJSONArray("data");
                    String mData = mJSONObject.getString("data");
                    Data data = new Gson().fromJson(mData, new TypeToken<Data>() {
                    }.getType());


                    float mMon = Float.parseFloat(data.Mon);
                    float mTue = Float.parseFloat(data.Tue);
                    float mWed = Float.parseFloat(data.Thu);
                    float mThu = Float.parseFloat(data.Wed);
                    float mFri = Float.parseFloat(data.Fri);
                    float mSat = Float.parseFloat(data.Sat);
                    float mSun = Float.parseFloat(data.Sun);

                    mFloats = new ArrayList<>();
                    mFloats.add(mMon);
                    mFloats.add(mThu);
                    mFloats.add(mTue);
                    mFloats.add(mWed);
                    mFloats.add(mFri);
                    mFloats.add(mSat);
                    mFloats.add(mSun);
                    for (int i = 0; i < mFloats.size(); i++) {
                        Log.e("ssss", mFloats.get(i) + "");
                    }

                    //mTempBean = new TempBean(mMon, mTue, mWed, mThu, mFri, mSat, mSun);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {


                            LineChart mLineChart = (LineChart) findViewById(R.id.lineChart);

                            MyMarkerView mv = new MyMarkerView(MainActivity.this, R.layout.custom_marker_view);
                            mv.setChartView(mLineChart);
                            mLineChart.setMarker(mv);

                            final String[] quarters = new String[] { "周一", "周二", "周三", "周四", "周五", "周六", "周日" };
                            IAxisValueFormatter formatter = new IAxisValueFormatter() {

                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return quarters[(int) value];
                                }

                            };

                            XAxis xAxis = mLineChart.getXAxis();
                            xAxis.setGranularity(1f); // minimum axis-step (interval) is 1
                            xAxis.setValueFormatter(formatter);

                            LineData mLineData = makeLineData(7);

                            mLineChart.setData(mLineData);

                            mLineChart.notifyDataSetChanged();
                        }
                    });

                } catch (JSONException e) {
                    Log.e("解析异常", "解析异常");
                    e.printStackTrace();
                }

            }
        });
    }

    /**
     * 获取当前时间
     *
     * @return
     */
    public String getSystime() {
        String sysTime;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:EEEEE");
        //获取当前系统时间并格式化
        sysTime = dateFormat.format(new Date(System.currentTimeMillis()));
        return sysTime;
    }

    private XListView.IXListViewListener mIXListViewListener = new XListView.IXListViewListener() {
        @Override
        public void onRefresh() {
            mHandler.removeCallbacksAndMessages(null);
            //数据刷新
            initData();
            initWeather();

            mListView.stopRefresh();
            mListView.setRefreshTime(getSystime());
        }

        @Override
        public void onLoadMore() {
            mListView.stopLoadMore();
        }
    };

    private void initWeather() {
        OkHttpClient httpClient = new OkHttpClient();
        Request.Builder requestBuilder = new Request.Builder().url("http://cloud.bmob.cn/6112dd9a882286d2/androidtest3?uuid=" + uuidString);

        Request request = requestBuilder.build();

        //请求队列
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            //请求失败回调
            @Override
            public void onFailure(Call call, IOException e) {

            }

            //请求成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("eeeeeeee", response + "");
                try {

                    JSONObject mJSONObject = new JSONObject(response.body().string());

                    JSONArray mData = mJSONObject.getJSONArray("data");
                    mList.clear();
                    for (int i = 0; i < mData.length(); i++) {
                        JSONObject mObject = mData.getJSONObject(i);
                        String mDistrict = mObject.getString("district");
                        String mWeather = mObject.getString("weather");
                        Bean mBean = new Bean(mDistrict, mWeather);
                        mList.add(mBean);
                        //mHandler.sendEmptyMessage(3);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                WeatherAdapter mWeatherAdapter = new WeatherAdapter(MainActivity.this, mList);
                                mListView.setAdapter(mWeatherAdapter);
                                mListView.setPullRefreshEnable(true);
                                //添加监听
                                mListView.setXListViewListener(mIXListViewListener);
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initData() {
        //创建OkHttpClient对象
        mOkHttpClient = new OkHttpClient();


        Request.Builder requestBuilder = new Request.Builder().url("http://cloud.bmob.cn/6112dd9a882286d2/androidtest1?uuid=" + uuidString);

        Request request = requestBuilder.build();

        //请求队列
        Call call = mOkHttpClient.newCall(request);
        call.enqueue(new Callback() {
            //请求失败回调
            @Override
            public void onFailure(Call call, IOException e) {

            }

            //请求成功回调
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.e("ssssssss", response + "");
                try {

                    JSONObject mJSONObject = new JSONObject(response.body().string());

                    JSONArray mData = mJSONObject.getJSONArray("data");
                    picUrl.clear();
                    for (int i = 0; i < mData.length(); i++) {
                        //JSONObject mObject = mData.getJSONObject(i);
                        String url = mData.getString(i);
                        picUrl.add(url);

                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                    //初始化所有的点
                    initDotLayout();
                    //初始化图片控件
                    initPagerData();
                    updateTextAndDot();
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void updateTextAndDot() {
        int currentItem = mViewpager.getCurrentItem()%picUrl.size();//获取当前选中的page


        //如果当前的currentItem和点的位置相同，点设置为白色，否则是黑色
        for (int i = 0; i < mDotLayout.getChildCount(); i++) {
            View childView = mDotLayout.getChildAt(i);
            childView.setBackgroundResource(currentItem==i?R.drawable.dot_select:
                    R.drawable.dot_unselect);
        }
    }

    private void initPagerData() {
        viewList.clear();
        for (int i = 0; i < picUrl.size(); i++) {
            ImageView mImageView = null;
            mImageView = (ImageView) getLayoutInflater().inflate(R.layout.activity_lead_item, null);
            //viewList.clear();
            viewList.add(mImageView);


        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mMyPagerAdapter = new MyPagerAdapter(MainActivity.this, viewList, picUrl);
                mViewpager.setAdapter(mMyPagerAdapter);
                mViewpager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                    @Override
                    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                    }

                    @Override
                    public void onPageSelected(int position) {
                        updateTextAndDot();
                    }

                    @Override
                    public void onPageScrollStateChanged(int state) {

                    }
                });
                mViewpager.setCurrentItem(picUrl.size() * 1000);
            }
        });

        mHandler.sendEmptyMessageDelayed(1, 3000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
    }
}
