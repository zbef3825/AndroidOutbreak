package com.cheese.jinwooklee.interfacedemo;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by jinwooklee on 16-04-07.
 */
public class googlemapInfo implements GoogleMap.InfoWindowAdapter {

    private final View myView;
    private Activity activity;
    private String country;
    private sqlData SQL = null;

    googlemapInfo(Activity activity, String country){
        this.activity = activity;
        this.myView = activity.getLayoutInflater().inflate(R.layout.googlemapinfo, null);
        this.country = country;
        this.SQL = new sqlData(activity);
    }
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        addingInfo(reformate(SearchingVirus()));
        return this.myView;
    }
    public ArrayList<HashMap<String,String>> SearchingVirus(){
        ArrayList<HashMap<String,String>> result = SQL.getVirusesfromCountry(this.country);
        return result;
    }

    public HashMap<String, Integer> reformate(ArrayList<HashMap<String,String>> s){
        HashMap<String,Integer> result = new HashMap<>();
        for(int i = 0; i < s.size(); i++){
            //does result has s.get(i).get("virusname")?

            //if it does, increase count
            if(result.containsKey(s.get(i).get("virusname"))){
                result.put(s.get(i).get("virusname"), result.get(s.get(i).get("virusname"))+1);
            }
            //add such virus into result
            else{
                result.put(s.get(i).get("virusname"), 1);
            }
        }
       return result;
    }

    public void addingInfo(HashMap<String, Integer> result){
        TableLayout tableLayout = (TableLayout)this.myView.findViewById(R.id.googleInfoWindow);

        Iterator<String> v = result.keySet().iterator();
        for(int i = 0; i < result.size(); i++){

            String virus = new String(v.next());
            TableRow tr = new TableRow(this.activity);
            TextView tv1 = new TextView(this.activity);
            TextView tv2 = new TextView(this.activity);

            //Setting Text
            tv1.setText(virus);
            tv2.setText(result.get(virus).toString());

            //Setting columns for respective textviews
            tv1.setLayoutParams(new TableRow.LayoutParams(1));
            tv2.setLayoutParams(new TableRow.LayoutParams(2));

            tv1.setMaxWidth(300);
            tv2.setMaxWidth(100);

            //17 is Center and 5 is right
            tv1.setGravity(17);
            tv2.setGravity(5);

            tr.addView(tv1);
            tr.addView(tv2);

            tableLayout.addView(tr);
        }

    }
}
