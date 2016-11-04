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
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
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
    static int appPos=10;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.LolTheme);
        if(android.os.Build.VERSION.SDK_INT<=19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setTintColor(R.color.dockColor);
            tintManager.setStatusBarTintColor(R.color.dockColor);
            tintManager.setStatusBarTintEnabled(false);
            tintManager.setNavigationBarTintEnabled(true);
        }
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this,R.id.APPWIDGET_HOST_ID);

        drawerGrid = (GridView) findViewById(R.id.content);//gridView 가져오기
        slidingDrawer = (SlidingDrawer)findViewById(R.id.drawer);
        homeView=(RelativeLayout)findViewById(R.id.home);
        pm = getPackageManager();
        set_pacs();

        homeView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                selectWidget();
                if(slidingDrawer.isOpened())
                    slidingDrawer.animateClose();
                return false;
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        filter.addDataScheme("package");//왜 추가?
        registerReceiver(new PacReceiver(), filter);

        buttonsListener();
        //colorManager(slidingDrawer.isOpened(),tintManager);

        /*
        final PackageManager pm = getPackageManager();
        //get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        */





    }

    /*
    public void colorManager(boolean opened, SystemBarTintManager tintManager){
        if(opened)
            tintManager.setStatusBarTintEnabled(true);
        else
            tintManager.setStatusBarTintEnabled(false);
    }

*/

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
    public void removeWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        AppWidgetHostView hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        homeView.removeView(hostView);
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
        try{///////////////////////////////////////////////////////////////////연구 필요
            unregisterReceiver(new PacReceiver());
        }
        catch(IllegalArgumentException e){
        }
    }

    public void buttonsListener(){
        ImageButton chat = (ImageButton)findViewById(R.id.chat);
        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("chat pressed");
            }
        });
        ImageButton dial = (ImageButton)findViewById(R.id.dial);
        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("dial pressed");
            }
        });
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
        DrawerClickListener drawerClickListener = new DrawerClickListener(this, pacs, pm);
        drawerGrid.setOnItemClickListener(drawerClickListener);//앱 실행
        drawerGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                DrawerLongClickListener(pacs, i);
                return false;
            }
        });

    }

    void DrawerLongClickListener(MainActivity.Pac[] pacs, int pos){
        appPos=pos;
        MainActivity.Pac[] pacsForAdapter;
        pacsForAdapter=pacs;

        Uri packageURI = Uri.parse("package:" + Uri.parse(pacsForAdapter[pos].packageName));
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(uninstallIntent);
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
        }

        if(keyCode == KeyEvent.KEYCODE_HOME){
            if(slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
        }
        return true;
    }
}