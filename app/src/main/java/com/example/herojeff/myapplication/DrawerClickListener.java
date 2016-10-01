package com.example.herojeff.myapplication;

import android.content.ComponentName;
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
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cp = new ComponentName(pacsForAdapter[pos].packageName, pacsForAdapter[pos].name);
            launchIntent.setComponent(cp);
            mContext.startActivity(launchIntent);
        }
    }
}