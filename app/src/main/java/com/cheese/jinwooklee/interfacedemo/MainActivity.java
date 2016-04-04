package com.cheese.jinwooklee.interfacedemo;


import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.cheese.jinwooklee.interfacedemo.CustomeGoogle.GoogleDataListener;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends FragmentActivity {

    private Boolean task1 = false;
    private Boolean task2 = false;
    private Boolean comp;
    private static ArrayList<HashMap<String,String>> result;
    private CustomeGoogle c_g;
    private sqlData sqLiteDatabase;
    private CustomAdapter arrayAdapter;
    private static Boolean viewByVirus;
    private static Boolean viewByCountry;
    private static Boolean viewbyDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //initiate SQLite database with mainactivity context
        //instantiation will invoke oncreate method
        sqLiteDatabase = new sqlData(this);

        this.viewbyDefault = new Boolean(true);
        this.viewByVirus = new Boolean(false);
        this.viewByVirus = new Boolean(false);

        //setup Custom google loc listener and context of this MainActivity
        c_g = new CustomeGoogle(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(c_g);
        c_g.setGl(new GoogleDataListener() {
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
        final ApiConnection apiConnection = new ApiConnection();

        apiConnection.setApiCALL(new ApiConnection.myApiCALLLisenter() {

            @Override
            public void onComplete() {
                task2 = true;

                //Retreieve all or partial data from SQLite and somehow initiate geocoding
                //Initially we will only pull 5 datasets
                result = new ArrayList<HashMap<String, String>>();
                result = sqLiteDatabase.retrieveRegionDatabase(7);
                pushDatatoListView(result);
                placemarker();
            }

            @Override
            public void onLastRow(ArrayList<HashMap<String, String>> lastrow1) {
                ArrayList<HashMap<String, String>> lastrow = sqLiteDatabase.retrieveRegionDatabase(1);
                comp = sqLiteDatabase.lastrowsCompare(lastrow, lastrow1);
                if (!comp) {
                    apiConnection.downloadContent();
                }
            }

            @Override
            public void onDataLoaded(JSONObject jsonObject) {
                try {
                    //put these data into SQLite database
                    sqLiteDatabase.insertRegionDatabase(jsonObject.getString("virusname"), jsonObject.getString("country"), jsonObject.getInt("lastupdated"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        sqLiteDatabase.destroySQL(this);
    }

    public void sqlcountrywithVirus(String v){
        result = sqLiteDatabase.countrywithVirus(v);
        pushDatatoListView(result);
        placemarker();
    }

    public void pushDatatoListView(ArrayList<HashMap<String, String>> s){
        //Set ListView
        ListView listView = (ListView)findViewById(R.id.list);

        arrayAdapter = new CustomAdapter(this, s);

        listView.setAdapter(arrayAdapter);

        arrayAdapter.setArrayListener(new CustomAdapter.ArrayListener() {
            @Override
            public void rowClicked(String virusname) {
                if (viewByVirus == false) {
                    sqlcountrywithVirus(virusname);
                    viewbyDefault = false;
                    viewByVirus = true;
                }

            }
        });
    }

    public void placemarker(){
        if(this.task1 && this.task2){
            c_g.startReverse(result);
        }
    }

    public void outbreakonClick(View v){
        if(this.viewbyDefault == false) {
            result = new ArrayList<>();
            result = sqLiteDatabase.retrieveRegionDatabase(7);
            pushDatatoListView(result);
            placemarker();
            this.viewbyDefault = true;
            this.viewByVirus = false;
        }
    }
}
