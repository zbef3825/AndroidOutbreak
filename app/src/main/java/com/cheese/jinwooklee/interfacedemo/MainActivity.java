package com.cheese.jinwooklee.interfacedemo;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Space;
import android.widget.TextView;

import com.cheese.jinwooklee.interfacedemo.CustomGoogleMap.GoogleDataListener;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends BaseActivity{

    private Boolean task1 = false;
    private Boolean task2 = false;
    private Boolean comp;
    private Boolean aniExpanded = false;
    private ArrayList<HashMap<String,String>> result;
    private CustomGoogleMap c_g;
    private sqlData sqLiteDatabase;
    private int trackingState;
    private int firstItem;
    private ApiConnection apiConnection;
    private Activity activity;
    private Animation animation;
    private int oldItem = 0;
    private Boolean halfExpanded = false;
    private Boolean Expanding = false;
    private Toolbar toolbar;

    private CustomAdapter arrayAdapter;
    private Boolean viewByVirus;
    private ListView listView;
    private TextView outbreakText;
    private Space space;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activateToolBar();

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch (item.getItemId()){
            case R.id.action_1week:
                Log.i("Info", "Popup menu CLicked");
                pushDatatoListView(virusByTime(7));
                placemarker();
                return true;

            case R.id.action_1month:
                pushDatatoListView(virusByTime(100));
                placemarker();
                return true;

            case R.id.action_3month:
                pushDatatoListView(virusByTime(300));
                placemarker();
                return true;

            case R.id.action_6month:
                pushDatatoListView(virusByTime(600));
                placemarker();
                return true;

            case R.id.action_1year:
                pushDatatoListView(virusByTime(10000));
                placemarker();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void sqliteData(){
        this.sqLiteDatabase = new sqlData(this);
    }

    public void googleMapApi(){
        //setup Custom google loc listener and context of this MainActivity
        c_g = new CustomGoogleMap(this, this);

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

                //retreive data by time constraint
                //first report is always 1 week
                result = new ArrayList<>();
                result = virusByTime(7);
                pushDatatoListView(result);
                placemarker();
            }

            @Override
            public void onLastRow(ArrayList<HashMap<String, String>> lastrow1) {
                ArrayList<HashMap<String, String>> lastrow = sqLiteDatabase.retrieveRegionDatabaseLastRow(1, 0);
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
        final ListView listView = (ListView)this.activity.findViewById(R.id.list);


        //ListView listener
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                trackingState = scrollState;
                if (trackingState == 0) {
                    oldItem = firstItem;
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //Log.i("firstVisible",String.valueOf(firstVisibleItem));

                firstItem = firstVisibleItem;
                if (firstItem > oldItem && trackingState == 2) {

                    //from half to full expand
                    space = (Space) findViewById(R.id.spaceee);
                    animation[0] = new LinearLayoutWeightAni(space, 10f, activity, R.id.spaceee);
                    animation[0].setDuration(900);
                    space.startAnimation(animation[0]);

                    animation[0] = new LinearLayoutWeightAni(listView, 0f, activity, R.id.list);
                    animation[0].setDuration(800);
                    listView.startAnimation(animation[0]);

                    //only half is true because it's not fully Expanded yet
                    halfExpanded = false;
                    aniExpanded = true;
                } else if (firstItem < oldItem && trackingState == 2) {

                    //Collapsing
                    space = (Space) findViewById(R.id.spaceee);
                    animation[0] = new LinearLayoutWeightAni(space, 0.55f, activity, R.id.spaceee);
                    animation[0].setDuration(900);
                    space.startAnimation(animation[0]);

                    animation[0] = new LinearLayoutWeightAni(listView, 10f, activity, R.id.list);
                    animation[0].setDuration(800);
                    listView.startAnimation(animation[0]);

                    //only half is true because it's not fully Expanded yet
                    halfExpanded = false;
                    aniExpanded = false;

                }
            }
        });
    }

    public void setListView(CustomAdapter arrayAdapter){
        listView.setAdapter(arrayAdapter);
    }

    public void pushDatatoListView(ArrayList<HashMap<String, String>> s){
        this.result = s;
        arrayAdapterInit(s);
        setListView(arrayAdapter);
    }

    public void placeAddtionalmarker(ArrayList<HashMap<String,String>> added){
        c_g.startReverse(added, false);
    }

    public void placemarker(){
        if(this.task1 && this.task2){
            c_g.startReverse(this.result, true);
        }
    }

    public void outbreakonClick(View v){
        //Initially false for both value
        if(!this.halfExpanded && !this.aniExpanded){

            this.space = (android.widget.Space)findViewById(R.id.spaceee);
            this.animation = new LinearLayoutWeightAni(space, 5f, activity, R.id.spaceee);
            this.animation.setDuration(900);
            this.space.startAnimation(animation);

            this.animation = new LinearLayoutWeightAni(listView, 10f, activity, R.id.list);
            this.animation.setDuration(800);
            this.listView.startAnimation(animation);

            //only half is true because it's not fully Expanded yet
            this.halfExpanded = true;

            this.oldItem = this.firstItem;

        }

        else if(!this.aniExpanded && this.halfExpanded) {
            hideToolBar();
            this.space = (Space)findViewById(R.id.spaceee);
            this.animation = new LinearLayoutWeightAni(space,10f, activity, R.id.spaceee);
            this.animation.setDuration(900);
            this.space.startAnimation(animation);

            this.animation = new LinearLayoutWeightAni(listView, 0f , activity, R.id.list);
            this.animation.setDuration(800);
            this.listView.startAnimation(animation);

            //only half is true because it's not fully Expanded yet
            this.halfExpanded = false;
            this.aniExpanded = true;

            this.oldItem = this.firstItem;

        }
        else{
            showToolBar();
            this.space = (Space)findViewById(R.id.spaceee);
            this.animation = new LinearLayoutWeightAni(space,0.55f, activity, R.id.spaceee);
            this.animation.setDuration(900);
            this.space.startAnimation(animation);

            this.animation = new LinearLayoutWeightAni(listView, 10f , activity, R.id.list);
            this.animation.setDuration(800);
            this.listView.startAnimation(animation);

           //false because it is now changing to collpased view
            this.halfExpanded = false;
            this.aniExpanded = false;

            this.oldItem = this.firstItem;

        }
    }

    public ArrayList<HashMap<String, String>> virusByTime(int date){
        viewByVirus = false;
        return sqLiteDatabase.virusByTime(date);
    }
}
