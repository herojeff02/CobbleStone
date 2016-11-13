package com.example.herojeff.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by herojeff on 2016. 9. 14..
 */
public class WidgetLongClickListener implements AdapterView.OnItemLongClickListener {
    Context mContext;
    RelativeLayout homeViewForAdapter;
    public WidgetLongClickListener(Context con, RelativeLayout homeview){
        mContext = con;
        homeViewForAdapter = homeview;
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight());
        lp.leftMargin = (int)view.getX();
        lp.topMargin = (int)view.getY();
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);

        homeViewForAdapter.addView(ll, lp);
        return false;
    }
}