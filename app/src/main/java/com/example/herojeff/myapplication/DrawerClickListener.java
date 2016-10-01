package com.example.herojeff.myapplication;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by herojeff on 2016. 9. 14..
 */
public class DrawerClickListener implements AdapterView.OnItemClickListener{
    Context mContext;
    MainActivity.Pac[] pacsForAdapter;
    PackageManager pmForListener;
    public DrawerClickListener(Context c, MainActivity.Pac[] pacs, PackageManager pm) {
        mContext=c;
        pacsForAdapter=pacs;
        pmForListener=pm;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
        if(MainActivity.appLaunchable) {
            Intent launchIntent = pmForListener.getLaunchIntentForPackage(pacsForAdapter[pos].name);
            mContext.startActivity(launchIntent);
        }
    }
}
