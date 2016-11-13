package com.example.herojeff.myapplication;


import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SlidingDrawer;

import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by herojeff on 2016. 9. 13..
 */
public class MainActivity extends AppCompatActivity{
    DrawerAdapter drawerAdapterObject;

    GridView drawerGrid;
    SlidingDrawer slidingDrawer;
    RelativeLayout homeView;
    ViewPager mPager;
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

    AppWidgetHostView hostView;
    LinearLayout tempHostView;

    static boolean appLaunchable = true;
    static boolean widgetHere = false;
    static int appPos=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if(android.os.Build.VERSION.SDK_INT<=19) {
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setTintColor(R.color.dockColor);
            tintManager.setStatusBarTintColor(R.color.dockColor);
            tintManager.setStatusBarTintEnabled(false);
            tintManager.setNavigationBarTintEnabled(true);
            setTheme(R.style.BackTheme);
        }
        else{
            setTheme(R.style.Theme);//////////////////////////////////////////////////////Manage Theme later
        }

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        mPager = new ViewPager(this);


        mAppWidgetManager = AppWidgetManager.getInstance(this);
        mAppWidgetHost = new AppWidgetHost(this,R.id.APPWIDGET_HOST_ID);

        drawerGrid = (GridView) findViewById(R.id.content);//gridView 가져오기
        slidingDrawer = (SlidingDrawer)findViewById(R.id.drawer);
        homeView=(RelativeLayout)findViewById(R.id.home);
        tempHostView = new LinearLayout(this);

        pm = getPackageManager();
        set_pacs();


        /*
        IntentFilter filter = new IntentFilter();
        filter.addDataScheme("package");//왜 추가?
        registerReceiver(new PacReceiver(), filter);
        */

        buttonsListener();


    }


    public void selectWidget(){
        int appWidgetId = this.mAppWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        addEmptyData(pickIntent);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }
    public void addEmptyData(Intent pickIntent){
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
    public void removeWidget() {
        homeView.removeView(hostView);
        widgetHere=false;
    }
    public void createWidget(Intent data) {
        //View view = View.inflate(getApplicationContext(), R.layout.inflate_one, null);
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = mAppWidgetManager.getAppWidgetInfo(appWidgetId);
        hostView = mAppWidgetHost.createView(this, appWidgetId, appWidgetInfo);
        hostView.setAppWidget(appWidgetId, appWidgetInfo);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.CENTER_VERTICAL,RelativeLayout.CENTER_HORIZONTAL);
        widgetHere=true;
        homeView.addView(hostView, lp);
        slidingDrawer.bringToFront();


        /*
        LinearLayout r1 = new LinearLayout(this);
        LinearLayout r2 = new LinearLayout(this);
        LinearLayout r3 = new LinearLayout(this);
        LinearLayout r4 = new LinearLayout(this);
        LinearLayout r5 = new LinearLayout(this);
        if(mPager.getCurrentItem()==0)
            r1.addView(hostView);
        else if(mPager.getCurrentItem()==1)
            r2.addView(tempHostView);
        else if(mPager.getCurrentItem()==2)
            r3.addView(tempHostView);
        else if(mPager.getCurrentItem()==3)
            r4.addView(tempHostView);
        else if(mPager.getCurrentItem()==4)
            r5.addView(tempHostView);

        mAdapter.add(r1);
        mAdapter.add(r2);
        mAdapter.add(r3);
        mAdapter.add(r4);
        mAdapter.add(r5);

        */


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
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.facebook.orca");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
                //Toast.makeText(MainActivity.this, "Chat Clicked", Toast.LENGTH_SHORT).show();
            }
        });
        ImageButton dial = (ImageButton)findViewById(R.id.dial);
        dial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent launchIntent = getPackageManager().getLaunchIntentForPackage("com.android.contacts");
                if (launchIntent != null) {
                    startActivity(launchIntent);
                }
                //Toast.makeText(MainActivity.this, "Phone Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void set_pacs() {
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> pacsList = pm.queryIntentActivities(mainIntent,0);
        pacs = new Pac[pacsList.size()-2];
        int j=0;
        for(int i = 0;i<pacsList.size();i++){
            pacs[j]=new Pac();
            pacs[j].icon = pacsList.get(i).loadIcon(pm);
            pacs[j].name = pacsList.get(i).activityInfo.name;
            pacs[j].packageName = pacsList.get(i).activityInfo.packageName;
            pacs[j].label = pacsList.get(i).loadLabel(pm).toString();
            if(pacs[j].label.contains("CobbleStone")){
                j--;
            }
            else if(pacs[j].label.contains("Messenger")){
                j--;
            }
            j++;
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
                return true;
            }
        });

    }

    boolean DrawerLongClickListener(MainActivity.Pac[] pacs, int pos){
        appPos=pos;

        Uri packageURI = Uri.parse("package:" + Uri.parse(pacs[pos].packageName));
        Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(uninstallIntent);
        return true;
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
        if(keyCode == KeyEvent.KEYCODE_MENU) {
            final Context context = this;
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
            if (widgetHere) {
                alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton("Remove Widget", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        removeWidget();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
            else
                selectWidget();
        }

        if(keyCode == KeyEvent.KEYCODE_HOME){
            if(slidingDrawer.isOpened()) {
                slidingDrawer.animateClose();
            }
        }
        return true;
    }
}