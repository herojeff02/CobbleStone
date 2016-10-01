package com.example.herojeff.myapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SlidingDrawer;
import android.widget.TextView;

/**
 * Created by herojeff on 2016. 9. 14..
 */
public class DrawerAdapter extends BaseAdapter {
    Context mContext;
    MainActivity.Pac[] pacsForAdapter;
    public DrawerAdapter(Context c, MainActivity.Pac pacs[]){
        mContext = c;
        pacsForAdapter = pacs;
    }

    @Override
    public int getCount() {
        return pacsForAdapter.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder{
        TextView text;
        ImageView icon;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);//메인액티비티 밖이므로 layoutinflater 그냥 사용할 수 없음
        if(convertView == null){
            convertView = li.inflate(R.layout.drawer_item, null);

            viewHolder = new ViewHolder();
            viewHolder.text = (TextView)convertView.findViewById(R.id.icon_text);//마찬가지로 캐스팅
            viewHolder.icon = (ImageView)convertView.findViewById(R.id.icon_image);
            convertView.setTag(viewHolder);
        }
        else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.text.setText(pacsForAdapter[pos].label);
        viewHolder.icon.setImageDrawable(pacsForAdapter[pos].icon);

        return convertView;
    }
}
