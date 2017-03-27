package com.zhuoxin.androidtest.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhuoxin.androidtest.R;
import com.zhuoxin.androidtest.bean.Bean;

import java.util.List;

/**
 * Created by Administrator on 2017/3/19.
 */

public class WeatherAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bean> mBeanList;

    public WeatherAdapter(Context context, List<Bean> beanList) {
        mContext = context;
        mBeanList = beanList;
    }

    @Override
    public int getCount() {
        if (mBeanList!=null){
            return mBeanList.size();
        }
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return mBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHoler mHoler = null;
        if (convertView == null){
            convertView = View.inflate(mContext,R.layout.item_weather,null);
            mHoler = new ViewHoler(convertView);
            convertView.setTag(mHoler);
        }else {
            mHoler = (ViewHoler) convertView.getTag();
        }
        Bean mBean = mBeanList.get(position);
        mHoler.area.setText(mBean.district);
        mHoler.weather.setText(mBean.weather);
        return convertView;
    }

    class ViewHoler{
        TextView area,weather;
        public ViewHoler(View view){
            area = (TextView) view.findViewById(R.id.area);
            weather = (TextView) view.findViewById(R.id.weather);
        }
    }
}
