package com.cheese.jinwooklee.interfacedemo;

import android.app.Activity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.LinearLayout;
import android.widget.Space;

/**
 * Created by jinwooklee on 16-04-04.
 */
public class LinearLayoutWeightAni extends Animation{

    private float end;
    private Activity activity;
    private int id;
    private SwipeRefreshLayout swipeRefreshLayout;
    private View view;
    private String type;
    private Space space;

    //needs to get initial and ending weight
    //initial weight can be accessed by its view weight
    //ending phase is specified by user
    //for findViewByID you need to put which ID you want to manipulate
    //needs to insert which activity it comes from

    LinearLayoutWeightAni(SwipeRefreshLayout swipeRefreshLayout, float end, Activity activity, int id){

        this.swipeRefreshLayout = swipeRefreshLayout;
        this.end = end;
        this.activity = activity;
        this.id = id;
        this.type = "swipe";
    }

    LinearLayoutWeightAni(View view, float end, Activity activity, int id){
        this.view = view;
        this.end = end;
        this.activity = activity;
        this.id = id;
        this.type = "view";

    }

    LinearLayoutWeightAni(Space space, float end, Activity activity, int id){
        this.space = space;
        this.end = end;
        this.activity = activity;
        this.id = id;
        this.type = "space";

    }

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

        if(this.type == "view"){
            //set LayoutParams parameters
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            //find the view by ID that you want to control
            //SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(R.id.swiperefresh);
            this.view = this.activity.findViewById(this.id);

            //Set the initial weight
            float start = ((LinearLayout.LayoutParams)this.view.getLayoutParams()).weight;

            //Calculate how much changes you require
            float mDelta = this.end - start;

            //set LayoutParams weight as you desire at given milliseconds
            lp.weight = (start + (mDelta * interpolatedTime));

            //the view finally gets what it deserve
            view.setLayoutParams(lp);

        }
        else if (this.type == "swipe") {

            //set LayoutParams parameters
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            //find the view by ID that you want to control
            //SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(R.id.swiperefresh);
            this.swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(this.id);

            //Set the initial weight
            float start = ((LinearLayout.LayoutParams)this.swipeRefreshLayout.getLayoutParams()).weight;

            //Calculate how much changes you require
            float mDelta = this.end - start;

            //set LayoutParams weight as you desire at given milliseconds
            lp.weight = (start + (mDelta * interpolatedTime));

            //the view finally gets what it deserve
            swipeRefreshLayout.setLayoutParams(lp);

        }
        else {

            //set LayoutParams parameters
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

            //find the view by ID that you want to control
            //SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)this.activity.findViewById(R.id.swiperefresh);
            this.space = (Space)this.activity.findViewById(this.id);

            //Set the initial weight
            float start = ((LinearLayout.LayoutParams)this.space.getLayoutParams()).weight;

            //Calculate how much changes you require
            float mDelta = this.end - start;

            //set LayoutParams weight as you desire at given milliseconds
            lp.weight = (start + (mDelta * interpolatedTime));

            //the view finally gets what it deserve
            this.space.setLayoutParams(lp);

        }

    }
}
