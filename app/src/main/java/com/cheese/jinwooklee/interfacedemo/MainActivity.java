package com.cheese.jinwooklee.interfacedemo;


import android.app.Activity;
import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.TextView;

import com.cheese.jinwooklee.interfacedemo.CustomeGoogle.GoogleDataListener;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends FragmentActivity {

    private Boolean task1 = false;
    private Boolean task2 = false;
    private Boolean comp;
    private Boolean aniExpanded = false;
    private static ArrayList<HashMap<String,String>> result;
    private CustomeGoogle c_g;
    private sqlData sqLiteDatabase;
    private int trackingState;
    private int firstItem;
    private ApiConnection apiConnection;
    private Activity activity;
    private Animation animation;
    private int oldItem = 0;

    private static CustomAdapter arrayAdapter;
    private static Boolean viewByVirus;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private static ListView listView;
    private TextView outbreakText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Full Screen
        //Needs to be initiated before setContentView
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        //Initiate swipeRefresh
        swipeRefresh();

        //Initiate ListView
        listviewInit(this);

        //initiate SQLite database with mainactivity context
        //instantiation will invoke oncreate method
        sqliteData();

        //Initiate View trackers
        this.viewByVirus = new Boolean(false);

        //Initiate api Connection to the server
        apiConnLis();

        //Initiate googlemap API
        googleMapApi();
    }

    public void swipeRefresh(){
        this.swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);

        this.swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ArrayList<HashMap<String, String>> added = sqLiteDatabase.retrieveRegionDatabase(5, result.size());
                result.addAll(added);
                pushDatatoListView(result);
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    public void sqliteData(){
        this.sqLiteDatabase = new sqlData(this);
    }

    public void googleMapApi(){
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
    }

    public void apiConnLis(){

        //setup the api listener
        apiConnection = new ApiConnection();

        apiConnection.setApiCALL(new ApiConnection.myApiCALLLisenter() {

            @Override
            public void onComplete() {
                task2 = true;

                //Retreieve all or partial data from SQLite and somehow initiate geocoding
                //Initially we will only pull 5 datasets
                result = new ArrayList<>();
                result = sqLiteDatabase.retrieveRegionDatabase(7, 0);
                pushDatatoListView(result);
                placemarker();
            }

            @Override
            public void onLastRow(ArrayList<HashMap<String, String>> lastrow1) {
                ArrayList<HashMap<String, String>> lastrow = sqLiteDatabase.retrieveRegionDatabase(1, 0);
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

    public void arrayAdapterInit(ArrayList<HashMap<String,String>> virus){
        arrayAdapter = new CustomAdapter(this, virus);

        //array Listener
        arrayAdapter.setArrayListener(new CustomAdapter.ArrayListener() {
            @Override
            public void rowClicked(String virusname) {
                if (viewByVirus == false) {
                    sqlcountrywithVirus(virusname);
                    viewByVirus = true;
                }

            }
        });
    }

    public void listviewInit(final Activity activity){
        listView = (ListView)findViewById(R.id.list);
        final LinearLayoutWeightAni[] animation = new LinearLayoutWeightAni[1];
        this.activity = activity;
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(R.id.swiperefresh);


        //ListView listener
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                trackingState = scrollState;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.i("firstVisible",String.valueOf(firstVisibleItem));

                firstItem = firstVisibleItem;
                if(firstItem > oldItem){
                    //Expanding
                    animation[0] = new LinearLayoutWeightAni(swipeRefreshLayout, 0, activity, R.id.swiperefresh);
                    animation[0].setDuration(800);
                    swipeRefreshLayout.startAnimation(animation[0]);
                    aniExpanded = true;

                    oldItem = firstVisibleItem;
                }
            }
        });
    }

    public void setListView(CustomAdapter arrayAdapter){
        listView.setAdapter(arrayAdapter);
    }

    public void pushDatatoListView(ArrayList<HashMap<String, String>> s){
        arrayAdapterInit(s);
        setListView(arrayAdapter);
    }

    public void placeAddtionalmarker(ArrayList<HashMap<String,String>> added){
        c_g.startReverse(added, false);
    }

    public void placemarker(){
        if(this.task1 && this.task2){
            c_g.startReverse(result, true);
        }
    }

    public void outbreakonClick(View v){
        if(this.aniExpanded) {

            this.oldItem = this.firstItem;
            this.animation = new LinearLayoutWeightAni(swipeRefreshLayout, 10, activity, R.id.swiperefresh);
            this.animation.setDuration(800);
            this.swipeRefreshLayout.startAnimation(animation);
            this.aniExpanded = false;

        }

//        if(this.viewbyDefault == false) {
//            result = new ArrayList<>();
//            result = sqLiteDatabase.retrieveRegionDatabase(7, 0);
//            pushDatatoListView(result);
//            placemarker();
//
//            this.viewbyDefault = true;
//            this.viewByVirus = false;
//        }
    }
}
