package com.cheese.jinwooklee.interfacedemo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.common.api.Api;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity {

    private Boolean task1 = false;
    private Boolean task2 = false;
    private ArrayList<String> virusCountry = null;
    private ArrayList<String> virusName = null;
    private CustomeGoogle c_g;

    public void pushDatatoListView(ArrayList<String> s){
        //Set ListView
        ListView listView = (ListView)findViewById(R.id.list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, s);
        listView.setAdapter(arrayAdapter);
    }

    public void placemarker(){
        if(this.task1 == true && this.task2 == true){
            for(int i = 0; i < virusCountry.size(); i++){
                c_g.startReverse(virusCountry.get(i));
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //setup Custome google loc listener and context of this MainActivity
        c_g = new CustomeGoogle(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(c_g);

        //set Array List
        virusCountry = new ArrayList<String>();
        virusName = new ArrayList<String>();

        c_g.setGl(new CustomeGoogle.GoogleDataListener() {
            @Override
            public void mapLoaded() {
                task1 = true;
                placemarker();

            }

            @Override
            public void onJobComplete(String s) {
            }
        });

        //setup the api listener
        ApiConnection apiConnection = new ApiConnection();

        apiConnection.setApiCALL(new ApiConnection.myApiCALLLisenter() {
            @Override
            public void onRequest(String string) {

            }

            @Override
            public void onComplete() {
                task2 = true;
                //Push all data into ListView
                pushDatatoListView(virusName);
                placemarker();
            }

            @Override
            public void onDataLoaded(JSONObject jsonObject) {
                try {
                    //add each data into virusInfo Array List
                    virusCountry.add(jsonObject.getString("country"));
                    virusName.add(jsonObject.getString("virusname"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
