package com.example.herojeff.myapplication;

/**
 * Created by herojeff on 2016. 9. 14..
 */
public class SortApps {
    public void exchange_sort(MainActivity.Pac[] pacs){
        MainActivity.Pac temp;
        for(int i = 0;i<pacs.length-1;i++){
            for(int j=i+1;j<pacs.length;j++){
                if(pacs[i].label.compareToIgnoreCase(pacs[j].label)>0) {
                    temp = pacs[i];
                    pacs[i]=pacs[j];
                    pacs[j]=temp;
                }
            }
        }
    }
}
