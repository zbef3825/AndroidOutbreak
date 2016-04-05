package com.cheese.jinwooklee.interfacedemo;


import android.os.Bundle;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.cheese.jinwooklee.interfacedemo.CustomeGoogle.GoogleDataListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

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
    private SwipeRefreshLayout swipeRefreshLayout;
    private int trackingState;
    private int oldItem;
    private int firstItem;
    private LayoutweightFloat num;
    private LinearLayout.LayoutParams Weightparams;


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

        //Initiate Layout Weight
        num = new LayoutweightFloat(13.0f,13.0f, 1f);

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
        arrayAdapter = new CustomAdapter(this, s);

        //Set ListView
        ListView listView = (ListView)findViewById(R.id.list);
        listView.setAdapter(arrayAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                //track scrollState
                // 0 when idle
                // 1 when the screen is touched
                trackingState = scrollState;
                if(scrollState == 0){
                    oldItem = firstItem;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                firstItem = firstVisibleItem;

                if (oldItem < firstItem && trackingState == 1){
                    //User scrolled up
                    num.NegNum(0.1f);

                    Log.i("Info", String.valueOf(num.getNum()));
                    Log.i("Info", String.valueOf(oldItem));
                    Log.i("Info", String.valueOf(firstVisibleItem));
                    Log.i("Info", String.valueOf(trackingState));


                    //Layout_weight params
                    Weightparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, num.getNum());
                    //Create reference to the SwipeRefreshLayout layout
                    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
                    swipeRefreshLayout.setLayoutParams(Weightparams);
                }
                else if (oldItem > firstItem && trackingState == 1) {
                    //User scrolled down
                    num.addNum(0.1f * (oldItem - firstItem));

                    Log.i("Info", String.valueOf(num.getNum()));

                    //Layout_weight params
                    Weightparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, num.getNum());
                    //Create reference to the SwipeRefreshLayout layout
                    SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swiperefresh);
                    swipeRefreshLayout.setLayoutParams(Weightparams);
                }
            }
        });

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

    public void placeAddtionalmarker(ArrayList<HashMap<String,String>> added){
        c_g.startReverse(added, false);
    }

    public void placemarker(){
        if(this.task1 && this.task2){
            c_g.startReverse(result, true);
        }
    }

    public void outbreakonClick(View v){
        if(this.viewbyDefault == false) {
            result = new ArrayList<>();
            result = sqLiteDatabase.retrieveRegionDatabase(7, 0);
            pushDatatoListView(result);
            placemarker();
            this.viewbyDefault = true;
            this.viewByVirus = false;
        }
    }
}
