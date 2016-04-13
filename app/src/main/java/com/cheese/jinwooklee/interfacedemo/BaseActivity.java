package com.cheese.jinwooklee.interfacedemo;

import android.animation.StateListAnimator;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;

/**
 * Created by jinwooklee on 16-04-11.
 */
public class BaseActivity extends AppCompatActivity {

    private Toolbar mtoolbar;
    private AppBarLayout appBarLayout;

    protected Toolbar activateToolBar(){
        if(mtoolbar == null){
            mtoolbar = (Toolbar)findViewById(R.id.toolbar);
            mtoolbar.setTitle("");
            appBarLayout = (AppBarLayout)findViewById(R.id.toolbarApp);
            if(mtoolbar != null){
                setSupportActionBar(mtoolbar);
            }
        }
        return mtoolbar;
    }

    protected void hideToolBar(){
        appBarLayout.animate().translationY(-appBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        mtoolbar.animate().translationY(-mtoolbar.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        appBarLayout.setVisibility(View.GONE);
    }

    protected void showToolBar(){
        appBarLayout.setVisibility(View.VISIBLE);
        mtoolbar.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2)).start();
        appBarLayout.animate().translationY(0).setInterpolator(new AccelerateInterpolator(2)).start();
    }
}
