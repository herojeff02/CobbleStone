package com.example.herojeff.myapplication;

import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;

import java.util.List;

/**
 * Created by herojeff on 2016. 9. 13..
 */
public class MainActivity extends AppCompatActivity {
    DrawerAdapter drawerAdapterObject;
    GridView drawerGrid;
    SlidingDrawer slidingDrawer;
    RelativeLayout homeView;
    class Pac{
        Drawable icon;
        String name;
        String packageName;
        String label;
    }
    Pac[] pacs;
    PackageManager pm;
    AppWidgetManager mAppWidgetManager;
    AppWidgetHost mAppWidgetHost;

    static boolean appLaunchable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BackTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this,R.id.APPWIDGET_HOST_ID);

        drawerGrid = (GridView) findViewById(R.id.content);//gridView 가져오기
        slidingDrawer = (SlidingDrawer)findViewById(R.id.drawer);
        homeView=(RelativeLayout)findViewById(R.id.home_view);
        pm = getPackageManager();
        set_pacs();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");//왜 추가?
        registerReceiver(new PacReceiver(), filter);
    }

    public void set_pacs() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent, 0);
        pacs = new Pac[pacsList.size()];
        for(int i = 0;i<pacsList.size();i++){
            pacs[i]=new Pac();
            pacs[i].icon=pacsList.get(i).loadIcon(pm);
            pacs[i].name =pacsList.get(i).activityInfo.name;
            pacs[i].packageName =pacsList.get(i).activityInfo.packageName;
            pacs[i].label =pacsList.get(i).loadLabel(pm).toString();
        }
        new SortApps().exchange_sort(pacs);
        drawerAdapterObject = new DrawerAdapter(this, pacs);
        drawerGrid.setAdapter(drawerAdapterObject);
        slidingDrawer.bringToFront();
        drawerGrid.setOnItemClickListener(new DrawerClickListener(this, pacs, pm));//앱 실행
    }
    public class PacReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            set_pacs();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK) {
            if(slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
        }
        if(keyCode == KeyEvent.KEYCODE_MENU){
            if(slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
        }
        return true;
    }
}