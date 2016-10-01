package com.example.herojeff.myapplication;


import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
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
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
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
    int REQUEST_CREATE_APPWIDGET = 100;
    int REQUEST_PICK_APPWIDGET = 120;

    static boolean appLaunchable = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BackTheme);
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        //tintManager.setTintColor(Color.parseColor("#00000000"));
        tintManager.setStatusBarTintEnabled(false);
        tintManager.setNavigationBarTintEnabled(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this,R.id.APPWIDGET_HOST_ID);

        drawerGrid = (GridView) findViewById(R.id.content);//gridView 가져오기
        slidingDrawer = (SlidingDrawer)findViewById(R.id.drawer);
        homeView=(RelativeLayout)findViewById(R.id.home_view);
        pm = getPackageManager();
        set_pacs();

        homeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectWidget();
                return false;
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");//왜 추가?
        registerReceiver(new PacReceiver(), filter);
    }

    void selectWidget(){
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }
    void addEmptyData(Intent pickIntent){
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if(resultCode==RESULT_OK){
            if(requestCode == REQUEST_PICK_APPWIDGET){
                configureWidget(data);
            }
            else if(requestCode == REQUEST_CREATE_APPWIDGET){
                createWidget(data);
            }
        }
        else if(resultCode==RESULT_CANCELED && data!=null){
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if(appWidgetId != -1){
                mAppWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }
    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(data);
        }
    }
    public void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.CENTER_HORIZONTAL);
        homeView.addView(hostView, lp);
        slidingDrawer.bringToFront();
    }
    @Override
    protected void onStart() {
        super.onStart();
        mAppWidgetHost.startListening();
    }
    @Override
    protected void onStop() {
        super.onStop();
        mAppWidgetHost.stopListening();
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
    public class PacReceiver extends BroadcastReceiver {

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