package com.zhuoxin.androidtest.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.zhuoxin.androidtest.R;

public class MyPagerAdapter extends PagerAdapter {

	private List<View> viewList = new ArrayList<View>();
	private List<String> urls = new ArrayList<String>();
	private Context mContext;
	
	
	public MyPagerAdapter(Context context,List<View> viewList,List<String> urls) {
		mContext = context;
		this.viewList = viewList;
		this.urls = urls;
	}

	@Override
	public int getCount() {
		return Integer.MAX_VALUE;
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		//position %= viewList.size();
		//1.加载View对象
		View view = View.inflate(mContext, R.layout.activity_lead_item, null);
		ImageView imageView = (ImageView) view.findViewById(R.id.icon);

		Picasso.with(mContext).load(urls.get(position%urls.size())).into(imageView);
//
//		ViewParent vp = view.getParent();
//		if (vp!=null) {
//			ViewGroup parent = (ViewGroup)vp;
//			parent.removeView(view);
//		}

		container.addView(view);
		return view;
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((View)object);
	}
}
