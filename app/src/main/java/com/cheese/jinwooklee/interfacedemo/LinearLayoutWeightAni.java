package com.cheese.jinwooklee.interfacedemo;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;

/**
 * Created by jinwooklee on 16-04-04.
 */
public class LinearLayoutWeightAni extends Animation{

    final private float end;
    private Activity activity;
    private int id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;

    //needs to get initial and ending weight
    //initial weight can be accessed by its view weight
    //ending phase is specified by user
    //for findViewByID you need to put which ID you want to manipulate
    //needs to insert which activity it comes from

    public LinearLayoutWeightAni(SwipeRefreshLayout swipeRefreshLayout, float end, Activity activity, int id){

        this.swipeRefreshLayout = swipeRefreshLayout;
        this.end = end;
        this.activity = activity;
        this.id = id;
    }

//    public LinearLayoutWeightAni(View view, float end, Activity activity, int id){
//        this.view = view;
//        this.end = end;
//        this.activity = activity;
//        this.id = id;
//        this.normal = true;
//
//    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        applyTrans(interpolatedTime);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }

    public void applyTrans(float interpolatedTime){

//        if(this.normal){
//            //set LayoutParams parameters
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
//                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//
//            //find the view by ID that you want to control
//            //SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(R.id.swiperefresh);
//            this.view = this.activity.findViewById(this.id);
//
//            //Set the initial weight
//            float start = ((LinearLayout.LayoutParams)view.getLayoutParams()).weight;
//
//            //Calculate how much changes you require
//            float mDelta = this.end - start;
//
//            //set LayoutParams weight as you desire at given milliseconds
//            lp.weight = (start + (mDelta * interpolatedTime));
//
//            //the view finally gets what it deserve
//            view.setLayoutParams(lp);
//
//        }



        //set LayoutParams parameters
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        //find the view by ID that you want to control
        //SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(R.id.swiperefresh);
        this.swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(this.id);

        //Set the initial weight
        float start = ((LinearLayout.LayoutParams)swipeRefreshLayout.getLayoutParams()).weight;

        //Calculate how much changes you require
        float mDelta = this.end - start;

        //set LayoutParams weight as you desire at given milliseconds
        lp.weight = (start + (mDelta * interpolatedTime));

        //the view finally gets what it deserve
        swipeRefreshLayout.setLayoutParams(lp);

    }
}
