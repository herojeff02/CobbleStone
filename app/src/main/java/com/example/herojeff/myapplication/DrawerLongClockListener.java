package com.example.herojeff.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;
import android.widget.TextView;

/**
 * Created by herojeff on 2016. 9. 14..
 */
public class DrawerLongClockListener implements AdapterView.OnItemLongClickListener {
    Context mContext;
    SlidingDrawer drawerForAdapter;
    RelativeLayout homeViewForAdapter;
    public DrawerLongClockListener(Context con, SlidingDrawer sdraw, RelativeLayout homeview){
        mContext = con;
        drawerForAdapter = sdraw;
        homeViewForAdapter = homeview;
    }
    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        MainActivity.appLaunchable=false;
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(view.getWidth(), view.getHeight());
        lp.leftMargin = (int)view.getX();
        lp.topMargin = (int)view.getY();
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout ll = (LinearLayout) li.inflate(R.layout.drawer_item, null);

        ((ImageView)ll.findViewById(R.id.icon_image)).setImageDrawable(((ImageView)view.findViewById(R.id.icon_image)).getDrawable());
        ((TextView)ll.findViewById(R.id.icon_text)).setText(((TextView)view.findViewById(R.id.icon_text)).getText());

        homeViewForAdapter.addView(ll, lp);
        drawerForAdapter.animateClose();
        drawerForAdapter.bringToFront();
        return false;
    }
}
