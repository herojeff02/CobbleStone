package com.example.herojeff.myapplication;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.view.View;
import android.widget.AdapterView;

/**
 * Created by herojeff on 2016. 9. 14..
 */
public class DrawerLongClickListener extends Activity implements AdapterView.OnItemLongClickListener {
    //SlidingDrawer drawerForAdapter;
    Context mContext;
    MainActivity.Pac[] pacsForAdapter;
    PackageManager pmForListener;
    public DrawerLongClickListener(){}
    public DrawerLongClickListener(Context c, MainActivity.Pac[] pacs, PackageManager pm) {
        mContext=c;
        pacsForAdapter=pacs;
        pmForListener=pm;
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int pos, long id) {
            /*
            Intent launchIntent = new Intent(Intent.ACTION_MAIN);
            launchIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            ComponentName cp = new ComponentName(pacsForAdapter[pos].packageName, pacsForAdapter[pos].name);
            launchIntent.setComponent(cp);
            */
        //MainActivity.appLaunchable=false;
        ComponentName cp = new ComponentName(pacsForAdapter[pos].packageName, pacsForAdapter[pos].name);

        //uninstallIntent.setComponent(packageURI);
        //Intent uninstallIntent = new Intent(Intent.ACTION_DELETE,packageURI);

        /*////////////Original Code
        Uri packageURI = Uri.parse(cp.getPackageName());
        Intent intent = new Intent(Intent.ACTION_DELETE);
        intent.setData(Uri.parse(pacsForAdapter[pos].packageName));
        mContext.startActivity(intent);
        */

            Uri packageURI = Uri.parse("package:" + Uri.parse(pacsForAdapter[pos].name));
            Intent uninstallIntent = new Intent(Intent.ACTION_DELETE, packageURI)
                    .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                            | Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(uninstallIntent);
        /*
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        TextView view = (TextView)findViewById(R.id.test_view);
        view.setOnClickListener(new View.OnClickListener(){
          public void onClick(View view){
            Uri packageUri = Uri.parse("package:org.klnusbaum.test");
            Intent uninstallIntent =
              new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            startActivity(uninstallIntent);
          }
        });
         */



        return false;
    }


}
